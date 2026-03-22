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
package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import ltd.huntinginfo.feng.center.api.dto.MsgQueueQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueuePageVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgQueueService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息队列表控制器
 * 提供队列任务的查询、统计等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 * 注：本控制器仅暴露查询接口，写操作（创建、更新、删除等）由内部服务或定时任务触发，
 * 如需对外提供写接口，请参考其他控制器添加 @SysLog 和 @HasPermission 注解。
 */
@Validated
@RestController
@RequestMapping("/message/queue")
@RequiredArgsConstructor
@Tag(name = "消息队列管理", description = "队列任务的查询和统计")
public class UmpMsgQueueController {

    private final UmpMsgQueueService umpMsgQueueService;

    @Operation(summary = "根据消息ID查询队列任务", description = "根据消息ID查询队列任务列表")
    @GetMapping("/message/{msgId}")
    public R<List<MsgQueueDetailVO>> getQueueTasksByMsgId(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId) {
        List<MsgQueueDetailVO> tasks = umpMsgQueueService.getQueueTasksByMsgId(msgId);
        return R.ok(tasks);
    }

    @Operation(summary = "分页查询队列任务", description = "根据条件分页查询队列任务列表")
    @PostMapping("/page")
    public R<Page<MsgQueuePageVO>> queryQueuePage(@Valid @RequestBody MsgQueueQueryDTO queryDTO) {
        Page<MsgQueuePageVO> page = umpMsgQueueService.queryQueuePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取待执行任务", description = "获取待执行的队列任务")
    @GetMapping("/pending")
    public R<List<MsgQueueDetailVO>> getPendingTasks(
            @Parameter(description = "队列类型")
            @RequestParam(required = false) String queueType,
            @Parameter(description = "队列名称")
            @RequestParam(required = false) String queueName,
            @Parameter(description = "限制数量", example = "100")
            @RequestParam(defaultValue = "100") int limit) {
        List<MsgQueueDetailVO> tasks = umpMsgQueueService.getPendingTasks(queueType, queueName, limit);
        return R.ok(tasks);
    }

    @Operation(summary = "获取任务详情", description = "根据任务ID获取队列任务详情")
    @GetMapping("/{taskId}")
    public R<MsgQueueDetailVO> getQueueTaskDetail(
            @Parameter(description = "任务ID", required = true)
            @PathVariable String taskId) {
        MsgQueueDetailVO detail = umpMsgQueueService.getQueueTaskDetail(taskId);
        return R.ok(detail);
    }

    @Operation(summary = "获取队列统计", description = "获取指定时间段内的队列统计信息")
    @GetMapping("/statistics")
    public R<MsgQueueStatisticsVO> getQueueStatistics(
            @Parameter(description = "开始时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime endTime,
            @Parameter(description = "队列类型")
            @RequestParam(required = false) String queueType) {

        // 设置默认时间范围（最近7天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        MsgQueueStatisticsVO statistics = umpMsgQueueService.getQueueStatistics(startTime, endTime, queueType);
        return R.ok(statistics);
    }
}