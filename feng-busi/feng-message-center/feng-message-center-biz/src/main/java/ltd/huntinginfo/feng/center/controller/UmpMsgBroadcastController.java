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

import ltd.huntinginfo.feng.center.api.dto.BroadcastQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
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

/**
 * 广播信息筒表控制器
 * 提供广播记录的查询、统计等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 * 注：本控制器仅暴露查询接口，写操作（创建、更新、状态变更等）由内部服务或定时任务触发，
 * 如需对外提供写接口，请参考其他控制器添加 @SysLog 和 @HasPermission 注解。
 */
@Validated
@RestController
@RequestMapping("/message/broadcast")
@RequiredArgsConstructor
@Tag(name = "广播信息筒管理", description = "广播记录的查询和统计")
public class UmpMsgBroadcastController {

    private final UmpMsgBroadcastService umpMsgBroadcastService;

    @Operation(summary = "根据消息ID查询广播记录", description = "根据消息ID查询广播记录详情")
    @GetMapping("/message/{msgId}")
    public R<BroadcastDetailVO> getBroadcastByMsgId(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId) {
        BroadcastDetailVO broadcast = umpMsgBroadcastService.getBroadcastByMsgId(msgId);
        return R.ok(broadcast);
    }

    @Operation(summary = "分页查询广播记录", description = "根据条件分页查询广播记录列表")
    @PostMapping("/page")
    public R<Page<BroadcastPageVO>> queryBroadcastPage(@Valid @RequestBody BroadcastQueryDTO queryDTO) {
        Page<BroadcastPageVO> page = umpMsgBroadcastService.queryBroadcastPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "获取广播统计", description = "获取指定时间段内的广播统计信息")
    @GetMapping("/statistics")
    public R<BroadcastStatisticsVO> getBroadcastStatistics(
            @Parameter(description = "开始时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime endTime,
            @Parameter(description = "广播类型")
            @RequestParam(required = false) String broadcastType) {

        // 设置默认时间范围（最近30天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(30);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        BroadcastStatisticsVO statistics = umpMsgBroadcastService.getBroadcastStatistics(
                startTime, endTime, broadcastType);
        return R.ok(statistics);
    }
}