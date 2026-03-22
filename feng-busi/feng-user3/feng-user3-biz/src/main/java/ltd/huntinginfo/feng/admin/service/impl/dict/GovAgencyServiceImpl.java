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
import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;
import ltd.huntinginfo.feng.admin.mapper.dict.GovAgencyMapper;
import ltd.huntinginfo.feng.admin.service.dict.GovAgencyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

/**
 * 机关代码服务实现类
 *
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "dict:agency")
public class GovAgencyServiceImpl 
    extends ServiceImpl<GovAgencyMapper, GovAgency> 
    implements GovAgencyService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ALL_CACHE_KEY = "all";
    private static final String TREE_CACHE_KEY = "tree";

    @Override
    @Cacheable(key = "#code")
    public GovAgency getByCode(String code) {
        try {
            QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
            wrapper.eq("code", code);
            return getOne(wrapper);
        } catch (Exception e) {
            log.error("获取机构信息失败，code: {}", code, e);
            return null;
        }
    }

    @Override
    @Cacheable(key = "'parent:' + #parentCode")
    public List<GovAgency> getByParentCode(String parentCode) {
        QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_code", parentCode)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = "'level:' + #level")
    public List<GovAgency> getByLevel(Integer level) {
        QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
        wrapper.eq("level", level)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = TREE_CACHE_KEY)
    public List<GovAgency> getAgencyTree() {
        List<GovAgency> allAgencies = this.getAllValidItems();
        Map<String, List<GovAgency>> agencyMap = allAgencies.stream()
            .collect(Collectors.groupingBy(agency -> 
                agency.getParentCode() == null ? "root" : agency.getParentCode()));
        
        List<GovAgency> rootAgencies = agencyMap.get("root");
        if (rootAgencies != null) {
            rootAgencies.forEach(root -> buildTree(root, agencyMap));
            return rootAgencies;
        }
        return Collections.emptyList();
    }

    private void buildTree(GovAgency parent, Map<String, List<GovAgency>> agencyMap) {
        List<GovAgency> children = agencyMap.get(parent.getCode());
        if (children != null) {
            parent.setChildren(children);
            children.forEach(child -> buildTree(child, agencyMap));
        }
    }

    @Override
    @Cacheable(key = ALL_CACHE_KEY)
    public List<GovAgency> getAllValidItems() {
        QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }    

    @Override
    public List<GovAgency> getDescendantList(String id) {
        // 根据 ID 获取机构信息
        GovAgency agency = this.getById(id);
        if (agency == null) {
            return Collections.emptyList();
        }

        // 获取整棵机构树（根节点列表，包含完整的 children 结构）
        List<GovAgency> rootAgencies = this.getAgencyTree();

        // 从树中查找 code 匹配的目标节点
        GovAgency target = findNodeByCode(rootAgencies, agency.getCode());
        if (target == null) {
            return Collections.emptyList();
        }

        // 收集目标节点及其所有子孙节点
        List<GovAgency> descendants = new ArrayList<>();
        collectDescendants(target, descendants);
        return descendants;
    }

    /**
     * 递归查找树中指定 code 的节点
     * @param nodes 当前层级的节点列表
     * @param code  目标 code
     * @return 匹配的节点，若未找到返回 null
     */
    private GovAgency findNodeByCode(List<GovAgency> nodes, String code) {
        if (nodes == null) return null;
        for (GovAgency node : nodes) {
            if (node.getCode().equals(code)) {
                return node;
            }
            if (node.getChildren() != null) {
                GovAgency found = findNodeByCode(node.getChildren(), code);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * 递归收集节点及其所有子孙节点
     * @param node   当前节点
     * @param result 收集结果的列表
     */
    private void collectDescendants(GovAgency node, List<GovAgency> result) {
        result.add(node);
        if (node.getChildren() != null) {
            for (GovAgency child : node.getChildren()) {
                collectDescendants(child, result);
            }
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
        String lockKey = "lock:dict:agency:init";
        try {
            // 尝试获取分布式锁
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(locked)) {
                log.info("开始预热机构字典缓存...");
                
                // 1. 触发全量列表缓存（对应 @Cacheable(key = ALL_CACHE_KEY)）
                List<GovAgency> allAgencies = this.getAllValidItems();  // 实际执行查询并存入缓存
                
                // 2. 触发树形结构缓存（对应 @Cacheable(key = TREE_CACHE_KEY)）
                if (!CollectionUtils.isEmpty(allAgencies)) {
                    this.getAgencyTree();  // 内部基于 allAgencies 构建树，结果自动缓存
                }
                
                // 注意：不预热单个机构、按父级/级别列表，让它们按需加载
                // 如需预热，可考虑使用 CacheManager 手动填充（避免再次查询），但会增加复杂度
                
                log.info("机构字典缓存预热完成");
            }
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
}