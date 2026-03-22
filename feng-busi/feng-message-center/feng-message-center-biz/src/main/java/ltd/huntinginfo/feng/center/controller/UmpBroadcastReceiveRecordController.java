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

import ltd.huntinginfo.feng.center.api.dto.BroadcastReceiveRecordQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.UnreadReceiverVO;
import ltd.huntinginfo.feng.center.service.UmpBroadcastReceiveRecordService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 广播消息接收记录表控制器
 * 提供广播接收记录的查询、状态更新等接口
 */
@Validated
@RestController
@RequestMapping("/message/broadcast/receive-record")
@RequiredArgsConstructor
@Tag(name = "广播接收记录管理", description = "广播接收记录的增删改查和状态管理")
public class UmpBroadcastReceiveRecordController {

    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;

    @Operation(summary = "创建或更新接收记录", description = "创建或更新广播接收记录")
    @PostMapping("/upsert")
    @SysLog("创建或更新广播接收记录")
    @HasPermission("ump_broadcast_receive_record_upsert")
    public R<Boolean> upsertReceiveRecord(
            @RequestParam String broadcastId,
            @RequestParam String receiverId,
            @RequestParam String receiverType) {
        try {
            boolean success = umpBroadcastReceiveRecordService.upsertReceiveRecord(
                    broadcastId, receiverId, receiverType);
            return success ? R.ok(true, "操作成功") : R.failed(false, "操作失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("创建或更新接收记录失败，请稍后重试");
        }
    }

    @Operation(summary = "批量创建或更新接收记录", description = "批量创建或更新广播接收记录")
    @PostMapping("/upsert/batch")
    @SysLog("批量创建或更新广播接收记录")
    @HasPermission("ump_broadcast_receive_record_upsert")
    public R<Integer> batchUpsertReceiveRecords(
            @RequestParam String broadcastId,
            @RequestBody List<Map<String, Object>> receivers) {
        try {
            int count = umpBroadcastReceiveRecordService.batchUpsertReceiveRecords(broadcastId, receivers);
            String msg = count > 0 ? "成功处理 " + count + " 条接收记录" : "没有记录被处理";
            return R.ok(count, msg);
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("批量创建或更新接收记录失败，请稍后重试");
        }
    }

    @Operation(summary = "查询接收记录", description = "根据复合主键查询广播接收记录")
    @GetMapping("/detail")
    public R<BroadcastReceiveRecordDetailVO> getReceiveRecord(
            @Parameter(description = "广播ID", required = true)
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType) {
        BroadcastReceiveRecordDetailVO detail = umpBroadcastReceiveRecordService.getReceiveRecord(
                broadcastId, receiverId, receiverType);
        return R.ok(detail);
    }

    @Operation(summary = "分页查询接收记录", description = "根据条件分页查询广播接收记录")
    @PostMapping("/page")
    public R<Page<BroadcastReceiveRecordPageVO>> queryReceiveRecordPage(@Valid @RequestBody BroadcastReceiveRecordQueryDTO queryDTO) {
        Page<BroadcastReceiveRecordPageVO> page = umpBroadcastReceiveRecordService.queryReceiveRecordPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "更新接收状态", description = "更新广播接收记录的接收状态")
    @PutMapping("/receive-status")
    @SysLog("更新广播接收状态")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Boolean> updateReceiveStatus(
            @RequestParam String broadcastId,
            @RequestParam String receiverId,
            @RequestParam String receiverType,
            @RequestParam String receiveStatus) {
        try {
            boolean success = umpBroadcastReceiveRecordService.updateReceiveStatus(
                    broadcastId, receiverId, receiverType, receiveStatus);
            return success ? R.ok(true, "状态更新成功") : R.failed(false, "状态更新失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("更新接收状态失败，请稍后重试");
        }
    }

    @Operation(summary = "标记为已送达", description = "将广播接收记录标记为已送达")
    @PutMapping("/delivered")
    @SysLog("标记广播接收记录为已送达")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Boolean> markAsDelivered(
            @Parameter(description = "广播ID", required = true)
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType) {
        try {
            boolean success = umpBroadcastReceiveRecordService.markAsDelivered(
                    broadcastId, receiverId, receiverType);
            return success ? R.ok(true, "标记成功") : R.failed(false, "标记失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("标记已送达失败，请稍后重试");
        }
    }

    @Operation(summary = "批量标记为已送达", description = "批量将广播接收记录标记为已送达")
    @PutMapping("/delivered/batch")
    @SysLog("批量标记广播接收记录为已送达")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Integer> batchMarkAsDelivered(
            @RequestParam String broadcastId,
            @RequestBody List<String> receiverIds,
            @RequestParam String receiverType) {
        try {
            int updatedCount = umpBroadcastReceiveRecordService.batchMarkAsDelivered(
                    broadcastId, receiverIds, receiverType);
            String msg = updatedCount > 0 ? "成功标记 " + updatedCount + " 条记录为已送达" : "没有记录被标记";
            return R.ok(updatedCount, msg);
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("批量标记已送达失败，请稍后重试");
        }
    }

    @Operation(summary = "更新阅读状态", description = "更新广播接收记录的阅读状态")
    @PutMapping("/read-status")
    @SysLog("更新广播阅读状态")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Boolean> updateReadStatus(
            @RequestParam String broadcastId,
            @RequestParam String receiverId,
            @RequestParam String receiverType,
            @RequestParam Integer readStatus) {
        try {
            boolean success = umpBroadcastReceiveRecordService.updateReadStatus(
                    broadcastId, receiverId, receiverType, readStatus);
            return success ? R.ok(true, "状态更新成功") : R.failed(false, "状态更新失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("更新阅读状态失败，请稍后重试");
        }
    }

    @Operation(summary = "标记为已读", description = "将广播接收记录标记为已读")
    @PutMapping("/read")
    @SysLog("标记广播接收记录为已读")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Boolean> markAsRead(
            @Parameter(description = "广播ID", required = true)
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType) {
        try {
            boolean success = umpBroadcastReceiveRecordService.markAsRead(
                    broadcastId, receiverId, receiverType);
            return success ? R.ok(true, "标记成功") : R.failed(false, "标记失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("标记已读失败，请稍后重试");
        }
    }

    @Operation(summary = "批量标记为已读", description = "批量将广播接收记录标记为已读")
    @PutMapping("/read/batch")
    @SysLog("批量标记广播接收记录为已读")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Integer> batchMarkAsRead(
            @RequestParam String broadcastId,
            @RequestBody List<String> receiverIds,
            @RequestParam String receiverType) {
        try {
            int updatedCount = umpBroadcastReceiveRecordService.batchMarkAsRead(
                    broadcastId, receiverIds, receiverType);
            String msg = updatedCount > 0 ? "成功标记 " + updatedCount + " 条记录为已读" : "没有记录被标记";
            return R.ok(updatedCount, msg);
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("批量标记已读失败，请稍后重试");
        }
    }

    @Operation(summary = "根据接收者标记为已读", description = "根据接收者标记广播为已读")
    @PutMapping("/read/receiver")
    @SysLog("根据接收者标记广播为已读")
    @HasPermission("ump_broadcast_receive_record_update")
    public R<Boolean> markAsReadByReceiver(
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType,
            @RequestBody(required = false) List<String> broadcastIds) {
        try {
            Boolean success = umpBroadcastReceiveRecordService.markAsReadByReceiver(
                    receiverId, receiverType, broadcastIds);
            return success ? R.ok(true, "标记成功") : R.failed(false, "标记失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("标记已读失败，请稍后重试");
        }
    }

    @Operation(summary = "获取广播接收统计", description = "获取广播的接收统计信息")
    @GetMapping("/statistics/{broadcastId}")
    public R<BroadcastReceiveRecordStatisticsVO> getBroadcastReceiveStatistics(
            @Parameter(description = "广播ID", required = true)
            @PathVariable String broadcastId) {
        BroadcastReceiveRecordStatisticsVO statistics = umpBroadcastReceiveRecordService.getBroadcastReceiveStatistics(broadcastId);
        return R.ok(statistics);
    }

    @Operation(summary = "查询广播未读接收者", description = "查询广播的未读接收者列表")
    @GetMapping("/unread-receivers/{broadcastId}")
    public R<List<UnreadReceiverVO>> getUnreadReceivers(
            @Parameter(description = "广播ID", required = true)
            @PathVariable String broadcastId,
            @Parameter(description = "限制数量", example = "100")
            @RequestParam(defaultValue = "100") int limit) {
        List<UnreadReceiverVO> receivers = umpBroadcastReceiveRecordService.getUnreadReceivers(broadcastId, limit);
        return R.ok(receivers);
    }

    @Operation(summary = "查询接收者的广播记录", description = "查询指定接收者的广播接收记录")
    @GetMapping("/receiver")
    public R<List<BroadcastReceiveRecordDetailVO>> getReceiverBroadcasts(
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType,
            @Parameter(description = "阅读状态")
            @RequestParam(required = false) Integer readStatus,
            @Parameter(description = "限制数量", example = "20")
            @RequestParam(defaultValue = "20") int limit) {
        List<BroadcastReceiveRecordDetailVO> broadcasts = umpBroadcastReceiveRecordService.getReceiverBroadcasts(
                receiverId, receiverType, readStatus, limit);
        return R.ok(broadcasts);
    }

    @Operation(summary = "删除接收记录", description = "删除广播接收记录")
    @DeleteMapping
    @SysLog("删除广播接收记录")
    @HasPermission("ump_broadcast_receive_record_del")
    public R<Boolean> deleteReceiveRecord(
            @Parameter(description = "广播ID", required = true)
            @RequestParam String broadcastId,
            @Parameter(description = "接收者ID", required = true)
            @RequestParam String receiverId,
            @Parameter(description = "接收者类型", required = true)
            @RequestParam String receiverType) {
        try {
            boolean success = umpBroadcastReceiveRecordService.deleteReceiveRecord(
                    broadcastId, receiverId, receiverType);
            return success ? R.ok(true, "删除成功") : R.failed(false, "删除失败（记录不存在）");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("删除接收记录失败，请稍后重试");
        }
    }

    @Operation(summary = "根据广播ID删除接收记录", description = "根据广播ID删除相关的接收记录")
    @DeleteMapping("/broadcast/{broadcastId}")
    @SysLog("根据广播ID删除接收记录")
    @HasPermission("ump_broadcast_receive_record_del")
    public R<Long> deleteByBroadcastId(
            @Parameter(description = "广播ID", required = true)
            @PathVariable String broadcastId) {
        try {
            long deletedCount = umpBroadcastReceiveRecordService.deleteByBroadcastId(broadcastId);
            String msg = deletedCount > 0 ? "成功删除 " + deletedCount + " 条记录" : "没有记录被删除";
            return R.ok(deletedCount, msg);
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("删除接收记录失败，请稍后重试");
        }
    }
}