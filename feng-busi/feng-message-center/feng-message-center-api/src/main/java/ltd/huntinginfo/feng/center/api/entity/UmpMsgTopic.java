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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;
import java.util.Map;

/**
 * 消息主题表实体类
 * 对应表：ump_msg_topic
 * 作用：管理消息主题，用于消息的分类和路由
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_msg_topic", autoResultMap = true)
@Schema(description = "消息主题表实体")
public class UmpMsgTopic extends BaseEntity<UmpMsgTopic> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主题ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "主题代码")
    private String topicCode;

    @Schema(description = "主题名称")
    private String topicName;

    @Schema(description = "主题类型:SYSTEM-系统主题 CUSTOM-自定义主题")
    private String topicType;

    @Schema(description = "主题描述")
    private String description;

    @Schema(description = "默认消息类型")
    private String defaultMsgType;

    @Schema(description = "默认优先级")
    private Integer defaultPriority;

    @Schema(description = "路由规则配置(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> routingRules;

    @Schema(description = "订阅者数量")
    private Integer subscriberCount;

    @Schema(description = "最大订阅者数量")
    private Integer maxSubscribers;

    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
}