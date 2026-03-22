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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTopic;
import ltd.huntinginfo.feng.center.api.dto.TopicQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TopicDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TopicPageVO;
import ltd.huntinginfo.feng.center.api.vo.TopicStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 消息主题表服务接口
 */
public interface UmpMsgTopicService extends IService<UmpMsgTopic> {

    /**
     * 创建主题
     *
     * @param topicCode 主题代码
     * @param topicName 主题名称
     * @param topicType 主题类型
     * @param description 主题描述
     * @param defaultMsgType 默认消息类型
     * @param defaultPriority 默认优先级
     * @param routingRules 路由规则
     * @param maxSubscribers 最大订阅者数量
     * @return 主题ID
     */
    String createTopic(String topicCode, String topicName, String topicType,
                      String description, String defaultMsgType,
                      Integer defaultPriority, Map<String, Object> routingRules,
                      Integer maxSubscribers);

    /**
     * 更新主题
     *
     * @param topicId 主题ID
     * @param topicName 主题名称
     * @param description 主题描述
     * @param defaultMsgType 默认消息类型
     * @param defaultPriority 默认优先级
     * @param routingRules 路由规则
     * @param maxSubscribers 最大订阅者数量
     * @param status 状态
     * @return 是否成功
     */
    boolean updateTopic(String topicId, String topicName, String description,
                       String defaultMsgType, Integer defaultPriority,
                       Map<String, Object> routingRules, Integer maxSubscribers,
                       Integer status);

    /**
     * 根据主题代码查询主题
     *
     * @param topicCode 主题代码
     * @return 主题详情VO
     */
    TopicDetailVO getTopicByCode(String topicCode);

    /**
     * 分页查询主题
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<TopicPageVO> queryTopicPage(TopicQueryDTO queryDTO);

    /**
     * 查询可用主题列表
     *
     * @return 主题列表
     */
    List<TopicDetailVO> getAvailableTopics();

    /**
     * 启用主题
     *
     * @param topicId 主题ID
     * @return 是否成功
     */
    boolean enableTopic(String topicId);

    /**
     * 禁用主题
     *
     * @param topicId 主题ID
     * @return 是否成功
     */
    boolean disableTopic(String topicId);

    /**
     * 批量启用主题
     *
     * @param topicIds 主题ID列表
     * @return 成功启用数量
     */
    int batchEnableTopics(List<String> topicIds);

    /**
     * 批量禁用主题
     *
     * @param topicIds 主题ID列表
     * @return 成功禁用数量
     */
    int batchDisableTopics(List<String> topicIds);

    /**
     * 更新主题订阅者数量
     *
     * @param topicId 主题ID
     * @param increment 增量（正数增加，负数减少）
     * @return 更新后的订阅者数量
     */
    Integer updateTopicSubscriberCount(String topicId, int increment);

    /**
     * 检查主题是否可用
     *
     * @param topicCode 主题代码
     * @return 是否可用
     */
    boolean isTopicAvailable(String topicCode);

    /**
     * 获取主题统计信息
     *
     * @return 统计信息VO
     */
    TopicStatisticsVO getTopicStatistics();

    /**
     * 逻辑删除主题
     *
     * @param topicId 主题ID
     * @return 是否成功
     */
    Boolean deleteTopic(String topicId);

    /**
     * 批量删除主题
     *
     * @param topicIds 主题ID列表
     * @return 成功删除数量
     */
    Boolean batchDeleteTopics(List<String> topicIds);
}