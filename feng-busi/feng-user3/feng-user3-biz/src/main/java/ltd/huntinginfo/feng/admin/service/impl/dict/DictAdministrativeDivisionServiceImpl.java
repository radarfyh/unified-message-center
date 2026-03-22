/*
 *      Copyright (c) 2018-2025, radarfyh(Edison.Feng) All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: radarfyh(Edison.Feng)
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */

package ltd.huntinginfo.feng.admin.service.impl.dict;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.entity.dict.DictAdministrativeDivision;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.mapper.dict.DictAdministrativeDivisionMapper;
import ltd.huntinginfo.feng.admin.service.dict.DictAdministrativeDivisionService;
import ltd.huntinginfo.feng.admin.service.dict.UniqueUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

/**
 * 行政区划代码服务实现类
 *
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "dict:administrative_division")
public class DictAdministrativeDivisionServiceImpl 
    extends ServiceImpl<DictAdministrativeDivisionMapper, DictAdministrativeDivision> 
    implements DictAdministrativeDivisionService {
	
	@Autowired
	private UniqueUserService uniqueUserService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ALL_CACHE_KEY = "all";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    @Cacheable(key = "#code")
    public DictAdministrativeDivision getByCode(String code) {
        try {
            QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
            wrapper.eq("code", code);
            return getOne(wrapper);
        } catch (Exception e) {
            log.error("获取行政区划失败，code: {}", code, e);
            return null;
        }
    }

    @Override
    @Cacheable(key = "'parent:' + #parentCode")
    public List<DictAdministrativeDivision> getByParentCode(String parentCode) {
        QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_code", parentCode)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = "'level:' + #level")
    public List<DictAdministrativeDivision> getByLevel(Integer level) {
        QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
        wrapper.eq("level", level)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = ALL_CACHE_KEY)
    public List<DictAdministrativeDivision> getAllValidItems() {
        QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }
    
    @Override
    @Cacheable(key = "'tree'")
    public List<DictAdministrativeDivision> getDivisionTree() {
        List<DictAdministrativeDivision> all = this.getAllValidItems();

        if (CollectionUtils.isEmpty(all)) {
            return List.of();
        }

        // 按 parentCode 分组
        Map<String, List<DictAdministrativeDivision>> groupMap =
                all.stream().collect(
                    Collectors.groupingBy(
                        d -> d.getParentCode() == null ? "ROOT" : d.getParentCode()
                    )
                );

        // 省级作为根节点（level = 1）
        List<DictAdministrativeDivision> roots =
                all.stream()
                   .filter(d -> d.getLevel() != null && d.getLevel() == 1)
                   .collect(Collectors.toList());

        roots.forEach(root -> buildTree(root, groupMap));
        return roots;
    }

    private void buildTree(
            DictAdministrativeDivision parent,
            Map<String, List<DictAdministrativeDivision>> groupMap) {

        List<DictAdministrativeDivision> children =
                groupMap.get(parent.getCode());

        if (!CollectionUtils.isEmpty(children)) {
            parent.setChildren(children);
            children.forEach(child -> buildTree(child, groupMap));
        }
    }


    @Override
    @CacheEvict(allEntries = true)
    public void refreshCache() {
        // 清空缓存后，下次查询会自动重新加载
    }

    /**
     * 初始化缓存数据
     */
    @PostConstruct
    public void initCache() {
        String lockKey = "lock:dict:administrative_division:init";
        try {
            // 尝试获取分布式锁
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(locked)) {
                List<DictAdministrativeDivision> allDivisions = this.getAllValidItems();
                if (!CollectionUtils.isEmpty(allDivisions)) {
                    // 缓存所有数据
                    redisTemplate.opsForValue().set(
                        "dict:administrative_division:all", 
                        allDivisions,
                        CACHE_EXPIRE_HOURS, 
                        TimeUnit.HOURS
                    );
                    
                    // 缓存每个code的单独数据
                    allDivisions.forEach(division -> {
                        redisTemplate.opsForValue().set(
                            "dict:administrative_division:" + division.getCode(),
                            division,
                            CACHE_EXPIRE_HOURS,
                            TimeUnit.HOURS
                        );
                        
                        // 缓存按父级分组的数据
                        if (division.getParentCode() != null) {
                            redisTemplate.opsForList().rightPush(
                                "dict:administrative_division:parent:" + division.getParentCode(),
                                division
                            );
                        }
                        
                        // 缓存按级别分组的数据
                        redisTemplate.opsForList().rightPush(
                            "dict:administrative_division:level:" + division.getLevel(),
                            division
                        );
                    });
                }
            }
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

	@Override
	public List<Map<String, Object>> listAreasByCodes(List<String> areaCodes) {
		QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
		wrapper.in("code", areaCodes)
		       .orderByAsc("code");

        List<DictAdministrativeDivision> ret = baseMapper.selectList(wrapper);
        return ret.stream()
	            .map(this::convertToMap)
	            .toList();
	}
	
	private Map<String, Object> convertToMap(DictAdministrativeDivision area) {
	    Map<String, Object> map = new HashMap<>(8);
	    map.put("id", area.getId());
	    map.put("name", area.getName());
	    map.put("code", area.getCode());
	    map.put("level", area.getLevel());
	    map.put("parentCode", area.getParentCode());
	    map.put("createTime", area.getCreateTime());
	    map.put("updateTime", area.getUpdateTime());
	    return map;
	}
	
	@Override
	public List<Map<String, Object>> listUsersByArea(Map<String, Object> query) {
	    String areaCode = query.get("areaCode").toString();
	    List<UniqueUser> users = uniqueUserService.list(
	        Wrappers.<UniqueUser>lambdaQuery()
	                .eq(UniqueUser::getDivisionCode, areaCode)
	    );
	    return users.stream().map(user -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", user.getId());
	        map.put("name", user.getName());
	        map.put("userId", user.getSysUserId());
	        map.put("uniqueOrgCode", user.getUniqueOrgCode());
	        return map;
	    }).toList();
	}
}