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

import ltd.huntinginfo.feng.center.api.dto.InboxQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxPageVO;
import ltd.huntinginfo.feng.center.api.vo.ReceiverStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.common.core.util.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 收件箱表控制器
 * 提供收件箱的查询、统计等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 * 注：本控制器仅暴露查询接口，写操作（创建、更新、删除等）由内部服务或定时任务触发，
 * 如需对外提供写接口，请参考其他控制器添加 @SysLog 和 @HasPermission 注解。
 */
@Validated
@RestController
@RequestMapping("/message/inbox")
@RequiredArgsConstructor
@Tag(name = "收件箱管理", description = "收件箱的查询和统计")
public class UmpMsgInboxController {

    private final UmpMsgInboxService umpMsgInboxService;

    @Operation(summary = "分页查询收件箱", description = "根据条件分页查询收件箱记录")
    @PostMapping("/page")
    public R<Page<InboxPageVO>> queryInboxPage(@Valid @RequestBody InboxQueryDTO queryDTO) {
        Page<InboxPageVO> page = umpMsgInboxService.queryInboxPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询收件箱详情", description = "根据收件箱记录ID查询详情")
    @GetMapping("/{inboxId}")
    public R<InboxDetailVO> getInboxDetail(
            @Parameter(description = "收件箱记录ID", required = true)
            @PathVariable String inboxId) {
        InboxDetailVO detail = umpMsgInboxService.getInboxDetail(inboxId);
        return R.ok(detail);
    }

    @Operation(summary = "根据消息和接收者查询", description = "根据消息ID和接收者信息查询收件箱记录")
    @GetMapping("/message/{msgId}")
    public R<InboxDetailVO> getByMsgAndReceiver(
            @Parameter(description = "消息ID", required = true)
            @PathVariable String msgId,
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType) {
        InboxDetailVO detail = umpMsgInboxService.getByMsgAndReceiver(msgId, receiverId, receiverType);
        return R.ok(detail);
    }

    @Operation(summary = "统计未读消息数量", description = "根据接收者统计未读消息数量")
    @GetMapping("/unread/count")
    public R<Integer> countUnreadMessages(
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType) {
        Integer count = umpMsgInboxService.countUnreadMessages(receiverId, receiverType);
        return R.ok(count);
    }

    @Operation(summary = "获取接收者统计", description = "获取接收者的消息统计信息")
    @GetMapping("/statistics/receiver")
    public R<ReceiverStatisticsVO> getReceiverStatistics(
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType,
            @Parameter(description = "开始时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        ReceiverStatisticsVO statistics = umpMsgInboxService.getReceiverStatistics(receiverId, receiverType,
                startTime, endTime);
        return R.ok(statistics);
    }
}