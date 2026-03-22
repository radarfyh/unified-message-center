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

import java.time.LocalDate;

/**
 * 消息统计表实体类
 * 对应表：ump_msg_statistics
 * 作用：按天统计消息发送和接收情况，用于业务分析和监控
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("ump_msg_statistics")
@Schema(description = "消息统计表实体")
public class UmpMsgStatistics extends BaseEntity<UmpMsgStatistics> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "统计ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "统计日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDate;

    @Schema(description = "应用标识")
    private String appKey;

    @Schema(description = "消息类型")
    private String msgType;

    @Schema(description = "发送数量")
    private Integer sendCount;

    @Schema(description = "发送成功数量")
    private Integer sendSuccessCount;

    @Schema(description = "发送失败数量")
    private Integer sendFailedCount;

    @Schema(description = "接收数量")
    private Integer receiveCount;

    @Schema(description = "阅读数量")
    private Integer readCount;

    @Schema(description = "平均处理时间(毫秒)")
    private Integer avgProcessTime;

    @Schema(description = "平均接收时间(毫秒)")
    private Integer avgReceiveTime;

    @Schema(description = "平均阅读时间(毫秒)")
    private Integer avgReadTime;

    @Schema(description = "错误数量")
    private Integer errorCount;

    @Schema(description = "重试数量")
    private Integer retryCount;
}