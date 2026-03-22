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

/**
 * 广播消息接收记录表实体类
 * 对应表：ump_broadcast_receive_record
 * 作用：记录重要广播消息的精准送达与阅读状态，用于关键消息审计
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_broadcast_receive_record", autoResultMap = true)
@Schema(description = "广播消息接收记录表实体")
public class UmpBroadcastReceiveRecord extends BaseEntity<UmpBroadcastReceiveRecord> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "广播ID")
    private String broadcastId;

    @Schema(description = "接收者ID")
    private String receiverId;

    @Schema(description = "接收者类型:USER/DEPT/CUSTOM/ALL")
    private String receiverType;

    @Schema(description = "接收状态:PENDING-待送达 SUCCESS-已送达 FAILED-送达失败")
    private String receiveStatus;

    @Schema(description = "接收/送达时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime receiveTime;

    @Schema(description = "阅读状态:0-未读 1-已读")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime readTime;
}