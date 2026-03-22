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
package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 主题订阅表实体类
 * 对应表：ump_topic_subscription
 * 作用：记录业务系统对消息主题的订阅关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_topic_subscription", autoResultMap = true)
@Schema(description = "主题订阅表实体")
public class UmpTopicSubscription extends BaseEntity<UmpTopicSubscription> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订阅ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "主题代码")
    private String topicCode;

    @Schema(description = "应用标识")
    private String appKey;

    @Schema(description = "回调配置(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> callbackConfig;

    @Schema(description = "回调地址")
    private String callbackUrl;

    @Schema(description = "推送方式:PUSH-推送 POLL-轮询")
    private String pushMode;

    @Schema(description = "状态:0-取消订阅 1-已订阅")
    private Integer status;

    @Schema(description = "订阅时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime subscribeTime;

    @Schema(description = "取消订阅时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime unsubscribeTime;

    @Schema(description = "接收消息数量")
    private Integer messageCount;

    @Schema(description = "最后消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastMessageTime;
}