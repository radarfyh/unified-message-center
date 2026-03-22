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
package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTopic;
import ltd.huntinginfo.feng.center.mapper.UmpMsgTopicMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgTopicService;
import ltd.huntinginfo.feng.center.api.dto.TopicQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TopicDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TopicPageVO;
import ltd.huntinginfo.feng.center.api.vo.TopicStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息主题表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgTopicServiceImpl extends ServiceImpl<UmpMsgTopicMapper, UmpMsgTopic> implements UmpMsgTopicService {

    private final UmpMsgTopicMapper umpMsgTopicMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTopic(String topicCode, String topicName, String topicType,
                             String description, String defaultMsgType,
                             Integer defaultPriority, Map<String, Object> routingRules,
                             Integer maxSubscribers) {
        if (!StringUtils.hasText(topicCode) || !StringUtils.hasText(topicName) || 
            !StringUtils.hasText(topicType)) {
            throw new IllegalArgumentException("主题代码、主题名称和主题类型不能为空");
        }

        // 检查主题代码是否已存在
        UmpMsgTopic existing = umpMsgTopicMapper.selectByTopicCode(topicCode);
        if (BeanUtil.isNotEmpty(existing)) {
            log.warn("主题已存在，主题代码: {}", topicCode);
            throw new RuntimeException("主题代码已存在");
        }

        // 创建主题
        UmpMsgTopic topic = new UmpMsgTopic();
        topic.setTopicCode(topicCode);
        topic.setTopicName(topicName);
        topic.setTopicType(topicType);
        topic.setDescription(description);
        topic.setDefaultMsgType(defaultMsgType);
        topic.setDefaultPriority(defaultPriority != null ? defaultPriority : 3);
        topic.setRoutingRules(routingRules);
        topic.setSubscriberCount(0);
        topic.setMaxSubscribers(maxSubscribers != null ? maxSubscribers : 1000);
        topic.setStatus(1); // 默认启用

        if (save(topic)) {
            log.info("主题创建成功，主题代码: {}, 主题名称: {}", topicCode, topicName);
            return topic.getId();
        } else {
            log.error("主题创建失败，主题代码: {}", topicCode);
            throw new RuntimeException("主题创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTopic(String topicId, String topicName, String description,
                              String defaultMsgType, Integer defaultPriority,
                              Map<String, Object> routingRules, Integer maxSubscribers,
                              Integer status) {
        if (!StringUtils.hasText(topicId)) {
            throw new IllegalArgumentException("主题ID不能为空");
        }

        UmpMsgTopic topic = getById(topicId);
        if (BeanUtil.isEmpty(topic)) {
            log.warn("主题不存在，主题ID: {}", topicId);
            return false;
        }

        if (StringUtils.hasText(topicName)) {
            topic.setTopicName(topicName);
        }
        if (description != null) {
            topic.setDescription(description);
        }
        if (StringUtils.hasText(defaultMsgType)) {
            topic.setDefaultMsgType(defaultMsgType);
        }
        if (defaultPriority != null) {
            topic.setDefaultPriority(defaultPriority);
        }
        if (routingRules != null) {
            topic.setRoutingRules(routingRules);
        }
        if (maxSubscribers != null) {
            topic.setMaxSubscribers(maxSubscribers);
        }
        if (status != null) {
            topic.setStatus(status);
        }

        boolean success = updateById(topic);
        if (success) {
            log.info("主题更新成功，主题ID: {}, 主题代码: {}", topicId, topic.getTopicCode());
        }
        
        return success;
    }

    @Override
    public TopicDetailVO getTopicByCode(String topicCode) {
        if (!StringUtils.hasText(topicCode)) {
            throw new IllegalArgumentException("主题代码不能为空");
        }

        UmpMsgTopic topic = umpMsgTopicMapper.selectByTopicCode(topicCode);
        if (BeanUtil.isEmpty(topic)) {
            log.warn("主题不存在，主题代码: {}", topicCode);
            return null;
        }

        return convertToDetailVO(topic);
    }

    @Override
    public Page<TopicPageVO> queryTopicPage(TopicQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgTopic::getDelFlag, 0); // 只查询未删除的记录

        if (StringUtils.hasText(queryDTO.getTopicName())) {
            queryWrapper.like(UmpMsgTopic::getTopicName, queryDTO.getTopicName());
        }
        
        if (StringUtils.hasText(queryDTO.getTopicType())) {
            queryWrapper.eq(UmpMsgTopic::getTopicType, queryDTO.getTopicType());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpMsgTopic::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getTopicCode())) {
            queryWrapper.eq(UmpMsgTopic::getTopicCode, queryDTO.getTopicCode());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgTopic::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgTopic> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgTopic> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<TopicPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<TopicPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<TopicDetailVO> getAvailableTopics() {
        List<UmpMsgTopic> topics = umpMsgTopicMapper.selectAvailableTopics();
        return topics.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableTopic(String topicId) {
        return updateTopicStatus(topicId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableTopic(String topicId) {
        return updateTopicStatus(topicId, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchEnableTopics(List<String> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return 0;
        }

        int updatedCount = umpMsgTopicMapper.batchUpdateStatus(topicIds, 1);
        if (updatedCount > 0) {
            log.info("批量启用主题成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDisableTopics(List<String> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return 0;
        }

        int updatedCount = umpMsgTopicMapper.batchUpdateStatus(topicIds, 0);
        if (updatedCount > 0) {
            log.info("批量禁用主题成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateTopicSubscriberCount(String topicId, int increment) {
        if (!StringUtils.hasText(topicId)) {
            throw new IllegalArgumentException("主题ID不能为空");
        }

        UmpMsgTopic topic = getById(topicId);
        if (BeanUtil.isEmpty(topic)) {
            log.warn("主题不存在，主题ID: {}", topicId);
            return null;
        }

        int newSubscriberCount = topic.getSubscriberCount() + increment;
        if (newSubscriberCount < 0) {
            newSubscriberCount = 0;
        }

        int updated = umpMsgTopicMapper.updateSubscriberCount(topicId, newSubscriberCount);
        if (updated > 0) {
            log.debug("主题订阅者数量更新，主题ID: {}, 原数量: {}, 增量: {}, 新数量: {}", 
                    topicId, topic.getSubscriberCount(), increment, newSubscriberCount);
            return newSubscriberCount;
        }
        
        return topic.getSubscriberCount();
    }

    @Override
    public boolean isTopicAvailable(String topicCode) {
        if (!StringUtils.hasText(topicCode)) {
            return false;
        }

        UmpMsgTopic topic = umpMsgTopicMapper.selectByTopicCode(topicCode);
        return BeanUtil.isNotEmpty(topic) && topic.getStatus() == 1;
    }

    @Override
    public TopicStatisticsVO getTopicStatistics() {
        Map<String, Object> statsMap = umpMsgTopicMapper.selectTopicStatistics();
        
        TopicStatisticsVO statisticsVO = new TopicStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setSystemCount(((Number) statsMap.getOrDefault("system_count", 0)).longValue());
            statisticsVO.setCustomCount(((Number) statsMap.getOrDefault("custom_count", 0)).longValue());
            statisticsVO.setEnabledCount(((Number) statsMap.getOrDefault("enabled_count", 0)).longValue());
            statisticsVO.setDisabledCount(((Number) statsMap.getOrDefault("disabled_count", 0)).longValue());
            statisticsVO.setTotalSubscribers(((Number) statsMap.getOrDefault("total_subscribers", 0)).longValue());
            statisticsVO.setAvgSubscribers(((Number) statsMap.getOrDefault("avg_subscribers", 0)).doubleValue());
            
            // 计算启用率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setEnableRate((double) statisticsVO.getEnabledCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTopic(String topicId) {
        if (!StringUtils.hasText(topicId)) {
            throw new IllegalArgumentException("主题ID不能为空");
        }
        
        boolean success = this.removeById(topicId);
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteTopics(List<String> topicIds) {
        if (CollectionUtils.isEmpty(topicIds)) {
            return false;
        }

        boolean success = this.removeByIds(topicIds);
        
        return success;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgTopic> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTopic::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTopic::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTopic::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTopic::getUpdateTime);
                }
                break;
            case "topicName":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTopic::getTopicName);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTopic::getTopicName);
                }
                break;
            case "subscriberCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTopic::getSubscriberCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTopic::getSubscriberCount);
                }
                break;
            case "defaultPriority":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgTopic::getDefaultPriority);
                } else {
                    queryWrapper.orderByDesc(UmpMsgTopic::getDefaultPriority);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgTopic::getCreateTime);
                break;
        }
    }

    private boolean updateTopicStatus(String topicId, Integer status) {
        if (!StringUtils.hasText(topicId) || status == null) {
            throw new IllegalArgumentException("主题ID和状态不能为空");
        }

        UmpMsgTopic topic = getById(topicId);
        if (BeanUtil.isEmpty(topic)) {
            log.warn("主题不存在，主题ID: {}", topicId);
            return false;
        }

        if (topic.getStatus().equals(status)) {
            log.debug("主题状态未改变，主题ID: {}, 状态: {}", topicId, status);
            return true;
        }

        topic.setStatus(status);
        
        boolean success = updateById(topic);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("主题{}成功，主题ID: {}, 主题代码: {}", action, topicId, topic.getTopicCode());
        }
        
        return success;
    }

    private TopicDetailVO convertToDetailVO(UmpMsgTopic topic) {
        TopicDetailVO vo = new TopicDetailVO();
        BeanUtils.copyProperties(topic, vo);
        
        // 计算订阅率
        if (topic.getMaxSubscribers() != null && topic.getMaxSubscribers() > 0) {
            vo.setSubscribeRate((double) topic.getSubscriberCount() / topic.getMaxSubscribers() * 100);
        }
        
        return vo;
    }

    private TopicPageVO convertToPageVO(UmpMsgTopic topic) {
        TopicPageVO vo = new TopicPageVO();
        BeanUtils.copyProperties(topic, vo);
        
        // 计算订阅率
        if (topic.getMaxSubscribers() != null && topic.getMaxSubscribers() > 0) {
            vo.setSubscribeRate((double) topic.getSubscriberCount() / topic.getMaxSubscribers() * 100);
        }
        
        return vo;
    }
}