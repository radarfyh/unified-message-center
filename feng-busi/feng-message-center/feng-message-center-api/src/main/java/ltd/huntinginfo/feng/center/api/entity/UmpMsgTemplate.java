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
import ltd.huntinginfo.feng.center.api.json.TemplateVariableDefinition;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

import java.util.Map;

/**
 * 消息模板表实体类
 * 对应表：ump_msg_template
 * 作用：管理消息模板，支持模板化消息发送
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_msg_template", autoResultMap = true)
@Schema(description = "消息模板表实体")
public class UmpMsgTemplate extends BaseEntity<UmpMsgTemplate> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "模板代码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "标题模板")
    private String titleTemplate;

    @Schema(description = "内容模板")
    private String contentTemplate;

    @Schema(description = "模板变量定义(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, TemplateVariableDefinition> variables;

    @Schema(description = "默认优先级")
    private Integer defaultPriority;

    @Schema(description = "默认推送方式")
    private String defaultPushMode;

    @Schema(description = "默认回调地址")
    private String defaultCallbackUrl;

    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
}