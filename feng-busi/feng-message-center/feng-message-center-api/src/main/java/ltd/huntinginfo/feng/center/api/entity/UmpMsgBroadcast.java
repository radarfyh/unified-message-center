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
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;
import java.time.LocalDateTime;

/**
 * 广播信息筒表实体类
 * 对应表：ump_msg_broadcast
 * 作用：存储广播消息的分发记录，采用读扩散模式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_msg_broadcast", autoResultMap = true)
@Schema(description = "广播信息筒表实体")
public class UmpMsgBroadcast extends BaseEntity<UmpMsgBroadcast> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "广播ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息ID")
    private String msgId;

    @Schema(description = "广播类型:ALL-全体 DEPT-部门 ORG-组织 AREA-区域 CUSTOM-自定义")
    private String broadcastType;
    
    @Schema(description = "接收单位ID")
    private String receivingUnitId;

    @Schema(description = "接收单位代码")
    private String receivingUnitCode;

    @Schema(description = "接收单位名称")
    private String receivingUnitName;
    
    @Schema(description = "接收范围配置(JSON), 多个目标")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ReceivingScope receivingScope;

    @Schema(description = "接收范围描述，多个目标")
    private String receivingDescription;

    @Schema(description = "总接收人数")
    private Integer totalReceivers;

    @Schema(description = "已分发数量")
    private Integer distributedCount;

    @Schema(description = "已接收数量")
    private Integer receivedCount;

    @Schema(description = "已读人数")
    private Integer readCount;

    @Schema(description = "状态:DISTRIBUTING-分发中 COMPLETED-完成 PARTIAL-部分完成")
    private String status;
    
    @Schema(description = "消息分发时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime startTime;

    @Schema(description = "消息处理完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime completeTime;
    
    @Schema(description = "推送次数")
    private Integer pushCount;

    @Schema(description = "最后推送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastPushTime;

    @Schema(description = "推送状态:PENDING-待推送 SUCCESS-已成功推送 FAILED-推送失败")
    private String pushStatus;
}