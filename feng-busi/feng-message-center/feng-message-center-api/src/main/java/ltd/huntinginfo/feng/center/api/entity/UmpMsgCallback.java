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
import ltd.huntinginfo.feng.center.api.json.CallbackConfigRequest;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigResponse;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 回调记录表实体类
 * 对应表：ump_msg_callback
 * 作用：记录所有回调请求的执行情况，确保回调的可靠性和可追溯性
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_msg_callback", autoResultMap = true)
@Schema(description = "回调记录表实体")
public class UmpMsgCallback extends BaseEntity<UmpMsgCallback> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息ID")
    private String msgId;

    @Schema(description = "回调地址")
    private String callbackUrl;

    @Schema(description = "回调数据(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> callbackData;

    @Schema(description = "回调签名")
    private String signature;

    @Schema(description = "状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败")
    private String status;

    @Schema(description = "响应内容")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> responseBody;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime sendTime;

    @Schema(description = "响应时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime responseTime;

    @Schema(description = "耗时(ms)")
    private Integer costTime;

    @Schema(description = "重试次数")
    private Integer retryCount;

    @Schema(description = "下次重试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime nextRetryTime;
}