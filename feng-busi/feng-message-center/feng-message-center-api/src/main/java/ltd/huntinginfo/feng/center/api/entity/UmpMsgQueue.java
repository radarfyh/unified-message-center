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
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * 消息队列表实体类
 * 对应表：ump_msg_queue
 * 作用：存储待处理的异步任务，实现消息的异步处理和削峰填谷
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_msg_queue", autoResultMap = true)
@Schema(description = "消息队列表实体")
public class UmpMsgQueue extends BaseEntity<UmpMsgQueue> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID(UUID)")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "队列类型:DISTRIBUTE-分发 PUSH-推送 RETRY-重试")
    private String queueType;

    @Schema(description = "队列名称")
    private String queueName;

    @Schema(description = "消息ID")
    private String msgId;

    @Schema(description = "任务数据(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TaskData taskData;

    @Schema(description = "优先级1-10,数字越小优先级越高")
    private Integer priority;

    @Schema(description = "执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime executeTime;

    @Schema(description = "最大重试次数")
    private Integer maxRetry;

    @Schema(description = "当前重试次数")
    private Integer currentRetry;

    @Schema(description = "状态:PENDING-等待 PROCESSING-处理中 SUCCESS-成功 FAILED-失败")
    private String status;

    @Schema(description = "工作者ID")
    private String workerId;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime endTime;

    @Schema(description = "结果代码")
    private String resultCode;

    @Schema(description = "结果消息")
    private String resultMessage;

    @Schema(description = "错误堆栈")
    private String errorStack;
}