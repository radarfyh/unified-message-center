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
import ltd.huntinginfo.feng.center.api.entity.UmpTopicSubscription;
import ltd.huntinginfo.feng.center.mapper.UmpTopicSubscriptionMapper;
import ltd.huntinginfo.feng.center.service.UmpTopicSubscriptionService;
import ltd.huntinginfo.feng.center.api.dto.SubscriptionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionPageVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 主题订阅表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpTopicSubscriptionServiceImpl extends ServiceImpl<UmpTopicSubscriptionMapper, UmpTopicSubscription> implements UmpTopicSubscriptionService {

    private final UmpTopicSubscriptionMapper umpTopicSubscriptionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createSubscription(String topicCode, String appKey,
                                   Map<String, Object> callbackConfig,
                                   String callbackUrl, String pushMode) {
        if (!StringUtils.hasText(topicCode) || !StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("主题代码和应用标识不能为空");
        }

        // 检查订阅是否已存在
        UmpTopicSubscription existing = umpTopicSubscriptionMapper.selectByTopicAndApp(topicCode, appKey);
        if (existing != null) {
            log.warn("订阅已存在，主题代码: {}, 应用标识: {}", topicCode, appKey);
            throw new RuntimeException("订阅已存在");
        }

        // 创建订阅
        UmpTopicSubscription subscription = new UmpTopicSubscription();
        subscription.setTopicCode(topicCode);
        subscription.setAppKey(appKey);
        subscription.setCallbackConfig(callbackConfig);
        subscription.setCallbackUrl(callbackUrl);
        subscription.setPushMode(StringUtils.hasText(pushMode) ? pushMode : "PUSH");
        subscription.setStatus(1); // 默认已订阅
        subscription.setSubscribeTime(LocalDateTime.now());
        subscription.setMessageCount(0);

        if (save(subscription)) {
            log.info("订阅创建成功，主题代码: {}, 应用标识: {}", topicCode, appKey);
            return subscription.getId();
        } else {
            log.error("订阅创建失败，主题代码: {}, 应用标识: {}", topicCode, appKey);
            throw new RuntimeException("订阅创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSubscription(String subscriptionId,
                                    Map<String, Object> callbackConfig,
                                    String callbackUrl, String pushMode) {
        if (!StringUtils.hasText(subscriptionId)) {
            throw new IllegalArgumentException("订阅ID不能为空");
        }

        UmpTopicSubscription subscription = getById(subscriptionId);
        if (subscription == null) {
            log.warn("订阅不存在，订阅ID: {}", subscriptionId);
            return false;
        }

        boolean updated = false;
        if (callbackConfig != null) {
            subscription.setCallbackConfig(callbackConfig);
            updated = true;
        }
        if (StringUtils.hasText(callbackUrl)) {
            subscription.setCallbackUrl(callbackUrl);
            updated = true;
        }
        if (StringUtils.hasText(pushMode)) {
            subscription.setPushMode(pushMode);
            updated = true;
        }

        if (updated) {
            boolean success = updateById(subscription);
            if (success) {
                log.info("订阅更新成功，订阅ID: {}, 主题代码: {}", subscriptionId, subscription.getTopicCode());
            }
            return success;
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean subscribeTopic(String topicCode, String appKey,
                                 Map<String, Object> callbackConfig,
                                 String callbackUrl, String pushMode) {
        // 检查是否已订阅
        UmpTopicSubscription existing = umpTopicSubscriptionMapper.selectByTopicAndApp(topicCode, appKey);
        if (existing != null) {
            // 如果已存在但已取消订阅，重新激活
            if (existing.getStatus() == 0) {
                existing.setStatus(1);
                existing.setSubscribeTime(LocalDateTime.now());
                existing.setUnsubscribeTime(null);
                if (callbackConfig != null) {
                    existing.setCallbackConfig(callbackConfig);
                }
                if (StringUtils.hasText(callbackUrl)) {
                    existing.setCallbackUrl(callbackUrl);
                }
                if (StringUtils.hasText(pushMode)) {
                    existing.setPushMode(pushMode);
                }
                
                boolean success = updateById(existing);
                if (success) {
                    log.info("重新订阅成功，主题代码: {}, 应用标识: {}", topicCode, appKey);
                }
                return success;
            } else {
                // 已经是活跃订阅，更新配置
                return updateSubscription(existing.getId(), callbackConfig, callbackUrl, pushMode);
            }
        }

        // 创建新订阅
        try {
            createSubscription(topicCode, appKey, callbackConfig, callbackUrl, pushMode);
            return true;
        } catch (Exception e) {
            log.error("订阅主题失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unsubscribeTopic(String topicCode, String appKey) {
        if (!StringUtils.hasText(topicCode) || !StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("主题代码和应用标识不能为空");
        }

        UmpTopicSubscription subscription = umpTopicSubscriptionMapper.selectByTopicAndApp(topicCode, appKey);
        if (subscription == null) {
            log.warn("订阅不存在，主题代码: {}, 应用标识: {}", topicCode, appKey);
            return false;
        }

        if (subscription.getStatus() == 0) {
            log.debug("订阅已取消，无需重复操作，主题代码: {}, 应用标识: {}", topicCode, appKey);
            return true;
        }

        subscription.setStatus(0);
        subscription.setUnsubscribeTime(LocalDateTime.now());
        
        boolean success = updateById(subscription);
        if (success) {
            log.info("取消订阅成功，主题代码: {}, 应用标识: {}", topicCode, appKey);
        }
        
        return success;
    }

    @Override
    public SubscriptionDetailVO getSubscription(String topicCode, String appKey) {
        if (!StringUtils.hasText(topicCode) || !StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("主题代码和应用标识不能为空");
        }

        UmpTopicSubscription subscription = umpTopicSubscriptionMapper.selectByTopicAndApp(topicCode, appKey);
        if (subscription == null) {
            log.warn("订阅不存在，主题代码: {}, 应用标识: {}", topicCode, appKey);
            return null;
        }

        return convertToDetailVO(subscription);
    }

    @Override
    public Page<SubscriptionPageVO> querySubscriptionPage(SubscriptionQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpTopicSubscription> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpTopicSubscription::getDelFlag, 0); // 只查询未删除的记录

        if (StringUtils.hasText(queryDTO.getTopicCode())) {
            queryWrapper.eq(UmpTopicSubscription::getTopicCode, queryDTO.getTopicCode());
        }
        
        if (StringUtils.hasText(queryDTO.getAppKey())) {
            queryWrapper.eq(UmpTopicSubscription::getAppKey, queryDTO.getAppKey());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpTopicSubscription::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getPushMode())) {
            queryWrapper.eq(UmpTopicSubscription::getPushMode, queryDTO.getPushMode());
        }

        // 时间范围查询
        if (queryDTO.getSubscribeTimeStart() != null) {
            queryWrapper.ge(UmpTopicSubscription::getSubscribeTime, queryDTO.getSubscribeTimeStart());
        }
        if (queryDTO.getSubscribeTimeEnd() != null) {
            queryWrapper.le(UmpTopicSubscription::getSubscribeTime, queryDTO.getSubscribeTimeEnd());
        }
        if (queryDTO.getLastMessageTimeStart() != null) {
            queryWrapper.ge(UmpTopicSubscription::getLastMessageTime, queryDTO.getLastMessageTimeStart());
        }
        if (queryDTO.getLastMessageTimeEnd() != null) {
            queryWrapper.le(UmpTopicSubscription::getLastMessageTime, queryDTO.getLastMessageTimeEnd());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpTopicSubscription::getSubscribeTime);
        }

        // 执行分页查询
        Page<UmpTopicSubscription> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpTopicSubscription> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<SubscriptionPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<SubscriptionPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<SubscriptionDetailVO> getSubscriptionsByTopic(String topicCode, Integer status) {
        if (!StringUtils.hasText(topicCode)) {
            throw new IllegalArgumentException("主题代码不能为空");
        }

        List<UmpTopicSubscription> subscriptions = umpTopicSubscriptionMapper.selectByTopicCode(topicCode, status);
        return subscriptions.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionDetailVO> getSubscriptionsByApp(String appKey, Integer status) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        List<UmpTopicSubscription> subscriptions = umpTopicSubscriptionMapper.selectByAppKey(appKey, status);
        return subscriptions.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateSubscription(String subscriptionId) {
        return updateSubscriptionStatus(subscriptionId, 1, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deactivateSubscription(String subscriptionId) {
        return updateSubscriptionStatus(subscriptionId, 0, LocalDateTime.now());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchActivateSubscriptions(List<String> subscriptionIds) {
        if (CollectionUtils.isEmpty(subscriptionIds)) {
            return 0;
        }

        int updatedCount = umpTopicSubscriptionMapper.batchUpdateStatus(subscriptionIds, 1, null);
        if (updatedCount > 0) {
            log.info("批量激活订阅成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeactivateSubscriptions(List<String> subscriptionIds) {
        if (CollectionUtils.isEmpty(subscriptionIds)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = umpTopicSubscriptionMapper.batchUpdateStatus(subscriptionIds, 0, now);
        if (updatedCount > 0) {
            log.info("批量停用订阅成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSubscriptionStats(String topicCode, String appKey,
                                         int increment, LocalDateTime lastMessageTime) {
        if (!StringUtils.hasText(topicCode) || !StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("主题代码和应用标识不能为空");
        }

        UmpTopicSubscription subscription = umpTopicSubscriptionMapper.selectByTopicAndApp(topicCode, appKey);
        if (subscription == null) {
            log.warn("订阅不存在，主题代码: {}, 应用标识: {}", topicCode, appKey);
            return null;
        }

        int newMessageCount = subscription.getMessageCount() + increment;
        if (newMessageCount < 0) {
            newMessageCount = 0;
        }

        // 更新最后消息时间（如果有新消息）
        LocalDateTime finalLastMessageTime = lastMessageTime;
        if (increment > 0 && lastMessageTime == null) {
            finalLastMessageTime = LocalDateTime.now();
        }

        int updated = umpTopicSubscriptionMapper.updateSubscriptionStats(
                subscription.getId(), newMessageCount, finalLastMessageTime);
        
        if (updated > 0) {
//            log.debug("订阅统计更新，主题代码: {}, 应用标识: {}, 原数量: {}, 增量: {}, 新数量: {}", 
//                    topicCode, appKey, subscription.getMessageCount(), increment, newMessageCount);
            return newMessageCount;
        }
        log.error("更新订阅统计失败，订阅ID: {}", subscription.getId());
        
        return null;
    }

    @Override
    public Boolean existsSubscription(String topicCode, String appKey, boolean activeOnly) {
        if (!StringUtils.hasText(topicCode) || !StringUtils.hasText(appKey)) {
            return null;
        }

        Integer status = activeOnly ? 1 : null;
        return umpTopicSubscriptionMapper.existsSubscription(topicCode, appKey, status);
    }

    @Override
    public SubscriptionStatisticsVO getSubscriptionStatistics(String topicCode, String appKey) {
        Map<String, Object> statsMap = umpTopicSubscriptionMapper.selectSubscriptionStatistics(topicCode, appKey);
        
        SubscriptionStatisticsVO statisticsVO = new SubscriptionStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setActiveCount(((Number) statsMap.getOrDefault("active_count", 0)).longValue());
            statisticsVO.setInactiveCount(((Number) statsMap.getOrDefault("inactive_count", 0)).longValue());
            statisticsVO.setPushModeCount(((Number) statsMap.getOrDefault("push_mode_count", 0)).longValue());
            statisticsVO.setPollModeCount(((Number) statsMap.getOrDefault("poll_mode_count", 0)).longValue());
            statisticsVO.setTotalMessages(((Number) statsMap.getOrDefault("total_messages", 0)).longValue());
            statisticsVO.setAvgMessages(((Number) statsMap.getOrDefault("avg_messages", 0)).doubleValue());
            
            // 计算活跃率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setActiveRate((double) statisticsVO.getActiveCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public Integer countActiveSubscriptions(String topicCode) {
        if (!StringUtils.hasText(topicCode)) {
            return 0;
        }

        return umpTopicSubscriptionMapper.countActiveSubscriptions(topicCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSubscription(String subscriptionId) {
        if (!StringUtils.hasText(subscriptionId)) {
            throw new IllegalArgumentException("订阅ID不能为空");
        }

        boolean success = this.removeById(subscriptionId);
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteSubscriptions(List<String> subscriptionIds) {
        if (CollectionUtils.isEmpty(subscriptionIds)) {
            return false;
        }

        boolean success = this.removeByIds(subscriptionIds);
        
        return success;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpTopicSubscription> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "subscribeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpTopicSubscription::getSubscribeTime);
                } else {
                    queryWrapper.orderByDesc(UmpTopicSubscription::getSubscribeTime);
                }
                break;
            case "lastMessageTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpTopicSubscription::getLastMessageTime);
                } else {
                    queryWrapper.orderByDesc(UmpTopicSubscription::getLastMessageTime);
                }
                break;
            case "messageCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpTopicSubscription::getMessageCount);
                } else {
                    queryWrapper.orderByDesc(UmpTopicSubscription::getMessageCount);
                }
                break;
            case "topicCode":
                if (asc) {
                    queryWrapper.orderByAsc(UmpTopicSubscription::getTopicCode);
                } else {
                    queryWrapper.orderByDesc(UmpTopicSubscription::getTopicCode);
                }
                break;
            case "appKey":
                if (asc) {
                    queryWrapper.orderByAsc(UmpTopicSubscription::getAppKey);
                } else {
                    queryWrapper.orderByDesc(UmpTopicSubscription::getAppKey);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpTopicSubscription::getSubscribeTime);
                break;
        }
    }

    private boolean updateSubscriptionStatus(String subscriptionId, Integer status, LocalDateTime unsubscribeTime) {
        if (!StringUtils.hasText(subscriptionId) || status == null) {
            throw new IllegalArgumentException("订阅ID和状态不能为空");
        }

        UmpTopicSubscription subscription = getById(subscriptionId);
        if (subscription == null) {
            log.warn("订阅不存在，订阅ID: {}", subscriptionId);
            return false;
        }

        if (subscription.getStatus().equals(status)) {
            log.debug("订阅状态未改变，订阅ID: {}, 状态: {}", subscriptionId, status);
            return true;
        }

        subscription.setStatus(status);
        if (unsubscribeTime != null) {
            subscription.setUnsubscribeTime(unsubscribeTime);
        }
        
        boolean success = updateById(subscription);
        if (success) {
            String action = status == 1 ? "激活" : "停用";
            log.info("订阅{}成功，订阅ID: {}, 主题代码: {}, 应用标识: {}", 
                    action, subscriptionId, subscription.getTopicCode(), subscription.getAppKey());
        }
        
        return success;
    }

    private SubscriptionDetailVO convertToDetailVO(UmpTopicSubscription subscription) {
        SubscriptionDetailVO vo = new SubscriptionDetailVO();
        BeanUtils.copyProperties(subscription, vo);
        
        // 计算订阅时长（天）
        if (subscription.getSubscribeTime() != null) {
            long days = java.time.Duration.between(subscription.getSubscribeTime(), LocalDateTime.now()).toDays();
            vo.setSubscriptionDays(days);
        }
        
        return vo;
    }

    private SubscriptionPageVO convertToPageVO(UmpTopicSubscription subscription) {
        SubscriptionPageVO vo = new SubscriptionPageVO();
        BeanUtils.copyProperties(subscription, vo);
        
        // 计算订阅时长（天）
        if (subscription.getSubscribeTime() != null) {
            long days = java.time.Duration.between(subscription.getSubscribeTime(), LocalDateTime.now()).toDays();
            vo.setSubscriptionDays(days);
        }
        
        return vo;
    }
}