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
package ltd.huntinginfo.feng.common.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.mq.producer.MqMessageProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka 消息生产者实现
 * <p>
 * 完全依赖 Spring Boot 自动配置的 KafkaTemplate。
 * - exchange 参数被忽略
 * - routingKey 直接作为 Topic（使用 Queues 常量）
 * - 延迟消息立即发送，记录警告
 * </p>
 */
@Slf4j
@Service
@Primary
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "kafka")
@RequiredArgsConstructor
public class KafkaMqService implements MqMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // -------------------------------------------------------------------------
    // 核心发送方法
    // -------------------------------------------------------------------------

    @Override
    public <T> void send(String exchange, String routingKey, MqMessage<T> message) {
        // 确保消息ID、发送时间、重试次数不为空
        if (message.getMessageId() == null) {
            message.setMessageId(java.util.UUID.randomUUID().toString());
        }
        if (message.getSendTime() == null) {
            message.setSendTime(java.time.LocalDateTime.now());
        }
        if (message.getRetryCount() == null) {
            message.setRetryCount(0);
        }

        String topic = routingKey;
        String key = message.getMessageId();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Kafka 消息发送成功，topic: {}, partition: {}, offset: {}, messageId: {}",
                        topic, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(), message.getMessageId());
            } else {
                log.error("Kafka 消息发送失败，topic: {}, messageId: {}", topic, message.getMessageId(), ex);
            }
        });
    }

    @Override
    public <T> void sendDelayed(String exchange, String routingKey, MqMessage<T> message, long delayMillis) {
        log.warn("Kafka 不支持原生延迟消息，delayMillis={} 参数被忽略，立即发送。topic={}, messageId={}",
                delayMillis, routingKey, message.getMessageId());
        send(exchange, routingKey, message);
    }

}