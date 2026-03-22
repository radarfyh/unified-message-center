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

import ltd.huntinginfo.feng.center.api.dto.StatusCodeQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeDetailVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodePageVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeTreeVO;
import ltd.huntinginfo.feng.center.service.UmpStatusCodeService;
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
 * 消息状态码表控制器
 * 提供状态码的创建、查询、更新等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/message/status-code")
@RequiredArgsConstructor
@Tag(name = "状态码管理", description = "消息状态码的增删改查和状态管理")
public class UmpStatusCodeController {

    private final UmpStatusCodeService umpStatusCodeService;

    @Operation(summary = "创建状态码", description = "创建新的状态码")
    @PostMapping("/create")
    @SysLog("创建状态码")
    @HasPermission("ump_status_code_add")
    public R<String> createStatusCode(
            @RequestParam String statusCode,
            @RequestParam String statusName,
            @RequestParam String statusDesc,
            @RequestParam String category,
            @RequestParam(required = false) String parentCode,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Integer isFinal,
            @RequestParam(required = false) Integer canRetry) {
        String id = umpStatusCodeService.createStatusCode(
                statusCode, statusName, statusDesc, category, parentCode,
                sortOrder, isFinal, canRetry);
        return R.ok(id, "状态码创建成功");
    }

    @Operation(summary = "更新状态码", description = "更新状态码信息")
    @PutMapping("/{id}")
    @SysLog("更新状态码")
    @HasPermission("ump_status_code_edit")
    public R<Boolean> updateStatusCode(
            @Parameter(description = "状态码ID", required = true)
            @PathVariable String id,
            @RequestParam(required = false) String statusName,
            @RequestParam(required = false) String statusDesc,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Integer isFinal,
            @RequestParam(required = false) Integer canRetry,
            @RequestParam(required = false) Integer status) {
        boolean success = umpStatusCodeService.updateStatusCode(
                id, statusName, statusDesc, sortOrder, isFinal, canRetry, status);
        return success ? R.ok(true, "状态码更新成功") : R.failed(false, "状态码更新失败（无记录更新）");
    }

    @Operation(summary = "根据状态码查询", description = "根据状态码查询详情")
    @GetMapping("/code/{statusCode}")
    public R<StatusCodeDetailVO> getByStatusCode(
            @Parameter(description = "状态码", required = true)
            @PathVariable String statusCode) {
        StatusCodeDetailVO statusCodeDetail = umpStatusCodeService.getByStatusCode(statusCode);
        return R.ok(statusCodeDetail);
    }

