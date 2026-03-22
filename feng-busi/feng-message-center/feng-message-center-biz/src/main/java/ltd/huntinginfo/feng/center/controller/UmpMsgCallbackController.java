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

import ltd.huntinginfo.feng.center.api.dto.CallbackQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.CallbackDetailVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackPageVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgCallbackService;
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
 * 回调记录表控制器
 * 提供回调记录的查询、统计等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 * 注：本控制器仅暴露查询接口，写操作（创建、更新、删除等）由内部服务或定时任务触发，
 * 如需对外提供写接口，请参考其他控制器添加 @SysLog 和 @HasPermission 注解。
 */
@Validated
@RestController
@RequestMapping("/message/callback")
@RequiredArgsConstructor
@Tag(name = "回调记录管理", description = "回调记录的查询和统计")
public class UmpMsgCallbackController {

    private final UmpMsgCallbackService umpMsgCallbackService;

    @Operation(summary = "根据消息和接收者查询回调记录", description = "根据消息ID和接收者ID查询回调记录列表")
    @GetMapping("/message/{msgId}/receiver/{receiverId}")
    public R<List<CallbackDetailVO>> getCallbacksByMsgAndReceiver(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId,
            @Parameter(description = "接收者ID", required = true)
            @PathVariable String receiverId) {
        List<CallbackDetailVO> callbacks = umpMsgCallbackService.getCallbacksByMsgAndReceiver(msgId, receiverId);
        return R.ok(callbacks);
    }

    @Operation(summary = "分页查询回调记录", description = "根据条件分页查询回调记录列表")
    @PostMapping("/page")
    public R<Page<CallbackPageVO>> queryCallbackPage(@Valid @RequestBody CallbackQueryDTO queryDTO) {
        Page<CallbackPageVO> page = umpMsgCallbackService.queryCallbackPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取回调记录详情", description = "根据回调记录ID获取详情")
    @GetMapping("/{callbackId}")
    public R<CallbackDetailVO> getCallbackDetail(
            @Parameter(description = "回调记录ID", required = true)
            @PathVariable String callbackId) {
        CallbackDetailVO detail = umpMsgCallbackService.getCallbackDetail(callbackId);
        return R.ok(detail);
    }

    @Operation(summary = "获取回调统计", description = "获取指定时间段内的回调统计信息")
    @GetMapping("/statistics")
    public R<CallbackStatisticsVO> getCallbackStatistics(
            @Parameter(description = "开始时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime endTime,
            @Parameter(description = "消息ID")
            @RequestParam(required = false) String msgId) {

        // 设置默认时间范围（最近7天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        CallbackStatisticsVO statistics = umpMsgCallbackService.getCallbackStatistics(
                startTime, endTime, msgId);
        return R.ok(statistics);
    }
}