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
package ltd.huntinginfo.feng.center.service.consumer;

import ltd.huntinginfo.feng.center.service.processor.MessageDistributionProcessor;
import ltd.huntinginfo.feng.center.service.processor.MessagePushProcessor;
import ltd.huntinginfo.feng.center.utils.ContentUtil;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.consumer.MqMessageConsumer;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import cn.hutool.json.JSONUtil;

import java.util.Map;

/**
 * RabbitMQ 消息消费者
 * <p>
 * 职责：监听各业务队列，将消息转换为业务对象，调用对应的业务处理器。
 * 不包含任何业务逻辑，所有业务处理委托给 {@link MessageDistributionProcessor} {@link MessagePushProcessor}。
 * </p>
 *
 * @author radarfyh
 * @since 2026-02-12
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "rabbitmq")
@RequiredArgsConstructor
public class MessageRabbitConsumer implements MqMessageConsumer {
    // 消息分发处理器
    private final MessageDistributionProcessor messageDistributionProcessor;

    // 消息推送处理器
    private final MessagePushProcessor messagePushProcessor;

    // ==================== 消息状态事件监听（与 ump_msg_main.status 一一对应） ====================

    /**
     * 收到MESSAGE_RECEIVED队列/主题，调用MessageDistributionProcessor的方法处理：
     * 创建任务到ump_msg_queue，
     * 修改主表的状态为MESSAGE_DISTRIBUTING，
     * 发布新MQ消息到MESSAGE_DISTRIBUTING队列/主题
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_RECEIVED)
    public void handleMessageReceived(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_RECEIVED handleMessageReceived:{}", JSONUtil.toJsonPrettyStr(message));

        messageDistributionProcessor.handleMessageReceived(ContentUtil.convert(message));
    }
    
    /**
     * 收到MESSAGE_DISTRIBUTING队列/主题，调用MessageDistributionProcessor的方法处理
     * 解析receiverScope
     * 创建接收记录到收件箱（个人或者小于阈值的部门、组织、机构、区域），或者创建接收记录到广播信息筒（大于阈值的部门、组织、机构、区域）
     * 修改主表状态为MESSAGE_DISTRIBUTED
     * 发布新MQ消息到MESSAGE_DISTRIBUTED队列/主题
     * 失败时发布新MQ消息到MESSAGE_DIST_FAILED队列/主题
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTING)
    public void handleMessageDistributeStart(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_DISTRIBUTING handleMessageDistributeStart:{}", JSONUtil.toJsonPrettyStr(message));
        messageDistributionProcessor.handleMessageDistributing(ContentUtil.convert(message));
    }

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
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED)
    public void handleMessageDistributed(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_DISTRIBUTED handleMessageDistributed:{}", JSONUtil.toJsonPrettyStr(message));
    	messagePushProcessor.pushMessageToReceiver(ContentUtil.convert(message));
    }

    /**
     * 收到MESSAGE_DIST_FAILED队列/主题, 分发重试：
     * 不再直接调用handleMessageDistributeStart
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_DIST_FAILED)
    public void handleMessageDistributeFailed(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_DIST_FAILED handleMessageDistributeFailed:{}", JSONUtil.toJsonPrettyStr(message));
    	//handleMessageDistributeStart(ContentUtil.convert(message));
    }
    
    /**
     * 收到MESSAGE_PUSHED队列/主题：
     * 不做任何操作（包括不发布MQ新消息）
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PUSHED)
    public void handleMessagePushed(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_PUSHED handleMessagePushed:{}", JSONUtil.toJsonPrettyStr(message));
    }
    
    /**
     * 收到MESSAGE_PUSH_FAILED队列/主题, 推送重试：
     * 不再直接调用handleMessageDistributed
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PUSH_FAILED)
    public void handleMessagePushFailed(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_PUSH_FAILED handleMessagePushFailed:{}", JSONUtil.toJsonPrettyStr(message));
    	//handleMessageDistributed(ContentUtil.convert(message));
    }
    
    /**
     * 收到MESSAGE_BIZ_RECEIVED队列/主题, MESSAGE_BIZ_RECEIVED应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_BIZ_RECEIVED)
    public void handleMessageBusinessReceived(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_BIZ_RECEIVED handleMessageBusinessReceived:{}", JSONUtil.toJsonPrettyStr(message));
        // UmpMsgMainService.reportBizReceived已处理
    	//messageDistributionProcessor.handleMessageBizReceived(ContentUtil.convert(message));
    }
    
    /**
     * 收到MESSAGE_PULL队列/主题：
     * 不做任何操作（包括不发布MQ新消息）
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PULL)
    public void handleMessagePullReady(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_PULL handleMessagePullReady:{}", JSONUtil.toJsonPrettyStr(message));
        
    }
    
    /**
     * 收到MESSAGE_BIZ_PULLED队列/主题, MESSAGE_BIZ_PULLED应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_BIZ_PULLED)
    public void handleMessageBusinessPulled(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_BIZ_PULLED handleMessageBusinessPulled:{}", JSONUtil.toJsonPrettyStr(message));
    }
    
    /**
     * 收到MESSAGE_BIZ_PULLED队列/主题, MESSAGE_BIZ_PULLED应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_PULL_FAILED)
    public void handleMessagePullFailed(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_PULL_FAILED handleMessagePullFailed:{}", JSONUtil.toJsonPrettyStr(message));
    }

    /**
     * 收到MESSAGE_READ队列/主题, MESSAGE_READ应该由UmpMsgMainService负责接收业务系统上报的信息后发送：
     * 不做任何操作（包括不发布MQ新消息）
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_READ)
    public void handleMessageRead(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_READ handleMessageRead:{}", JSONUtil.toJsonPrettyStr(message));
    	//在UmpMsgMainServiceImpl.reportBizRead中已经处理
        //messageDistributionProcessor.handleMessageRead(ContentUtil.convert(message));
    }

    /**
     * 收到MESSAGE_EXPIRED队列/主题, MESSAGE_EXPIRED应该由MessageQueueTaskProcessor负责扫描过期消息后发送拉取失败事件，后续不再处理：
     * 修改主表状态为MESSAGE_PULL_FAILED
     * 不发布MQ新消息
     */
    @Override
    @RabbitListener(queues = MqMessageEventConstants.Queues.MESSAGE_EXPIRED)
    public void handleMessageExpired(MqMessage<Map<String, Object>> message) {
    	log.info("收到MQ消息MESSAGE_EXPIRED handleMessageExpired:{}", JSONUtil.toJsonPrettyStr(message));
//        messageDistributionProcessor.handleMessageExpired(ContentUtil.convert(message));
    }
}