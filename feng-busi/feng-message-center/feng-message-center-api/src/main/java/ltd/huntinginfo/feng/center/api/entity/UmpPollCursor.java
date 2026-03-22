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
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("ump_poll_cursor")
@Schema(name = "轮询游标", description = "消息轮询游标实体")
public class UmpPollCursor extends BaseEntity<UmpPollCursor> {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;

    @Schema(description = "游标键，关联主题编码/队列编码/消息类型", defaultValue = "COMPONENT1")
    private String cursorKey;
    
    @Schema(description = "游标ID")
    private String cursorId;
    
    @Schema(description = "上次轮询时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastPollTime;
    
    @Schema(description = "轮询间隔(秒)，≥10", defaultValue = "10")
    private Integer pollInterval;
    
    @Schema(description = "轮询次数", defaultValue = "0")
    private Integer pollCount;
    
    @Schema(description = "获取消息总数", defaultValue = "0")
    private Integer messageCount;
    
    @Schema(description = "上次获取消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastMessageTime;
    
    @Schema(description = "状态:0-停止 1-运行", defaultValue = "1")
    private Integer status;
    
    @Schema(description = "连续错误次数", defaultValue = "0")
    private Integer errorCount;
    
    @Schema(description = "上次错误信息")
    private String lastError;
    
    @Schema(description = "上次成功时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastSuccessTime;
}