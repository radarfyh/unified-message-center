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
package ltd.huntinginfo.feng.common.kafka.config;

import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Kafka 配置类（仅保留条件化装配和可选的 ObjectMapper 定制）
 * <p>
 * 所有连接参数、序列化器均由 Spring Boot 自动配置，通过 application.yml 控制。
 * 本类仅在 mc.mq.type = kafka 时加载，确保与其他 MQ 实现隔离。
 * </p>
 */
@Slf4j
@Configuration
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(name = MqMessageEventConstants.ConfigKeys.MQ_TYPE, havingValue = "kafka")
public class FengKafkaConfig {

    /**
     * 定制 ObjectMapper，支持 Java 8 时间类型
     * 此 Bean 会被自动配置的 JsonSerializer/JsonDeserializer 使用
     * 使用 Jackson 3.x 推荐的建造者模式创建
     */
    @Bean
    public ObjectMapper kafkaObjectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }
}