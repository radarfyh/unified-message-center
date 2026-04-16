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
//package ltd.huntinginfo.feng.center.strategy.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import ltd.huntinginfo.feng.center.strategy.IMqProducer;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
// 核心：当配置文件中 feng.mq.type = kafka 时，加载此 Bean
//@ConditionalOnProperty(prefix = "feng.mq", name = "type", havingValue = "kafka")
//public class KafkaProducerImpl implements IMqProducer {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @Override
//    public void sendMsg(String topic, Map<String, Object> payload) {
//        log.info("【Kafka】发送消息, Topic: {}, Payload: {}", topic, payload);
//        // 注意：Kafka Topic 需要提前创建，或者配置 auto-create-topics
//        kafkaTemplate.send(topic, payload);
//    }
//}
