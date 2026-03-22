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
package ltd.huntinginfo.feng.common.rabbitmq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.mq.producer.MqMessageProducer;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ 消息发送服务（实现 MqMessageProducer 接口）
 * <p>
 * 核心改进：
 * 1. 不再手动转换 Message，直接调用 rabbitTemplate.convertAndSend(Object)
 * 2. 延迟消息通过 MessagePostProcessor 设置 x-delay 头
 * 3. 移除冗余的消息头设置（消息体已包含全部业务信息）
 * 4. 与重构后的常量体系完全对齐
 * 5. 废弃专用延迟交换机方法，统一使用主交换机 + x-delay
 * </p>
 *
 * @author radarfyh
 * @since 2026-02-12
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "rabbitmq")
@RequiredArgsConstructor
public class RabbitMqService implements MqMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public <T> void send(String exchange, String routingKey, MqMessage<T> message) {
        try {
            // 确保消息ID存在（构造时已生成）
            if (message.getMessageId() == null) {
                message.setMessageId(java.util.UUID.randomUUID().toString());
            }
            // 确保发送时间存在
            if (message.getSendTime() == null) {
                message.setSendTime(java.time.LocalDateTime.now());
            }
            // 确保重试次数不为空
            if (message.getRetryCount() == null) {
                message.setRetryCount(0);
            }

            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(message.getMessageId());
            rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);

            log.debug("RabbitMQ 消息发送成功，exchange: {}, routingKey: {}, messageId: {}, type: {}",
                    exchange, routingKey, message.getMessageId(), message.getBusinessType());
        } catch (AmqpException e) {
            log.error("RabbitMQ 消息发送失败，exchange: {}, routingKey: {}, messageId: {}",
                    exchange, routingKey, message.getMessageId(), e);
            throw new RuntimeException("RabbitMQ 消息发送失败", e);
        }
    }

    @Override
    public <T> void sendDelayed(String exchange, String routingKey, MqMessage<T> message, long delayMillis) {
        try {
            if (message.getMessageId() == null) {
                message.setMessageId(java.util.UUID.randomUUID().toString());
            }
            if (message.getSendTime() == null) {
                message.setSendTime(java.time.LocalDateTime.now());
            }
            if (message.getRetryCount() == null) {
                message.setRetryCount(0);
            }

            // 使用 MessagePostProcessor 设置延迟头
            CorrelationData correlationData = new CorrelationData();
            correlationData.setId(message.getMessageId());
            rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
                msg.getMessageProperties().setHeader("x-delay", delayMillis);
                return msg;
            }, correlationData);

            log.debug("RabbitMQ 延迟消息发送成功，exchange: {}, routingKey: {}, delay: {}ms, messageId: {}",
                    exchange, routingKey, delayMillis, message.getMessageId());
        } catch (AmqpException e) {
            log.error("RabbitMQ 延迟消息发送失败，exchange: {}, routingKey: {}, messageId: {}",
                    exchange, routingKey, message.getMessageId(), e);
            throw new RuntimeException("RabbitMQ 延迟消息发送失败", e);
        }
    }

}