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
package ltd.huntinginfo.feng.center.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.strategy.IMqProducer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
// 核心：当配置文件中 feng.mq.type = rabbitmq 时，加载此 Bean
@ConditionalOnProperty(prefix = "feng.mq", name = "type", havingValue = "rabbitmq", matchIfMissing = true)
public class RabbitProducerImpl implements IMqProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendMsg(String topic, Map<String, Object> payload) {
        log.info("【RabbitMQ】发送消息, Topic: {}, Payload: {}", topic, payload);
        // 这里简单将 topic 既当作 Exchange 也当作 RoutingKey，具体逻辑可根据业务细化
        rabbitTemplate.convertAndSend(topic, topic, payload);
    }
}