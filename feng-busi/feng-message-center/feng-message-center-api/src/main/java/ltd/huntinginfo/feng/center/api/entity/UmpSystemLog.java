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
 * 系统日志表实体类
 * 对应表：ump_system_log
 * 作用：合并认证日志和操作日志，统一记录系统行为
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_system_log", autoResultMap = true)
@Schema(description = "系统日志表实体")
public class UmpSystemLog extends BaseEntity<UmpSystemLog> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日志ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "日志类型:AUTH-认证日志 OPERATION-操作日志 SYSTEM-系统日志")
    private String logType;

    @Schema(description = "日志级别:DEBUG/INFO/WARN/ERROR")
    private String logLevel;

    @Schema(description = "应用标识")
    private String appKey;

    @Schema(description = "操作者")
    private String operator;

    @Schema(description = "操作名称")
    private String operation;

    @Schema(description = "请求ID")
    private String requestId;

    @Schema(description = "API路径")
    private String apiPath;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "请求参数(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> requestParams;

    @Schema(description = "认证类型:APPKEY/TOKEN")
    private String authType;

    @Schema(description = "认证状态:0-失败 1-成功")
    private Integer authStatus;

    @Schema(description = "认证错误码")
    private String authErrorCode;

    @Schema(description = "响应代码")
    private String responseCode;

    @Schema(description = "响应消息")
    private String responseMessage;

    @Schema(description = "响应数据(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> responseData;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "服务器主机")
    private String serverHost;

    @Schema(description = "耗时(ms)")
    private Integer costTime;

    @Schema(description = "内存使用(KB)")
    private Integer memoryUsage;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "错误堆栈")
    private String errorStack;
}