    @Operation(summary = "分页查询状态码", description = "根据条件分页查询状态码列表")
    @PostMapping("/page")
    public R<Page<StatusCodePageVO>> queryStatusCodePage(@Valid @RequestBody StatusCodeQueryDTO queryDTO) {
        Page<StatusCodePageVO> page = umpStatusCodeService.queryStatusCodePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据分类查询状态码列表", description = "根据分类查询状态码列表")
    @GetMapping("/category/{category}")
    public R<List<StatusCodeDetailVO>> getByCategory(
            @Parameter(description = "分类", required = true)
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<StatusCodeDetailVO> statusCodes = umpStatusCodeService.getByCategory(category, enabledOnly);
        return R.ok(statusCodes);
    }

    @Operation(summary = "根据父状态码查询子状态码列表", description = "根据父状态码查询子状态码列表")
    @GetMapping("/parent/{parentCode}")
    public R<List<StatusCodeDetailVO>> getByParentCode(
            @Parameter(description = "父状态码", required = true)
            @PathVariable String parentCode,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<StatusCodeDetailVO> statusCodes = umpStatusCodeService.getByParentCode(parentCode, enabledOnly);
        return R.ok(statusCodes);
    }

    @Operation(summary = "查询所有启用的状态码", description = "查询所有启用的状态码列表")
    @GetMapping("/enabled")
    public R<List<StatusCodeDetailVO>> getAllEnabled() {
        List<StatusCodeDetailVO> statusCodes = umpStatusCodeService.getAllEnabled();
        return R.ok(statusCodes);
    }

    @Operation(summary = "启用状态码", description = "启用状态码")
    @PutMapping("/enable/{id}")
    @SysLog("启用状态码")
    @HasPermission("ump_status_code_enable")
    public R<Boolean> enableStatusCode(
            @Parameter(description = "状态码ID", required = true)
            @PathVariable String id) {
        boolean success = umpStatusCodeService.enableStatusCode(id);
        return success ? R.ok(true, "状态码已启用") : R.failed(false, "启用失败（状态码不存在或已启用）");
    }

    @Operation(summary = "禁用状态码", description = "禁用状态码")
    @PutMapping("/disable/{id}")
    @SysLog("禁用状态码")
    @HasPermission("ump_status_code_disable")
    public R<Boolean> disableStatusCode(
            @Parameter(description = "状态码ID", required = true)
            @PathVariable String id) {
        boolean success = umpStatusCodeService.disableStatusCode(id);
        return success ? R.ok(true, "状态码已禁用") : R.failed(false, "禁用失败（状态码不存在或已禁用）");
    }

    @Operation(summary = "批量启用状态码", description = "批量启用状态码")
    @PutMapping("/enable/batch")
    @SysLog("批量启用状态码")
    @HasPermission("ump_status_code_enable")
    public R<Integer> batchEnableStatusCodes(@RequestBody List<String> ids) {
        int updatedCount = umpStatusCodeService.batchEnableStatusCodes(ids);
        String msg = updatedCount > 0 ? "成功启用 " + updatedCount + " 个状态码" : "没有状态码被启用（可能已启用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "批量禁用状态码", description = "批量禁用状态码")
    @PutMapping("/disable/batch")
    @SysLog("批量禁用状态码")
    @HasPermission("ump_status_code_disable")
    public R<Integer> batchDisableStatusCodes(@RequestBody List<String> ids) {
        int updatedCount = umpStatusCodeService.batchDisableStatusCodes(ids);
        String msg = updatedCount > 0 ? "成功禁用 " + updatedCount + " 个状态码" : "没有状态码被禁用（可能已禁用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "检查状态码是否存在", description = "检查状态码是否存在")
    @GetMapping("/exists/{statusCode}")
    public R<Boolean> existsByStatusCode(
            @Parameter(description = "状态码", required = true)
            @PathVariable String statusCode) {
        boolean exists = umpStatusCodeService.existsByStatusCode(statusCode);
        return R.ok(exists);
    }

    @Operation(summary = "获取状态码统计", description = "获取状态码的统计信息")
    @GetMapping("/statistics")
    public R<StatusCodeStatisticsVO> getStatusCodeStatistics() {
        StatusCodeStatisticsVO statistics = umpStatusCodeService.getStatusCodeStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "获取状态码层级树", description = "获取状态码的层级树结构")
    @GetMapping("/tree")
    public R<List<StatusCodeTreeVO>> getStatusCodeTree(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<StatusCodeTreeVO> tree = umpStatusCodeService.getStatusCodeTree(category, enabledOnly);
        return R.ok(tree);
    }

    @Operation(summary = "获取状态码映射", description = "获取状态码的键值对映射")
    @GetMapping("/map/{category}")
    public R<Map<String, String>> getStatusCodeMap(
            @Parameter(description = "分类", required = true)
            @PathVariable String category,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        Map<String, String> codeMap = umpStatusCodeService.getStatusCodeMap(category, enabledOnly);
        return R.ok(codeMap);
    }

    @Operation(summary = "检查是否为最终状态", description = "检查状态码是否为最终状态")
    @GetMapping("/check/final/{statusCode}")
    public R<Boolean> isFinalStatusCode(
            @Parameter(description = "状态码", required = true)
            @PathVariable String statusCode) {
        boolean isFinal = umpStatusCodeService.isFinalStatusCode(statusCode);
        return R.ok(isFinal);
    }

    @Operation(summary = "检查是否可重试", description = "检查状态码是否可重试")
    @GetMapping("/check/retry/{statusCode}")
    public R<Boolean> canRetryStatusCode(
            @Parameter(description = "状态码", required = true)
            @PathVariable String statusCode) {
        boolean canRetry = umpStatusCodeService.canRetryStatusCode(statusCode);
        return R.ok(canRetry);
    }

    @Operation(summary = "获取有效流转状态码", description = "获取当前状态码可流转的目标状态码列表")
    @GetMapping("/transitions/{currentStatusCode}")
    public R<List<StatusCodeDetailVO>> getValidTransitionStatusCodes(
            @Parameter(description = "当前状态码", required = true)
            @PathVariable String currentStatusCode) {
        List<StatusCodeDetailVO> transitions = umpStatusCodeService.getValidTransitionStatusCodes(currentStatusCode);
        return R.ok(transitions);
    }

    @Operation(summary = "验证状态流转", description = "验证状态流转是否允许")
    @GetMapping("/validate/transition")
    public R<Boolean> validateStatusTransition(
            @RequestParam String currentStatusCode,
            @RequestParam String targetStatusCode) {
        boolean valid = umpStatusCodeService.validateStatusTransition(currentStatusCode, targetStatusCode);
        return R.ok(valid);
    }
}