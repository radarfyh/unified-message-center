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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.entity.dict.DictFootprintPosition;
import ltd.huntinginfo.feng.admin.mapper.dict.DictFootprintPositionMapper;
import ltd.huntinginfo.feng.admin.service.dict.DictFootprintPositionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

/**
 * 足迹部位代码服务实现类
 *
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "dict:footprint_position")
public class DictFootprintPositionServiceImpl 
    extends ServiceImpl<DictFootprintPositionMapper, DictFootprintPosition> 
    implements DictFootprintPositionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ALL_CACHE_KEY = "all";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    @Cacheable(key = "#code", unless = "#result == null")
    public DictFootprintPosition getByCode(String code) {
        try {
            QueryWrapper<DictFootprintPosition> wrapper = new QueryWrapper<>();
            wrapper.eq("code", code);
            return getOne(wrapper);
        } catch (Exception e) {
            log.error("获取足迹部位失败，code: {}", code, e);
            return null;
        }
    }

    @Override
    @Cacheable(key = ALL_CACHE_KEY, unless = "#result == null || #result.isEmpty()")
    public List<DictFootprintPosition> getAllValidItems() {
        QueryWrapper<DictFootprintPosition> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void refreshCache() {
        log.info("刷新足迹部位字典缓存");
    }

    @PostConstruct
    public void initCache() {
        String lockKey = "lock:dict:footprint_position:init";
        try {
            Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(locked)) {
                List<DictFootprintPosition> allItems = this.getAllValidItems();
                if (!CollectionUtils.isEmpty(allItems)) {
                    // 缓存所有数据
                    redisTemplate.opsForValue().set(
                        "dict:footprint_position:all", 
                        allItems,
                        CACHE_EXPIRE_HOURS, 
                        TimeUnit.HOURS
                    );
                    
                    // 缓存每个code的单独数据
                    allItems.forEach(item -> {
                        redisTemplate.opsForValue().set(
                            "dict:footprint_position:" + item.getCode(),
                            item,
                            CACHE_EXPIRE_HOURS,
                            TimeUnit.HOURS
                        );
                    });
                }
            }
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
