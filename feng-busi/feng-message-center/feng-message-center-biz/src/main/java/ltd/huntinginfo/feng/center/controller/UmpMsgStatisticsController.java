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

import ltd.huntinginfo.feng.center.api.dto.MsgStatisticsQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.*;
import ltd.huntinginfo.feng.center.service.UmpMsgStatisticsService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 消息统计表控制器
 * 提供消息统计的查询、分析和报表接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/message/statistics")
@RequiredArgsConstructor
@Tag(name = "消息统计管理", description = "消息统计的查询、分析和报表")
public class UmpMsgStatisticsController {

    private final UmpMsgStatisticsService umpMsgStatisticsService;

    @Operation(summary = "根据日期和应用查询统计", description = "根据统计日期和应用标识查询统计记录")
    @GetMapping("/date-app")
    public R<MsgStatisticsDetailVO> getByDateAndApp(
            @RequestParam LocalDate statDate,
            @RequestParam String appKey) {
        MsgStatisticsDetailVO statistics = umpMsgStatisticsService.getByDateAndApp(statDate, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "分页查询统计记录", description = "根据条件分页查询统计记录")
    @PostMapping("/page")
    public R<Page<MsgStatisticsPageVO>> queryStatisticsPage(@Valid @RequestBody MsgStatisticsQueryDTO queryDTO) {
        Page<MsgStatisticsPageVO> page = umpMsgStatisticsService.queryStatisticsPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据日期范围查询统计记录", description = "根据日期范围查询统计记录")
    @GetMapping("/date-range")
    public R<List<MsgStatisticsDetailVO>> getByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType) {
        List<MsgStatisticsDetailVO> statistics = umpMsgStatisticsService.getByDateRange(startDate, endDate, appKey, msgType);
        return R.ok(statistics);
    }

    @Operation(summary = "根据应用标识查询统计记录", description = "根据应用标识查询统计记录")
    @GetMapping("/app/{appKey}")
    public R<List<MsgStatisticsDetailVO>> getByAppKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey,
            @RequestParam(required = false) Integer limit) {
        List<MsgStatisticsDetailVO> statistics = umpMsgStatisticsService.getByAppKey(appKey, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "根据消息类型查询统计记录", description = "根据消息类型查询统计记录")
    @GetMapping("/msg-type/{msgType}")
    public R<List<MsgStatisticsDetailVO>> getByMsgType(
            @Parameter(description = "消息类型", required = true)
            @PathVariable String msgType,
            @RequestParam(required = false) Integer limit) {
        List<MsgStatisticsDetailVO> statistics = umpMsgStatisticsService.getByMsgType(msgType, limit);
        return R.ok(statistics);
    }

    @Operation(summary = "获取统计汇总信息", description = "获取消息统计的汇总信息")
    @GetMapping("/summary")
    public R<MsgStatisticsSummaryVO> getStatisticsSummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType) {
        MsgStatisticsSummaryVO summary = umpMsgStatisticsService.getStatisticsSummary(startDate, endDate, appKey, msgType);
        return R.ok(summary);
    }

    @Operation(summary = "获取应用统计排名", description = "获取应用统计排名")
    @GetMapping("/ranking/app")
    public R<List<AppStatisticsRankingVO>> getAppRanking(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<AppStatisticsRankingVO> ranking = umpMsgStatisticsService.getAppRanking(startDate, endDate, limit);
        return R.ok(ranking);
    }

    @Operation(summary = "获取消息类型统计排名", description = "获取消息类型统计排名")
    @GetMapping("/ranking/msg-type")
    public R<List<MsgTypeStatisticsRankingVO>> getMsgTypeRanking(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<MsgTypeStatisticsRankingVO> ranking = umpMsgStatisticsService.getMsgTypeRanking(startDate, endDate, limit);
        return R.ok(ranking);
    }

    @Operation(summary = "获取统计趋势数据", description = "获取统计趋势数据")
    @GetMapping("/trend")
    public R<List<StatisticsTrendVO>> getStatisticsTrend(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType,
            @RequestParam(defaultValue = "DAY") String interval) {
        List<StatisticsTrendVO> trend = umpMsgStatisticsService.getStatisticsTrend(startDate, endDate, appKey, msgType, interval);
        return R.ok(trend);
    }

