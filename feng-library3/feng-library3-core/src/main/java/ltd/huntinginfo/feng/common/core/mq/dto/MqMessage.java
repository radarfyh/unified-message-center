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

package ltd.huntinginfo.feng.common.core.mq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.util.StrUtil;

/**
 * RabbitMQ 消息统一封装体
 * <p>
 * 特性：
 * - 泛型 payload，支持任意业务对象
 * - 自动生成全局消息ID
 * - 包含事件类型（与数据库状态码一致）、业务类型（NOTICE/ALERT等）
 * - 支持扩展参数与重试计数（重试计数建议由消费者维护，生产者一般传0）
 * </p>
 *
 * @author radarfyh
 * @since 2026-02-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqMessage<T> {

    /**
     * 全局唯一消息ID，用于链路追踪与幂等
     */
    private String messageId;

    /**
     * 事件类型，与数据库 ump_msg_main.status 字段值一致
     * 取值见 {@link RabbitMessageEvent.EventTypes}
     */
    private String eventType;

    /**
     * 业务类型，对应数据库 ump_msg_main.msg_type 字段
     * 取值见 {@link RabbitMessageEvent.BusinessTypes}
     */
    private String businessType;

    /**
     * 业务消息体
     */
    private T payload;

    /**
     * 消息创建时间（ISO-8601 字符串，由 Jackson 自动序列化）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * 扩展属性，用于传递额外元数据
     */
    private Map<String, Object> extParams;

    /**
     * 重试次数（仅用于消费者重试传递，生产者通常填0）
     */
    private Integer retryCount;

    /**
     * 快速构造消息（仅事件类型 + 业务数据）
     * 业务类型默认为 null，由生产者按需补充
     */
    public static <T> MqMessage<T> create(String msgId, String eventType, T payload) {
        return MqMessage.<T>builder()
                .messageId(msgId)
                .eventType(eventType)
                .payload(payload)
                .sendTime(LocalDateTime.now())
                .retryCount(MqMessageEventConstants.RetryDefaults.MAX_RETRY)
                .build();
    }

    /**
     * 完整构造消息（事件类型 + 业务类型 + 业务数据）
     */
    public static <T> MqMessage<T> create(String msgId, String eventType, String businessType, T payload) {
        return MqMessage.<T>builder()
                .messageId(msgId)
                .eventType(eventType)
                .businessType(businessType)
                .payload(payload)
                .sendTime(LocalDateTime.now())
                .retryCount(MqMessageEventConstants.RetryDefaults.MAX_RETRY)
                .build();
    }
}