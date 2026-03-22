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
 * 收件箱表实体类
 * 对应表：ump_msg_inbox
 * 作用：存储个人或小范围消息的分发记录，采用写扩散模式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_msg_inbox", autoResultMap = true)
@Schema(description = "收件箱表实体")
public class UmpMsgInbox extends BaseEntity<UmpMsgInbox> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息ID")
    private String msgId;

    @Schema(description = "接收者类型:USER/DEPT/CUSTOM/ALL")
    private String receiverType;
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "接收者名称")
    private String receiverName;
    
    @Schema(description = "接收者身份证号码")
    private String receiverIdNumber;
    
    @Schema(description = "接收者电话")
    private String receiverPhone;
    
    @Schema(description = "接收单位ID")
    private String receivingUnitId;

    @Schema(description = "接收单位代码")
    private String receivingUnitCode;

    @Schema(description = "接收单位名称")
    private String receivingUnitName;

    @Schema(description = "发送方式:PUSH-推送 POLL-轮询")
    private String distributeMode;

    @Schema(description = "发送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime distributeTime;

    @Schema(description = "接收状态:PENDING-待接收 SUCCESS-已接收 FAILED-接收失败")
    private String receiveStatus;

    @Schema(description = "接收时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime receiveTime;

    @Schema(description = "阅读状态:0-未读 1-已读")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime readTime;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "推送次数")
    private Integer pushCount;

    @Schema(description = "最后推送时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime lastPushTime;

    @Schema(description = "推送状态:PENDING-待推送 SUCCESS-已成功推送 FAILED-推送失败")
    private String pushStatus;
}