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

// Jackson 3 核心类（包名 tools.jackson）
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.json.JsonMapper.Builder;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;

/**
 * RabbitMQ 基础配置（Spring Boot 4 / Spring AMQP 4.0 / Jackson 3 适配版）
 * <p>
 * 关键修正：
 * 1. 使用 tools.jackson.databind.json.JsonMapper（非 com.fasterxml）
 * 2. 使用 tools.jackson.datatype.jsr310.JavaTimeModule
 * 3. JavaTimeModule 默认输出 ISO 8601 字符串，无需禁用时间戳
 * 4. 直接使用 JacksonJsonMessageConverter(JsonMapper, String...) 构造函数
 * </p>
 *
 * @author radarfyh
 * @since 2026-02-12
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "rabbitmq")
@ConditionalOnClass({RabbitTemplate.class, JacksonJsonMessageConverter.class})
public class FengRabbitConfig {

    /**
     * 信任包列表（从配置中心读取，默认信任 common DTO）
     */
    @Value("${mq.rabbit.trusted-packages:ltd.huntinginfo.feng.common.rabbitmq.dto}")
    private String[] trustedPackages;

    /**
     * Jackson 3 JsonMapper 构建器
     * <p>
     * 特性：
     * - 注册 JavaTimeModule → 支持 LocalDateTime 等 Java 8+ 时间类型，序列化为 ISO-8601 字符串
     * - 不调用 disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) —— Jackson 3 JavaTimeModule 默认已是字符串格式
     * - 如需其他定制，使用 JsonWriteFeature / JsonReadFeature
     * </p>
     */
    @Bean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    /**
     * JSON 消息转换器（Jackson 3 版）
     * <p>
     * 直接传入 JsonMapper 和信任包列表，内部自动配置类型映射器及 TYPE_ID 策略
     * </p>
     */
    @Bean
    public JacksonJsonMessageConverter messageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper, trustedPackages);
    }

    /**
     * RabbitTemplate 核心操作类
     * <p>
     * - 设置自定义消息转换器
     * - 启用 mandatory + 发布确认 + 返回回调
     * </p>
     */
    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         JacksonJsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setMandatory(true);

        // 发布确认回调（需配置 publisher-confirm-type=correlated）
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String messageId = correlationData != null ? correlationData.getId() : null;
            if (ack) {
                log.debug("消息已确认到达交换机，messageId: {}", messageId);
            } else {
                log.error("消息未到达交换机，messageId: {}, 原因: {}", messageId, cause);
            }
        });

        // 消息无法路由回调（mandatory=true 时生效）
        rabbitTemplate.setReturnsCallback(returned ->
                log.error("消息路由失败，exchange: {}, routingKey: {}, replyCode: {}, replyText: {}",
                        returned.getExchange(), returned.getRoutingKey(),
                        returned.getReplyCode(), returned.getReplyText())
        );

        return rabbitTemplate;
    }
}