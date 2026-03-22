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

import ltd.huntinginfo.feng.center.api.dto.SystemLogQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.*;
import ltd.huntinginfo.feng.center.service.UmpSystemLogService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 系统日志表控制器
 * 提供系统日志的查询、统计和管理接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
@Tag(name = "系统日志管理", description = "系统日志的查询、统计和管理")
public class UmpSystemLogController {

    private final UmpSystemLogService umpSystemLogService;

    @Operation(summary = "记录操作日志", description = "记录操作日志")
    @PostMapping("/record/operation")
    @HasPermission("ump_log_add")
    public R<String> recordOperationLog(
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String operator,
            @RequestParam String operation,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) String apiPath,
            @RequestParam(required = false) String httpMethod,
            @RequestBody(required = false) Map<String, Object> requestParams,
            @RequestParam(required = false) String responseCode,
            @RequestParam(required = false) String responseMessage,
            @RequestBody(required = false) Map<String, Object> responseData,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String userAgent,
            @RequestParam(required = false) String serverHost,
            @RequestParam(required = false) Integer costTime,
            @RequestParam(required = false) Integer memoryUsage,
            @RequestParam(required = false) String errorMessage,
            @RequestParam(required = false) String errorStack) {
        String logId = umpSystemLogService.recordOperationLog(
                logLevel, appKey, operator, operation, requestId, apiPath, httpMethod,
                requestParams, responseCode, responseMessage, responseData,
                ipAddress, userAgent, serverHost, costTime, memoryUsage,
                errorMessage, errorStack);
        return R.ok(logId, "操作日志记录成功");
    }

    @Operation(summary = "记录认证日志", description = "记录认证日志")
    @PostMapping("/record/auth")
    @HasPermission("ump_log_add")
    public R<String> recordAuthLog(
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) String apiPath,
            @RequestParam(required = false) String httpMethod,
            @RequestBody(required = false) Map<String, Object> requestParams,
            @RequestParam(required = false) String authType,
            @RequestParam(required = false) Integer authStatus,
            @RequestParam(required = false) String authErrorCode,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String userAgent,
            @RequestParam(required = false) String serverHost,
            @RequestParam(required = false) Integer costTime) {
        String logId = umpSystemLogService.recordAuthLog(
                logLevel, appKey, operator, requestId, apiPath, httpMethod,
                requestParams, authType, authStatus, authErrorCode,
                ipAddress, userAgent, serverHost, costTime);
        return R.ok(logId, "认证日志记录成功");
    }

    @Operation(summary = "记录系统日志", description = "记录系统日志")
    @PostMapping("/record/system")
    @HasPermission("ump_log_add")
    public R<String> recordSystemLog(
            @RequestParam(required = false) String logLevel,
            @RequestParam String operation,
            @RequestParam(required = false) String responseMessage,
            @RequestParam(required = false) String errorMessage,
            @RequestParam(required = false) String errorStack) {
        String logId = umpSystemLogService.recordSystemLog(
                logLevel, operation, responseMessage, errorMessage, errorStack);
        return R.ok(logId, "系统日志记录成功");
    }

    @Operation(summary = "根据请求ID查询日志", description = "根据请求ID查询日志详情")
    @GetMapping("/request/{requestId}")
    public R<SystemLogDetailVO> getByRequestId(
            @Parameter(description = "请求ID", required = true)
            @PathVariable String requestId) {
        SystemLogDetailVO log = umpSystemLogService.getByRequestId(requestId);
        return R.ok(log);
    }

    @Operation(summary = "分页查询日志", description = "根据条件分页查询日志列表")
    @PostMapping("/page")
    public R<Page<SystemLogPageVO>> queryLogPage(@Valid @RequestBody SystemLogQueryDTO queryDTO) {
        Page<SystemLogPageVO> page = umpSystemLogService.queryLogPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据日志类型查询日志列表", description = "根据日志类型查询日志列表")
    @GetMapping("/type/{logType}")
    public R<List<SystemLogDetailVO>> getByLogType(
            @Parameter(description = "日志类型", required = true)
            @PathVariable String logType,
            @RequestParam(required = false) Integer limit) {
        List<SystemLogDetailVO> logs = umpSystemLogService.getByLogType(logType, limit);
        return R.ok(logs);
    }

    @Operation(summary = "根据日志级别查询日志列表", description = "根据日志级别查询日志列表")
    @GetMapping("/level/{logLevel}")
    public R<List<SystemLogDetailVO>> getByLogLevel(
            @Parameter(description = "日志级别", required = true)
            @PathVariable String logLevel,
            @RequestParam(required = false) Integer limit) {
        List<SystemLogDetailVO> logs = umpSystemLogService.getByLogLevel(logLevel, limit);
        return R.ok(logs);
    }

    @Operation(summary = "根据应用标识查询日志列表", description = "根据应用标识查询日志列表")
    @GetMapping("/app/{appKey}")
    public R<List<SystemLogDetailVO>> getByAppKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey,
            @RequestParam(required = false) Integer limit) {
        List<SystemLogDetailVO> logs = umpSystemLogService.getByAppKey(appKey, limit);
        return R.ok(logs);
    }

    @Operation(summary = "根据操作者查询日志列表", description = "根据操作者查询日志列表")
    @GetMapping("/operator/{operator}")
    public R<List<SystemLogDetailVO>> getByOperator(
            @Parameter(description = "操作者", required = true)
            @PathVariable String operator,
            @RequestParam(required = false) Integer limit) {
        List<SystemLogDetailVO> logs = umpSystemLogService.getByOperator(operator, limit);
        return R.ok(logs);
    }

    @Operation(summary = "获取日志统计信息", description = "获取日志的统计信息")
    @GetMapping("/statistics")
    public R<SystemLogStatisticsVO> getLogStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) String appKey) {
        SystemLogStatisticsVO statistics = umpSystemLogService.getLogStatistics(startTime, endTime, logType, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "获取错误日志统计", description = "获取错误日志的统计信息")
    @GetMapping("/statistics/error")
    public R<List<ErrorLogStatisticsVO>> getErrorLogStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<ErrorLogStatisticsVO> statistics = umpSystemLogService.getErrorLogStatistics(startTime, endTime);
        return R.ok(statistics);
    }

    @Operation(summary = "获取性能统计信息", description = "获取性能统计信息")
    @GetMapping("/statistics/performance")
    public R<LogPerformanceStatisticsVO> getPerformanceStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        LogPerformanceStatisticsVO statistics = umpSystemLogService.getPerformanceStatistics(startTime, endTime);
        return R.ok(statistics);
    }

    @Operation(summary = "获取API调用统计", description = "获取API调用统计")
    @GetMapping("/statistics/api")
    public R<List<ApiCallStatisticsVO>> getApiCallStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer limit) {
        List<ApiCallStatisticsVO> statistics = umpSystemLogService.getApiCallStatistics(startTime, endTime, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "获取操作者统计", description = "获取操作者统计")
    @GetMapping("/statistics/operator")
    public R<List<OperatorStatisticsVO>> getOperatorStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer limit) {
        List<OperatorStatisticsVO> statistics = umpSystemLogService.getOperatorStatistics(startTime, endTime, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "获取应用统计", description = "获取应用统计")
    @GetMapping("/statistics/app")
    public R<List<AppStatisticsVO>> getAppStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer limit) {
        List<AppStatisticsVO> statistics = umpSystemLogService.getAppStatistics(startTime, endTime, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "获取日志趋势统计", description = "获取日志趋势统计")
    @GetMapping("/statistics/trend")
    public R<List<LogTrendVO>> getLogTrendStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "DAY") String interval,
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) String logLevel) {
        List<LogTrendVO> trend = umpSystemLogService.getLogTrendStatistics(startTime, endTime, interval, logType, logLevel);
        return R.ok(trend);
    }

    @Operation(summary = "获取错误日志详情", description = "获取错误日志详情列表")
    @GetMapping("/error")
    public R<List<SystemLogDetailVO>> getErrorLogs(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(required = false) Integer limit) {
        List<SystemLogDetailVO> errorLogs = umpSystemLogService.getErrorLogs(startTime, endTime, limit);
        return R.ok(errorLogs);
    }

    @Operation(summary = "搜索日志", description = "根据关键词搜索日志")
    @GetMapping("/search")
    public R<List<SystemLogDetailVO>> searchLogs(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer limit) {
        List<SystemLogDetailVO> logs = umpSystemLogService.searchLogs(keyword, limit);
        return R.ok(logs);
    }

    @Operation(summary = "导出日志", description = "导出日志数据")
    @PostMapping("/export")
    @SysLog("导出日志")
    @HasPermission("ump_log_export")
    public void exportLogs(@Valid @RequestBody SystemLogQueryDTO queryDTO,
                          HttpServletResponse response) {
        try {
            List<SystemLogDetailVO> logs = umpSystemLogService.exportLogs(queryDTO);

            // 构建CSV内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("日志ID,日志类型,日志级别,应用标识,操作者,操作名称,请求ID,API路径,HTTP方法,IP地址,响应代码,响应消息,错误信息,耗时(ms),创建时间\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (SystemLogDetailVO log : logs) {
                csvContent.append(log.getId()).append(",");
                csvContent.append(log.getLogType()).append(",");
                csvContent.append(log.getLogLevel()).append(",");
                csvContent.append(log.getAppKey() != null ? log.getAppKey() : "").append(",");
                csvContent.append(log.getOperator() != null ? log.getOperator() : "").append(",");
                csvContent.append(log.getOperation() != null ? log.getOperation() : "").append(",");
                csvContent.append(log.getRequestId() != null ? log.getRequestId() : "").append(",");
                csvContent.append(log.getApiPath() != null ? log.getApiPath() : "").append(",");
                csvContent.append(log.getHttpMethod() != null ? log.getHttpMethod() : "").append(",");
                csvContent.append(log.getIpAddress() != null ? log.getIpAddress() : "").append(",");
                csvContent.append(log.getResponseCode() != null ? log.getResponseCode() : "").append(",");
                csvContent.append(log.getResponseMessage() != null ? log.getResponseMessage() : "").append(",");
                csvContent.append(log.getErrorMessage() != null ? log.getErrorMessage() : "").append(",");
                csvContent.append(log.getCostTime() != null ? log.getCostTime() : "").append(",");
                csvContent.append(log.getCreateTime() != null ? log.getCreateTime().format(formatter) : "").append("\n");
            }

            // 设置响应头
            String filename = "system_logs_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            response.setContentType("text/csv");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            // 写入响应
            response.getWriter().write(csvContent.toString());
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException("导出日志失败", e);
        }
    }

    @Operation(summary = "清理过期日志", description = "清理指定天数前的过期日志（逻辑删除）")
    @DeleteMapping("/clean/expired")
    @SysLog("清理过期日志")
    @HasPermission("ump_log_clean")
    public R<Integer> cleanExpiredLogs(
            @RequestParam(defaultValue = "30") Integer days) {
        int cleanedCount = umpSystemLogService.cleanExpiredLogs(days);
        String msg = cleanedCount > 0 ? "成功清理 " + cleanedCount + " 条日志" : "没有日志需要清理";
        return R.ok(cleanedCount, msg);
    }

    @Operation(summary = "批量删除日志", description = "批量删除日志（逻辑删除）")
    @DeleteMapping("/batch")
    @SysLog("批量删除日志")
    @HasPermission("ump_log_del")
    public R<Integer> batchDeleteLogs(@RequestBody List<String> ids) {
        int deletedCount = umpSystemLogService.batchDeleteLogs(ids);
        String msg = deletedCount > 0 ? "成功删除 " + deletedCount + " 条日志" : "没有日志被删除";
        return R.ok(deletedCount, msg);
    }
}