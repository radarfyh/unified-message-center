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

package ltd.huntinginfo.feng.common.core.mq.producer;

import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;

/**
 * 消息队列生产者抽象接口
 * <p>
 * 统一消息平台可基于此接口实现 RabbitMQ、Kafka 等多种实现，
 * 业务模块通过 {@code @ConditionalOnProperty} 注入具体实现，实现 MQ 切换。
 * </p>
 *
 * @author radarfyh
 * @since 2026-02-12
 */
public interface MqMessageProducer {

    /**
     * 发送普通消息
     *
     * @param exchange   交换机（RabbitMQ）/ Topic（Kafka）
     * @param routingKey 路由键（RabbitMQ）/ 分区键（Kafka）
     * @param message    消息体（已包含业务泛型）
     * @param <T>        业务数据类型
     */
    <T> void send(String exchange, String routingKey, MqMessage<T> message);

    /**
     * 发送延迟消息
     *
     * @param exchange    交换机（RabbitMQ）/ Topic（Kafka）
     * @param routingKey  路由键（RabbitMQ）/ 分区键（Kafka）
     * @param message     消息体
     * @param delayMillis 延迟毫秒数（RabbitMQ 需安装 x-delay 插件）
     * @param <T>         业务数据类型
     */
    <T> void sendDelayed(String exchange, String routingKey, MqMessage<T> message, long delayMillis);

    /**
     * 发送【消息已接收】事件（对应状态：RECEIVED）
     */
    default <T> void sendMessageReceived(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.RECEIVED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_RECEIVED,
                message);
    }

    /**
     * 发送【消息分发中】事件（对应状态：DISTRIBUTING）
     */
    default <T> void sendMessageDistributeStart(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.DISTRIBUTING,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTING,
                message);
    }

    /**
     * 发送【消息已分发】事件（对应状态：DISTRIBUTED）
     */
    default <T> void sendMessageDistributed(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.DISTRIBUTED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTED,
                message);
    }
    
    /**
     * 发送【消息分发失败】事件（对应状态：DIST_FAILED）
     */
    default <T> void sendMessageDistributeFailed(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.DIST_FAILED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DIST_FAILED,
                message);
    }
    
    /**
     * 发送【消息已推送】事件（对应状态：PUSHED）
     */
    default <T> void sendMessagePushed(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.PUSHED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSHED,
                message);
    }
    
    /**
     * 发送【消息推送失败】事件（对应状态：PUSH_FAILED）
     */
    default <T> void sendMessagePushFailed(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.PUSH_FAILED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSH_FAILED,
                message);
    }

    /**
     * 发送【消息业务已接收】事件（对应状态：BIZ_RECEIVED）
     */
    default <T> void sendMessageBusinessReceived(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.BIZ_RECEIVED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_BIZ_RECEIVED,
                message);
    }
    
    /**
     * 发送【消息业务待拉取】事件（对应状态：PULL）
     */
    default <T> void sendMessagePullReady(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.PULL,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PULL,
                message);
    }
    
    /**
     * 发送【消息业务已拉取】事件（对应状态：BIZ_PULLED）
     */
    default <T> void sendMessageBusinessPulled(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.BIZ_PULLED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_BIZ_PULLED,
                message);
    }
    
    /**
     * 发送【消息业务拉取失败】事件（对应状态：PULL_FAILED）
     */
    default <T> void sendMessagePullFailed(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.PULL_FAILED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PULL_FAILED,
                message);
    }

    /**
     * 发送【消息已读】事件（对应状态：READ）
     */
    default <T> void sendMessageRead(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.READ,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_READ,
                message);
    }

    /**
     * 发送【消息已过期】事件（对应状态：EXPIRED）
     */
    default <T> void sendMessageExpired(T payload, String msgType, String msgId) {
        MqMessage<T> message = MqMessage.create(
        		msgId,
                MqMessageEventConstants.EventTypes.EXPIRED,
                msgType,
                payload
        );
        send(MqMessageEventConstants.Exchanges.MESSAGE,
                MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED,
                message);
    }
}