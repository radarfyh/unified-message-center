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
import ltd.huntinginfo.feng.center.api.json.CallbackConfig;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用认证凭证表实体类
 * 对应表：ump_app_credential
 * 作用：管理所有接入统一消息平台的业务系统，包括直接接入和通过代理平台接入的应用
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_app_credential", autoResultMap = true)
@Schema(description = "应用认证凭证表实体")
public class UmpAppCredential extends BaseEntity<UmpAppCredential> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "应用唯一标识")
    private String appKey;

    @Schema(description = "AES加密后的应用密钥")
    private String appSecret;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用类型:DIRECT-直接接入 AGENT-代理接入")
    private String appType;
    
    // ===================负责人信息===================
    
    @Schema(description = "所属单位编码")
    private String agencyCode;
    
    @Schema(description = "所属单位区域代码")
    private String divisionCode;
    
    @Schema(description = "所属单位名称")
    private String agencyName;
    
    @Schema(description = "申请人证件号码")
    private String applicantIdCard;
    
    @Schema(description = "申请人电话")
    private String applicantPhone;
    
    @Schema(description = "申请人姓名")
    private String applicantName;

    // ===================应用信息===================
    
    @Schema(description = "应用描述")
    private String appDesc;

    @Schema(description = "应用图标地址")
    private String appIcon;

    @Schema(description = "应用首页地址")
    private String homeUrl;
    
    @Schema(description = "分配给业务系统Token")
    private String appToken;
    
    @Schema(description = "分配业务系统Token的过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime appTokenExpireTime;

    // ===================推送配置===================

    @Schema(description = "默认推送方式:PUSH-推送 POLL-轮询")
    private String defaultPushMode;

    @Schema(description = "默认回调地址")
    private String callbackUrl;
    
    @Schema(description = "回调配置(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private CallbackConfig callbackConfig;

    @Schema(description = "回调认证模式")
    private String callbackAuthMode;

    // ===================限制配置===================

    @Schema(description = "API调用速率限制(次/分钟)")
    private Integer rateLimit;

    @Schema(description = "最大消息大小(字节)")
    private Integer maxMsgSize;

    @Schema(description = "IP白名单(JSON数组)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> ipWhitelist;

    // ===================状态===================

    @Schema(description = "状态:0-禁用 1-启用")
    @TableField("status")
    private Integer status;

    @Schema(description = "密钥过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime secretExpireTime;
}