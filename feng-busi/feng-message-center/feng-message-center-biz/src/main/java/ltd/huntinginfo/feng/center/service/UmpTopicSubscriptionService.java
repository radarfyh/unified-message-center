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
package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpTopicSubscription;
import ltd.huntinginfo.feng.center.api.dto.SubscriptionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionPageVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 主题订阅表服务接口
 */
public interface UmpTopicSubscriptionService extends IService<UmpTopicSubscription> {

    /**
     * 创建订阅
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @param subscriptionConfig 订阅配置
     * @param callbackUrl 回调地址
     * @param pushMode 推送方式
     * @return 订阅ID
     */
    String createSubscription(String topicCode, String appKey,
                            Map<String, Object> subscriptionConfig,
                            String callbackUrl, String pushMode);

    /**
     * 更新订阅
     *
     * @param subscriptionId 订阅ID
     * @param subscriptionConfig 订阅配置
     * @param callbackUrl 回调地址
     * @param pushMode 推送方式
     * @return 是否成功
     */
    boolean updateSubscription(String subscriptionId,
                             Map<String, Object> subscriptionConfig,
                             String callbackUrl, String pushMode);

    /**
     * 订阅主题
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @param subscriptionConfig 订阅配置
     * @param callbackUrl 回调地址
     * @param pushMode 推送方式
     * @return 是否成功
     */
    boolean subscribeTopic(String topicCode, String appKey,
                          Map<String, Object> subscriptionConfig,
                          String callbackUrl, String pushMode);

    /**
     * 取消订阅
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @return 是否成功
     */
    boolean unsubscribeTopic(String topicCode, String appKey);

    /**
     * 根据主题代码和应用标识查询订阅
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @return 订阅详情VO
     */
    SubscriptionDetailVO getSubscription(String topicCode, String appKey);

    /**
     * 分页查询订阅
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<SubscriptionPageVO> querySubscriptionPage(SubscriptionQueryDTO queryDTO);

    /**
     * 根据主题代码查询订阅列表
     *
     * @param topicCode 主题代码
     * @param status 状态（可选）
     * @return 订阅列表
     */
    List<SubscriptionDetailVO> getSubscriptionsByTopic(String topicCode, Integer status);

    /**
     * 根据应用标识查询订阅列表
     *
     * @param appKey 应用标识
     * @param status 状态（可选）
     * @return 订阅列表
     */
    List<SubscriptionDetailVO> getSubscriptionsByApp(String appKey, Integer status);

    /**
     * 激活订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    boolean activateSubscription(String subscriptionId);

    /**
     * 停用订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    boolean deactivateSubscription(String subscriptionId);

    /**
     * 批量激活订阅
     *
     * @param subscriptionIds 订阅ID列表
     * @return 成功激活数量
     */
    int batchActivateSubscriptions(List<String> subscriptionIds);

    /**
     * 批量停用订阅
     *
     * @param subscriptionIds 订阅ID列表
     * @return 成功停用数量
     */
    int batchDeactivateSubscriptions(List<String> subscriptionIds);

    /**
     * 更新订阅统计信息
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @param increment 增量（正数增加，负数减少）
     * @param lastMessageTime 最后消息时间
     * @return 更新后的消息数量
     */
    Integer updateSubscriptionStats(String topicCode, String appKey,
                                  int increment, LocalDateTime lastMessageTime);

    /**
     * 检查订阅是否存在
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @param activeOnly 是否只检查活跃订阅
     * @return 是否存在
     */
    Boolean existsSubscription(String topicCode, String appKey, boolean activeOnly);

    /**
     * 获取订阅统计信息
     *
     * @param topicCode 主题代码（可选）
     * @param appKey 应用标识（可选）
     * @return 统计信息VO
     */
    SubscriptionStatisticsVO getSubscriptionStatistics(String topicCode, String appKey);

    /**
     * 获取活跃订阅数量
     *
     * @param topicCode 主题代码
     * @return 活跃订阅数量
     */
    Integer countActiveSubscriptions(String topicCode);

    /**
     * 逻辑删除订阅
     *
     * @param subscriptionId 订阅ID
     * @return 是否成功
     */
    Boolean deleteSubscription(String subscriptionId);

    /**
     * 批量删除订阅
     *
     * @param subscriptionIds 订阅ID列表
     * @return 成功删除数量
     */
    Boolean batchDeleteSubscriptions(List<String> subscriptionIds);
}