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
package ltd.huntinginfo.feng.common.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;

/**
 * RabbitMQ 队列、交换机、绑定配置（Spring Boot 4 + Jackson 3）
 * <p>
 * 设计原则：
 * - 队列名、交换机名、路由键严格使用 MqMessageEventConstants 常量
 * - 移除所有队列级 TTL，改为消息级 TTL（生产者设置）
 * - 移除队列 maxLength 硬编码，由 RabbitMQ 磁盘/内存阈值控制
 * - 独立死信交换机，各业务队列绑定 DLX + 明确路由键
 * - 废弃专用延迟交换机，使用主交换机 + x-delay 头 + 业务延迟队列
 * </p>
 */
@Configuration
public class RabbitQueueConfig {

    // -------------------- 1. 交换机 --------------------
    @Bean
    public TopicExchange messageExchange() {
        return ExchangeBuilder.topicExchange(MqMessageEventConstants.Exchanges.MESSAGE)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(MqMessageEventConstants.Exchanges.DLX)
                .durable(true)
                .build();
    }

    // -------------------- 2. 消息状态队列 --------------------
    @Bean
    public Queue messageReceivedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_RECEIVED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageDistributingQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTING)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageDistributedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messageDistFailedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DIST_FAILED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messagePushedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_PUSHED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messagePushFailedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_PUSH_FAILED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messageBusinessReceivedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_BIZ_RECEIVED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messagePullQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_PULL)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messageBusinessPulledQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_BIZ_PULLED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }
    
    @Bean
    public Queue messagePullFailedQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_PULL_FAILED)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageReadQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_READ)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    @Bean
    public Queue messageExpiredQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_EXPIRED)
                .build();
    }

    // -------------------- 3. 异步任务队列 --------------------
    @Bean
    public Queue messagePushTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_PUSH_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_RETRY_TASK)
                .build();
    }

    @Bean
    public Queue messageDistributeTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DISTRIBUTE_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_RETRY_TASK)
                .build();
    }

    @Bean
    public Queue messageRetryTaskQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_RETRY_TASK)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    // -------------------- 4. 延迟队列 --------------------
    @Bean
    public Queue delayedSendQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DELAYED_SEND)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    @Bean
    public Queue delayedExpireQueue() {
        return QueueBuilder.durable(MqMessageEventConstants.Queues.MESSAGE_DELAYED_EXPIRE)
                .deadLetterExchange(MqMessageEventConstants.Exchanges.DLX)
                .deadLetterRoutingKey(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED)
                .build();
    }

    // -------------------- 5. 绑定关系 --------------------
    @Bean
    public Binding messageReceivedBinding(Queue messageReceivedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageReceivedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_RECEIVED);
    }
    
    @Bean
    public Binding messageDistributingBinding(Queue messageDistributingQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageDistributingQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTING);
    }
    
    @Bean
    public Binding messageDistributedBinding(Queue messageDistributedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageDistributedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTED);
    }

    @Bean
    public Binding messagePushedBinding(Queue messagePushedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messagePushedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSHED);
    }
    
    @Bean
    public Binding messagePushFailedBinding(Queue messagePushFailedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messagePushFailedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSH_FAILED);
    }
    
    @Bean
    public Binding messageBusinessReceivedBinding(Queue messageBusinessReceivedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageBusinessReceivedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_BIZ_RECEIVED);
    }
    
    @Bean
    public Binding messagePullBinding(Queue messagePullQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messagePullQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PULL);
    }

    @Bean
    public Binding messageBusinessPulledBinding(Queue messageBusinessPulledQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageBusinessPulledQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_BIZ_PULLED);
    }
    
    @Bean
    public Binding messagePullFailedBinding(Queue messagePullFailedQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messagePullFailedQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PULL_FAILED);
    }
    
    @Bean
    public Binding messageReadBinding(Queue messageReadQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageReadQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_READ);
    }

    @Bean
    public Binding messageExpiredBinding(Queue messageExpiredQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageExpiredQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_EXPIRED);
    }
    
    // ----------------

    @Bean
    public Binding messagePushTaskBinding(Queue messagePushTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messagePushTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_PUSH_TASK);
    }

    @Bean
    public Binding messageDistributeTaskBinding(Queue messageDistributeTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageDistributeTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DISTRIBUTE_TASK);
    }

    @Bean
    public Binding messageRetryTaskBinding(Queue messageRetryTaskQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(messageRetryTaskQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_RETRY_TASK);
    }

    @Bean
    public Binding delayedSendBinding(Queue delayedSendQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(delayedSendQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DELAYED_SEND);
    }

    @Bean
    public Binding delayedExpireBinding(Queue delayedExpireQueue, TopicExchange messageExchange) {
        return BindingBuilder.bind(delayedExpireQueue)
                .to(messageExchange)
                .with(MqMessageEventConstants.RoutingKeys.ROUTING_KEY_DELAYED_EXPIRE);
    }
}