    @Operation(summary = "获取性能统计信息", description = "获取性能统计信息")
    @GetMapping("/performance")
    public R<PerformanceStatisticsVO> getPerformanceStatistics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey) {
        PerformanceStatisticsVO statistics = umpMsgStatisticsService.getPerformanceStatistics(startDate, endDate, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "获取错误统计信息", description = "获取错误统计信息")
    @GetMapping("/error")
    public R<List<ErrorStatisticsVO>> getErrorStatistics(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey) {
        List<ErrorStatisticsVO> statistics = umpMsgStatisticsService.getErrorStatistics(startDate, endDate, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "检查统计记录是否存在", description = "检查统计记录是否存在")
    @GetMapping("/exists")
    public R<Boolean> existsStatistics(
            @RequestParam LocalDate statDate,
            @RequestParam String appKey,
            @RequestParam String msgType) {
        boolean exists = umpMsgStatisticsService.existsStatistics(statDate, appKey, msgType);
        return R.ok(exists);
    }

    @Operation(summary = "清理过期统计记录", description = "清理指定天数前的过期统计记录")
    @DeleteMapping("/clean/expired")
    @SysLog("清理过期统计记录")
    @HasPermission("ump_statistics_clean")
    public R<Integer> cleanExpiredStatistics(
            @RequestParam(defaultValue = "365") Integer days) {
        int cleanedCount = umpMsgStatisticsService.cleanExpiredStatistics(days);
        String msg = cleanedCount > 0 ? "成功清理 " + cleanedCount + " 条过期统计记录" : "没有记录被清理";
        return R.ok(cleanedCount, msg);
    }

    @Operation(summary = "获取统计图表数据", description = "获取统计图表数据")
    @GetMapping("/chart")
    public R<Map<String, Object>> getChartData(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(defaultValue = "SEND") String chartType) {
        Map<String, Object> chartData = umpMsgStatisticsService.getChartData(startDate, endDate, appKey, chartType);
        return R.ok(chartData);
    }

    @Operation(summary = "导出统计报表", description = "导出统计报表数据")
    @GetMapping("/export")
    @HasPermission("ump_statistics_export")
    public void exportStatisticsReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String msgType,
            HttpServletResponse response) {
        try {
            List<MsgStatisticsExportVO> exportData = umpMsgStatisticsService.exportStatisticsReport(
                    startDate, endDate, appKey, msgType);

            // 构建CSV内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("统计日期,应用标识,消息类型,发送数量,发送成功数量,发送失败数量,接收数量,阅读数量,错误数量,重试数量,平均处理时间(ms),平均接收时间(ms),平均阅读时间(ms),创建时间\n");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (MsgStatisticsExportVO data : exportData) {
                csvContent.append(data.getStatDate().format(dateFormatter)).append(",");
                csvContent.append(data.getAppKey() != null ? data.getAppKey() : "").append(",");
                csvContent.append(data.getMsgType() != null ? data.getMsgType() : "").append(",");
                csvContent.append(data.getSendCount()).append(",");
                csvContent.append(data.getSendSuccessCount()).append(",");
                csvContent.append(data.getSendFailedCount()).append(",");
                csvContent.append(data.getReceiveCount()).append(",");
                csvContent.append(data.getReadCount()).append(",");
                csvContent.append(data.getErrorCount()).append(",");
                csvContent.append(data.getRetryCount()).append(",");
                csvContent.append(data.getAvgProcessTime()).append(",");
                csvContent.append(data.getAvgReceiveTime()).append(",");
                csvContent.append(data.getAvgReadTime()).append(",");
                csvContent.append(data.getCreateTime() != null ? data.getCreateTime().format(dateTimeFormatter) : "").append("\n");
            }

            // 设置响应头
            String filename = "message_statistics_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv";
            response.setContentType("text/csv");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            // 写入响应
            response.getWriter().write(csvContent.toString());
            response.getWriter().flush();
        } catch (IOException e) {
            // 全局异常处理器应处理此异常，此处可记录日志但无需返回 R
            throw new RuntimeException("导出统计报表失败", e);
        }
    }

    @Operation(summary = "获取实时统计概览", description = "获取实时统计概览")
    @GetMapping("/overview/real-time")
    public R<RealTimeStatisticsOverviewVO> getRealTimeOverview() {
        RealTimeStatisticsOverviewVO overview = umpMsgStatisticsService.getRealTimeOverview();
        return R.ok(overview);
    }

    @Operation(summary = "获取统计看板数据", description = "获取统计看板数据")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> getDashboardData(
            @RequestParam(defaultValue = "30") Integer days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        // 获取汇总数据
        MsgStatisticsSummaryVO summary = umpMsgStatisticsService.getStatisticsSummary(startDate, endDate, null, null);

        // 获取应用排名
        List<AppStatisticsRankingVO> appRanking = umpMsgStatisticsService.getAppRanking(startDate, endDate, 5);

        // 获取消息类型排名
        List<MsgTypeStatisticsRankingVO> msgTypeRanking = umpMsgStatisticsService.getMsgTypeRanking(startDate, endDate, 5);

        // 获取趋势数据
        List<StatisticsTrendVO> trendData = umpMsgStatisticsService.getStatisticsTrend(startDate, endDate, null, null, "DAY");

        // 获取实时概览
        RealTimeStatisticsOverviewVO realTimeOverview = umpMsgStatisticsService.getRealTimeOverview();

        // 构建看板数据
        Map<String, Object> dashboardData = Map.of(
                "summary", summary,
                "appRanking", appRanking,
                "msgTypeRanking", msgTypeRanking,
                "trendData", trendData,
                "realTimeOverview", realTimeOverview
        );

        return R.ok(dashboardData);
    }
}