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

package ltd.huntinginfo.feng.common.core.mq.consumer;

import java.util.Map;

import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;

/**
 * 消息消费者统一接口
 * <p>
 * 业务模块需实现该接口，并通过 {@code @Component} 注册为 Bean。
 * RabbitMQ / Kafka 模块通过监听器调用此接口方法，将消息传递给业务层。
 * </p>
 *
 * @author radarfyh
 * @since 2026-02-12
 */
public interface MqMessageConsumer {

    // ---------- 消息状态事件 ----------
	
    /**
     * 收到MESSAGE_RECEIVED队列/主题，调用MessageDistributionProcessor的方法处理：
     * 创建任务到ump_msg_queue，
     * 修改主表的状态为MESSAGE_DISTRIBUTING，
     * 发布新MQ消息到MESSAGE_DISTRIBUTING队列/主题
     */
    void handleMessageReceived(MqMessage<Map<String, Object>> message);
    
    /**
     * 收到MESSAGE_DISTRIBUTING队列/主题，调用MessageDistributionProcessor的方法处理
     * 解析receiverScope
     * 创建接收记录到收件箱（个人或者小于阈值的部门、组织、机构、区域），或者创建接收记录到广播信息筒（大于阈值的部门、组织、机构、区域）
     * 修改主表状态为MESSAGE_DISTRIBUTED
     * 发布新MQ消息到MESSAGE_DISTRIBUTED队列/主题
     * 失败时发布新MQ消息到MESSAGE_DIST_FAILED队列/主题
     */
	void handleMessageDistributeStart(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_DISTRIBUTED队列/主题, 调用MessagePushProcessor的方法处理
     * 解析receiverScope
     * 解析callbackConfig
     * 构造回调消息头
     * 构造回调消息体
     * 调用callbackUrl把消息发送给业务系统
     * 修改主表状态为MESSAGE_PUSHED（个人或者小于阈值的DEPT/ORG/AREA)或者MESSAGE_PULL(大于阈值的DEPT/ORG/AREA)
     * 不发布MQ新消息
     * 失败时发布新MQ消息到MESSAGE_PUSH_FAILED队列/主题
     */
    void handleMessageDistributed(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_DIST_FAILED队列/主题, 分发重试：
     * 不再直接调用handleMessageDistributeStart，而是由定时任务调度器执行
     */
	void handleMessageDistributeFailed(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_PUSHED队列/主题：
     * 不做任何操作（包括不发布MQ新消息）
     */
	void handleMessagePushed(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_PUSH_FAILED队列/主题, 推送重试：
     * 不再直接调用handleMessageDistributed，而是由定时任务调度器执行
     */
	void handleMessagePushFailed(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_BIZ_RECEIVED队列/主题, MESSAGE_BIZ_RECEIVED应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
	void handleMessageBusinessReceived(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_PULL队列/主题：
     * 不做任何操作（包括不发布MQ新消息）
     */
	void handleMessagePullReady(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_BIZ_PULLED队列/主题, MESSAGE_BIZ_PULLED应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
	void handleMessageBusinessPulled(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_BIZ_PULLED队列/主题, MESSAGE_BIZ_PULLED应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
	void handleMessagePullFailed(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_READ队列/主题, MESSAGE_READ应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
    void handleMessageRead(MqMessage<Map<String, Object>> message);

    /**
     * 收到MESSAGE_EXPIRED队列/主题, MESSAGE_EXPIRED应该由UmpMsgMainService负责扫描过期消息后发送，调用MessagePushProcessor的方法处理：
     * 修改主表状态为MESSAGE_PULL_FAILED
     * 不发布MQ新消息
     */
    void handleMessageExpired(MqMessage<Map<String, Object>> message);
}