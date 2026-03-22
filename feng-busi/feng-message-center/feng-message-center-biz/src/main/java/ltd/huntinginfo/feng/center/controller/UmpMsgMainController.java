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

import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息主表控制器
 * 提供消息的查询、状态更新、统计等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/message/main")
@RequiredArgsConstructor
@Tag(name = "消息主表管理", description = "消息主表的查询、状态管理和统计")
public class UmpMsgMainController {

    private final UmpMsgMainService umpMsgMainService;

    @Operation(summary = "根据消息编码查询消息", description = "根据消息编码查询消息详情")
    @GetMapping("/code/{msgCode}")
    public R<MessageDetailVO> getMessageByCode(
            @Parameter(description = "消息编码", required = true)
            @PathVariable String msgCode) {
        MessageDetailVO message = umpMsgMainService.getMessageByCode(msgCode);
        return R.ok(message);
    }

    @Operation(summary = "分页查询消息", description = "根据条件分页查询消息列表")
    @PostMapping("/page")
    public R<Page<MessagePageVO>> queryMessagePage(@Valid @RequestBody MessageQueryDTO queryDTO) {
        Page<MessagePageVO> page = umpMsgMainService.queryMessagePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "更新已读统计", description = "更新消息的已读人数统计")
    @PutMapping("/statistics/read/{msgId}")
    @SysLog("更新消息已读统计")
    @HasPermission("ump_msg_update")
    public R<Boolean> updateReadStatistics(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId,
            @Parameter(description = "已读人数", required = true)
            @RequestParam int readCount) {
        boolean success = umpMsgMainService.updateReadStatistics(msgId, readCount);
        return success ? R.ok(true, "更新成功") : R.failed(false, "更新失败");
    }

    @Operation(summary = "获取消息统计", description = "获取指定时间段内的消息统计信息")
    @GetMapping("/statistics")
    public R<MessageStatisticsVO> getMessageStatistics(
            @Parameter(description = "开始时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "应用标识")
            @RequestParam(required = false) String appKey) {

        // 设置默认时间范围（最近7天）
        if (startTime == null) {
            startTime = LocalDateTime.now().minusDays(7);
        }
        if (endTime == null) {
            endTime = LocalDateTime.now();
        }

        MessageStatisticsVO statistics = umpMsgMainService.getMessageStatistics(startTime, endTime, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "查询未读消息", description = "查询指定接收者的未读消息")
    @GetMapping("/unread")
    public R<List<MessageDetailVO>> getUnreadMessages(
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType,
            @Parameter(description = "限制数量", example = "20")
            @RequestParam(defaultValue = "20") int limit) {

        List<MessageDetailVO> messages = umpMsgMainService.getUnreadMessages(receiverId, receiverType, limit);
        return R.ok(messages);
    }

    @Operation(summary = "检查消息有效性", description = "检查消息是否存在且未删除")
    @GetMapping("/exists/{msgId}")
    public R<Boolean> existsAndValid(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId) {
        boolean exists = umpMsgMainService.existsAndValid(msgId);
        return R.ok(exists);
    }

    @Operation(summary = "删除消息", description = "逻辑删除消息")
    @DeleteMapping("/{msgId}")
    @SysLog("删除消息")
    @HasPermission("ump_msg_del")
    public R<Boolean> deleteMessage(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId) {
        boolean success = umpMsgMainService.removeById(msgId);
        return success ? R.ok(true, "删除成功") : R.failed(false, "删除失败（消息不存在）");
    }
}