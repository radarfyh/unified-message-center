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
package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.IdUtil;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgStatistics;
import ltd.huntinginfo.feng.center.mapper.UmpMsgStatisticsMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgStatisticsService;
import ltd.huntinginfo.feng.center.api.dto.MsgStatisticsQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息统计表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgStatisticsServiceImpl extends ServiceImpl<UmpMsgStatisticsMapper, UmpMsgStatistics> implements UmpMsgStatisticsService {

    private final UmpMsgStatisticsMapper umpMsgStatisticsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upsertStatistics(LocalDate statDate, String appKey, String msgType,
                                   Integer sendCount, Integer sendSuccessCount, Integer sendFailedCount,
                                   Integer receiveCount, Integer readCount, Integer errorCount,
                                   Integer retryCount, Integer processTime, Integer receiveTime,
                                   Integer readTime) {
        if (statDate == null || !StringUtils.hasText(appKey) || !StringUtils.hasText(msgType)) {
            throw new IllegalArgumentException("统计日期、应用标识和消息类型不能为空");
        }

        try {
            // 生成主键ID
            String id = IdUtil.fastSimpleUUID();

            // 对计数参数做 null 转 0 处理（确保累加操作不会出错）
            sendCount = sendCount != null ? sendCount : 0;
            sendSuccessCount = sendSuccessCount != null ? sendSuccessCount : 0;
            sendFailedCount = sendFailedCount != null ? sendFailedCount : 0;
            receiveCount = receiveCount != null ? receiveCount : 0;
            readCount = readCount != null ? readCount : 0;
            errorCount = errorCount != null ? errorCount : 0;
            retryCount = retryCount != null ? retryCount : 0;

            // 调用原子性 upsert 方法
            int affected = umpMsgStatisticsMapper.upsertStatistics(
                    id, statDate, appKey, msgType,
                    sendCount, sendSuccessCount, sendFailedCount,
                    receiveCount, readCount, errorCount, retryCount,
                    processTime, receiveTime, readTime);

            // 如果 affected > 0 表示操作成功（插入或更新）
            boolean success = affected > 0;
            if (success) {
                log.debug("统计记录 upsert 成功，日期: {}, 应用: {}, 消息类型: {}", statDate, appKey, msgType);
            }
            return success;

        } catch (Exception e) {
            log.error("更新统计记录失败，日期: {}, 应用: {}, 消息类型: {}", statDate, appKey, msgType, e);
            throw new RuntimeException("更新统计记录失败", e);
        }
    }

    @Override
    public MsgStatisticsDetailVO getByDateAndApp(LocalDate statDate, String appKey) {
        if (statDate == null || !StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("统计日期和应用标识不能为空");
        }

        UmpMsgStatistics statistics = umpMsgStatisticsMapper.selectByDateAndApp(statDate, appKey);
        if (statistics == null) {
            return null;
        }

        return convertToDetailVO(statistics);
    }

    @Override
    public Page<MsgStatisticsPageVO> queryStatisticsPage(MsgStatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgStatistics> queryWrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getStatDateStart() != null) {
            queryWrapper.ge(UmpMsgStatistics::getStatDate, queryDTO.getStatDateStart());
        }
        
        if (queryDTO.getStatDateEnd() != null) {
            queryWrapper.le(UmpMsgStatistics::getStatDate, queryDTO.getStatDateEnd());
        }
        
        if (StringUtils.hasText(queryDTO.getAppKey())) {
            queryWrapper.eq(UmpMsgStatistics::getAppKey, queryDTO.getAppKey());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgType())) {
            queryWrapper.eq(UmpMsgStatistics::getMsgType, queryDTO.getMsgType());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgStatistics::getStatDate)
                       .orderByDesc(UmpMsgStatistics::getSendCount);
        }

        // 执行分页查询
        Page<UmpMsgStatistics> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgStatistics> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<MsgStatisticsPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<MsgStatisticsPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<MsgStatisticsDetailVO> getByDateRange(LocalDate startDate, LocalDate endDate,
                                                     String appKey, String msgType) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<UmpMsgStatistics> statisticsList = umpMsgStatisticsMapper.selectByDateRange(
                startDate, endDate, appKey, msgType);
        
        return statisticsList.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MsgStatisticsDetailVO> getByAppKey(String appKey, Integer limit) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        List<UmpMsgStatistics> statisticsList = umpMsgStatisticsMapper.selectByAppKey(appKey, limit);
        
        return statisticsList.stream()
                .map(this::convertToDetailVO)
                .sorted((s1, s2) -> s2.getStatDate().compareTo(s1.getStatDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MsgStatisticsDetailVO> getByMsgType(String msgType, Integer limit) {
        if (!StringUtils.hasText(msgType)) {
            throw new IllegalArgumentException("消息类型不能为空");
        }

        List<UmpMsgStatistics> statisticsList = umpMsgStatisticsMapper.selectByMsgType(msgType, limit);
        
        return statisticsList.stream()
                .map(this::convertToDetailVO)
                .sorted((s1, s2) -> s2.getStatDate().compareTo(s1.getStatDate()))
                .collect(Collectors.toList());
    }

    @Override
    public MsgStatisticsSummaryVO getStatisticsSummary(LocalDate startDate, LocalDate endDate,
                                                      String appKey, String msgType) {
        Map<String, Object> summaryMap = umpMsgStatisticsMapper.selectStatisticsSummary(
                startDate, endDate, appKey, msgType);
        
        MsgStatisticsSummaryVO summaryVO = new MsgStatisticsSummaryVO();
        
        if (summaryMap != null) {
            summaryVO.setTotalSendCount(((Number) summaryMap.getOrDefault("total_send_count", 0)).longValue());
            summaryVO.setTotalSendSuccessCount(((Number) summaryMap.getOrDefault("total_send_success_count", 0)).longValue());
            summaryVO.setTotalSendFailedCount(((Number) summaryMap.getOrDefault("total_send_failed_count", 0)).longValue());
            summaryVO.setTotalReceiveCount(((Number) summaryMap.getOrDefault("total_receive_count", 0)).longValue());
            summaryVO.setTotalReadCount(((Number) summaryMap.getOrDefault("total_read_count", 0)).longValue());
            summaryVO.setTotalErrorCount(((Number) summaryMap.getOrDefault("total_error_count", 0)).longValue());
            summaryVO.setTotalRetryCount(((Number) summaryMap.getOrDefault("total_retry_count", 0)).longValue());
            summaryVO.setAvgProcessTime(((Number) summaryMap.getOrDefault("avg_process_time", 0)).doubleValue());
            summaryVO.setAvgReceiveTime(((Number) summaryMap.getOrDefault("avg_receive_time", 0)).doubleValue());
            summaryVO.setAvgReadTime(((Number) summaryMap.getOrDefault("avg_read_time", 0)).doubleValue());
            
            // 计算成功率
            if (summaryVO.getTotalSendCount() > 0) {
                summaryVO.setSendSuccessRate((double) summaryVO.getTotalSendSuccessCount() / summaryVO.getTotalSendCount() * 100);
            }
            
            // 计算接收率
            if (summaryVO.getTotalSendCount() > 0) {
                summaryVO.setReceiveRate((double) summaryVO.getTotalReceiveCount() / summaryVO.getTotalSendCount() * 100);
            }
            
            // 计算阅读率
            if (summaryVO.getTotalSendCount() > 0) {
                summaryVO.setReadRate((double) summaryVO.getTotalReadCount() / summaryVO.getTotalSendCount() * 100);
            }
            
            // 计算错误率
            if (summaryVO.getTotalSendCount() > 0) {
                summaryVO.setErrorRate((double) summaryVO.getTotalErrorCount() / summaryVO.getTotalSendCount() * 100);
            }
            
            // 计算重试率
            if (summaryVO.getTotalSendCount() > 0) {
                summaryVO.setRetryRate((double) summaryVO.getTotalRetryCount() / summaryVO.getTotalSendCount() * 100);
            }
        }
        
        return summaryVO;
    }

    @Override
    public List<AppStatisticsRankingVO> getAppRanking(LocalDate startDate, LocalDate endDate, Integer limit) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<Map<String, Object>> rankingList = umpMsgStatisticsMapper.selectAppRanking(
                startDate, endDate, limit);
        
        return rankingList.stream()
                .map(this::convertToAppRankingVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MsgTypeStatisticsRankingVO> getMsgTypeRanking(LocalDate startDate, LocalDate endDate, Integer limit) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<Map<String, Object>> rankingList = umpMsgStatisticsMapper.selectMsgTypeRanking(
                startDate, endDate, limit);
        
        return rankingList.stream()
                .map(this::convertToMsgTypeRankingVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StatisticsTrendVO> getStatisticsTrend(LocalDate startDate, LocalDate endDate,
                                                     String appKey, String msgType, String interval) {
        if (startDate == null || endDate == null || !StringUtils.hasText(interval)) {
            throw new IllegalArgumentException("开始日期、结束日期和时间间隔不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<Map<String, Object>> trendList = umpMsgStatisticsMapper.selectStatisticsTrend(
                startDate, endDate, appKey, msgType, interval);
        
        return trendList.stream()
                .map(this::convertToTrendVO)
                .collect(Collectors.toList());
    }

    @Override
    public PerformanceStatisticsVO getPerformanceStatistics(LocalDate startDate, LocalDate endDate, String appKey) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        Map<String, Object> perfStats = umpMsgStatisticsMapper.selectPerformanceStatistics(
                startDate, endDate, appKey);
        
        PerformanceStatisticsVO statisticsVO = new PerformanceStatisticsVO();
        
        if (perfStats != null) {
            statisticsVO.setTotalSendCount(((Number) perfStats.getOrDefault("total_send_count", 0)).longValue());
            statisticsVO.setAvgProcessTime(((Number) perfStats.getOrDefault("avg_process_time", 0)).doubleValue());
            statisticsVO.setAvgReceiveTime(((Number) perfStats.getOrDefault("avg_receive_time", 0)).doubleValue());
            statisticsVO.setAvgReadTime(((Number) perfStats.getOrDefault("avg_read_time", 0)).doubleValue());
            statisticsVO.setMaxProcessTime(((Number) perfStats.getOrDefault("max_process_time", 0)).intValue());
            statisticsVO.setMaxReceiveTime(((Number) perfStats.getOrDefault("max_receive_time", 0)).intValue());
            statisticsVO.setMaxReadTime(((Number) perfStats.getOrDefault("max_read_time", 0)).intValue());
            statisticsVO.setP95ProcessTime(((Number) perfStats.getOrDefault("p95_process_time", 0)).doubleValue());
            statisticsVO.setP95ReceiveTime(((Number) perfStats.getOrDefault("p95_receive_time", 0)).doubleValue());
            statisticsVO.setP95ReadTime(((Number) perfStats.getOrDefault("p95_read_time", 0)).doubleValue());
            
            // 计算慢消息比例（假设超过1000ms为慢消息）
            long slowMessages = ((Number) perfStats.getOrDefault("slow_messages", 0)).longValue();
            if (statisticsVO.getTotalSendCount() > 0) {
                statisticsVO.setSlowMessageRate((double) slowMessages / statisticsVO.getTotalSendCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public List<ErrorStatisticsVO> getErrorStatistics(LocalDate startDate, LocalDate endDate, String appKey) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("开始日期和结束日期不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<Map<String, Object>> errorStats = umpMsgStatisticsMapper.selectErrorStatistics(
                startDate, endDate, appKey);
        
        return errorStats.stream()
                .map(this::convertToErrorStatisticsVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreateStatistics(List<UmpMsgStatistics> statisticsList) {
        if (CollectionUtils.isEmpty(statisticsList)) {
            return false;
        }

        try {
            int insertedCount = umpMsgStatisticsMapper.batchInsertStatistics(statisticsList);
            if (insertedCount > 0) {
                log.info("批量创建统计记录成功，数量: {}", insertedCount);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("批量创建统计记录失败", e);
            throw new RuntimeException("批量创建统计记录失败", e);
        }
    }

    @Override
    public boolean existsStatistics(LocalDate statDate, String appKey, String msgType) {
        if (statDate == null || !StringUtils.hasText(appKey) || !StringUtils.hasText(msgType)) {
            return false;
        }

        return umpMsgStatisticsMapper.existsStatistics(statDate, appKey, msgType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredStatistics(Integer days) {
        if (days == null || days <= 0) {
            throw new IllegalArgumentException("保留天数必须大于0");
        }

        LocalDate beforeDate = LocalDate.now().minusDays(days);
        int cleanedCount = umpMsgStatisticsMapper.cleanExpiredStatistics(beforeDate);
        
        if (cleanedCount > 0) {
            log.info("清理过期统计记录成功，清理{}天前的记录，共清理{}条", days, cleanedCount);
        }
        
        return cleanedCount;
    }

    @Override
    public Map<String, Object> getChartData(LocalDate startDate, LocalDate endDate,
                                           String appKey, String chartType) {
        if (startDate == null || endDate == null || !StringUtils.hasText(chartType)) {
            throw new IllegalArgumentException("开始日期、结束日期和图表类型不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        // 获取趋势数据
        List<StatisticsTrendVO> trendData = getStatisticsTrend(startDate, endDate, appKey, null, "DAY");
        
        // 构建图表数据
        Map<String, Object> chartData = new HashMap<>();
        List<String> categories = new ArrayList<>();
        List<Long> sendData = new ArrayList<>();
        List<Long> receiveData = new ArrayList<>();
        List<Long> readData = new ArrayList<>();
        List<Long> errorData = new ArrayList<>();
        
        for (StatisticsTrendVO trend : trendData) {
            categories.add(trend.getTimePeriod());
            sendData.add(trend.getTotalSendCount());
            receiveData.add(trend.getTotalReceiveCount());
            readData.add(trend.getTotalReadCount());
            errorData.add(trend.getTotalErrorCount());
        }
        
        chartData.put("categories", categories);
        
        switch (chartType.toUpperCase()) {
            case "SEND":
                chartData.put("series", Map.of("发送数量", sendData));
                break;
            case "RECEIVE":
                chartData.put("series", Map.of("接收数量", receiveData));
                break;
            case "READ":
                chartData.put("series", Map.of("阅读数量", readData));
                break;
            case "ERROR":
                chartData.put("series", Map.of("错误数量", errorData));
                break;
            case "ALL":
                chartData.put("series", Map.of(
                    "发送数量", sendData,
                    "接收数量", receiveData,
                    "阅读数量", readData,
                    "错误数量", errorData
                ));
                break;
            default:
                chartData.put("series", Map.of("发送数量", sendData));
                break;
        }
        
        return chartData;
    }

    @Override
    public List<MsgStatisticsExportVO> exportStatisticsReport(LocalDate startDate, LocalDate endDate,
                                                             String appKey, String msgType) {
        List<MsgStatisticsDetailVO> statisticsList = getByDateRange(startDate, endDate, appKey, msgType);
        
        return statisticsList.stream()
                .map(this::convertToExportVO)
                .collect(Collectors.toList());
    }

    @Override
    public RealTimeStatisticsOverviewVO getRealTimeOverview() {
        // 获取今日统计
        LocalDate today = LocalDate.now();
        MsgStatisticsSummaryVO todaySummary = getStatisticsSummary(today, today, null, null);
        
        // 获取昨日统计
        LocalDate yesterday = today.minusDays(1);
        MsgStatisticsSummaryVO yesterdaySummary = getStatisticsSummary(yesterday, yesterday, null, null);
        
        // 获取最近7天统计
        LocalDate sevenDaysAgo = today.minusDays(7);
        MsgStatisticsSummaryVO weekSummary = getStatisticsSummary(sevenDaysAgo, today, null, null);
        
        // 获取最近30天统计
        LocalDate thirtyDaysAgo = today.minusDays(30);
        MsgStatisticsSummaryVO monthSummary = getStatisticsSummary(thirtyDaysAgo, today, null, null);
        
        // 计算实时数据（这里需要从实时数据源获取，暂时使用今日数据）
        RealTimeStatisticsOverviewVO overview = new RealTimeStatisticsOverviewVO();
        overview.setTodaySendCount(todaySummary.getTotalSendCount());
        overview.setTodayReceiveCount(todaySummary.getTotalReceiveCount());
        overview.setTodayReadCount(todaySummary.getTotalReadCount());
        overview.setTodayErrorCount(todaySummary.getTotalErrorCount());
        
        overview.setYesterdaySendCount(yesterdaySummary.getTotalSendCount());
        overview.setYesterdayReceiveCount(yesterdaySummary.getTotalReceiveCount());
        overview.setYesterdayReadCount(yesterdaySummary.getTotalReadCount());
        overview.setYesterdayErrorCount(yesterdaySummary.getTotalErrorCount());
        
        overview.setWeekSendCount(weekSummary.getTotalSendCount());
        overview.setWeekReceiveCount(weekSummary.getTotalReceiveCount());
        overview.setWeekReadCount(weekSummary.getTotalReadCount());
        overview.setWeekErrorCount(weekSummary.getTotalErrorCount());
        
        overview.setMonthSendCount(monthSummary.getTotalSendCount());
        overview.setMonthReceiveCount(monthSummary.getTotalReceiveCount());
        overview.setMonthReadCount(monthSummary.getTotalReadCount());
        overview.setMonthErrorCount(monthSummary.getTotalErrorCount());
        
        // 计算增长率
        if (yesterdaySummary.getTotalSendCount() > 0) {
            overview.setSendGrowthRate(
                (double) (todaySummary.getTotalSendCount() - yesterdaySummary.getTotalSendCount()) 
                / yesterdaySummary.getTotalSendCount() * 100
            );
        }
        
        if (yesterdaySummary.getTotalReceiveCount() > 0) {
            overview.setReceiveGrowthRate(
                (double) (todaySummary.getTotalReceiveCount() - yesterdaySummary.getTotalReceiveCount()) 
                / yesterdaySummary.getTotalReceiveCount() * 100
            );
        }
        
        if (yesterdaySummary.getTotalReadCount() > 0) {
            overview.setReadGrowthRate(
                (double) (todaySummary.getTotalReadCount() - yesterdaySummary.getTotalReadCount()) 
                / yesterdaySummary.getTotalReadCount() * 100
            );
        }
        
        if (yesterdaySummary.getTotalErrorCount() > 0) {
            overview.setErrorGrowthRate(
                (double) (todaySummary.getTotalErrorCount() - yesterdaySummary.getTotalErrorCount()) 
                / yesterdaySummary.getTotalErrorCount() * 100
            );
        }
        
        // 获取实时在线用户数（这里需要从实时数据源获取，暂时设置为0）
        overview.setOnlineUserCount(0L);
        
        // 获取实时消息队列大小（这里需要从实时数据源获取，暂时设置为0）
        overview.setQueueSize(0L);
        
        return overview;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgStatistics> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "statDate":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgStatistics::getStatDate);
                } else {
                    queryWrapper.orderByDesc(UmpMsgStatistics::getStatDate);
                }
                break;
            case "sendCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgStatistics::getSendCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgStatistics::getSendCount);
                }
                break;
            case "receiveCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgStatistics::getReceiveCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgStatistics::getReceiveCount);
                }
                break;
            case "readCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgStatistics::getReadCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgStatistics::getReadCount);
                }
                break;
            case "errorCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgStatistics::getErrorCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgStatistics::getErrorCount);
                }
                break;
            case "avgProcessTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgStatistics::getAvgProcessTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgStatistics::getAvgProcessTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgStatistics::getStatDate)
                           .orderByDesc(UmpMsgStatistics::getSendCount);
                break;
        }
    }

    private MsgStatisticsDetailVO convertToDetailVO(UmpMsgStatistics statistics) {
        MsgStatisticsDetailVO vo = new MsgStatisticsDetailVO();
        BeanUtils.copyProperties(statistics, vo);
        
        // 计算成功率、接收率、阅读率
        if (statistics.getSendCount() > 0) {
            vo.setSendSuccessRate((double) statistics.getSendSuccessCount() / statistics.getSendCount() * 100);
            vo.setSendFailedRate((double) statistics.getSendFailedCount() / statistics.getSendCount() * 100);
            vo.setReceiveRate((double) statistics.getReceiveCount() / statistics.getSendCount() * 100);
            vo.setReadRate((double) statistics.getReadCount() / statistics.getSendCount() * 100);
            vo.setErrorRate((double) statistics.getErrorCount() / statistics.getSendCount() * 100);
            vo.setRetryRate((double) statistics.getRetryCount() / statistics.getSendCount() * 100);
        }
        
        return vo;
    }

    private MsgStatisticsPageVO convertToPageVO(UmpMsgStatistics statistics) {
        MsgStatisticsPageVO vo = new MsgStatisticsPageVO();
        BeanUtils.copyProperties(statistics, vo);
        
        // 计算成功率、接收率、阅读率
        if (statistics.getSendCount() > 0) {
            vo.setSendSuccessRate((double) statistics.getSendSuccessCount() / statistics.getSendCount() * 100);
            vo.setReceiveRate((double) statistics.getReceiveCount() / statistics.getSendCount() * 100);
            vo.setReadRate((double) statistics.getReadCount() / statistics.getSendCount() * 100);
        }
        
        return vo;
    }

    private AppStatisticsRankingVO convertToAppRankingVO(Map<String, Object> rankingMap) {
        AppStatisticsRankingVO vo = new AppStatisticsRankingVO();
        vo.setAppKey((String) rankingMap.get("app_key"));
        vo.setSendCount(((Number) rankingMap.getOrDefault("send_count", 0)).longValue());
        vo.setReceiveCount(((Number) rankingMap.getOrDefault("receive_count", 0)).longValue());
        vo.setReadCount(((Number) rankingMap.getOrDefault("read_count", 0)).longValue());
        vo.setErrorCount(((Number) rankingMap.getOrDefault("error_count", 0)).longValue());
        
        if (vo.getSendCount() > 0) {
            vo.setSuccessRate((double) ((Number) rankingMap.getOrDefault("success_count", 0)).longValue() / vo.getSendCount() * 100);
            vo.setReceiveRate((double) vo.getReceiveCount() / vo.getSendCount() * 100);
            vo.setReadRate((double) vo.getReadCount() / vo.getSendCount() * 100);
            vo.setErrorRate((double) vo.getErrorCount() / vo.getSendCount() * 100);
        }
        
        return vo;
    }

    private MsgTypeStatisticsRankingVO convertToMsgTypeRankingVO(Map<String, Object> rankingMap) {
        MsgTypeStatisticsRankingVO vo = new MsgTypeStatisticsRankingVO();
        vo.setMsgType((String) rankingMap.get("msg_type"));
        vo.setSendCount(((Number) rankingMap.getOrDefault("send_count", 0)).longValue());
        vo.setReceiveCount(((Number) rankingMap.getOrDefault("receive_count", 0)).longValue());
        vo.setReadCount(((Number) rankingMap.getOrDefault("read_count", 0)).longValue());
        vo.setErrorCount(((Number) rankingMap.getOrDefault("error_count", 0)).longValue());
        
        if (vo.getSendCount() > 0) {
            vo.setSuccessRate((double) ((Number) rankingMap.getOrDefault("success_count", 0)).longValue() / vo.getSendCount() * 100);
            vo.setReceiveRate((double) vo.getReceiveCount() / vo.getSendCount() * 100);
            vo.setReadRate((double) vo.getReadCount() / vo.getSendCount() * 100);
            vo.setErrorRate((double) vo.getErrorCount() / vo.getSendCount() * 100);
        }
        
        return vo;
    }

    private StatisticsTrendVO convertToTrendVO(Map<String, Object> trendMap) {
        StatisticsTrendVO vo = new StatisticsTrendVO();
        vo.setTimePeriod((String) trendMap.get("time_period"));
        vo.setTotalSendCount(((Number) trendMap.getOrDefault("total_send_count", 0)).longValue());
        vo.setTotalSendSuccessCount(((Number) trendMap.getOrDefault("total_send_success_count", 0)).longValue());
        vo.setTotalSendFailedCount(((Number) trendMap.getOrDefault("total_send_failed_count", 0)).longValue());
        vo.setTotalReceiveCount(((Number) trendMap.getOrDefault("total_receive_count", 0)).longValue());
        vo.setTotalReadCount(((Number) trendMap.getOrDefault("total_read_count", 0)).longValue());
        vo.setTotalErrorCount(((Number) trendMap.getOrDefault("total_error_count", 0)).longValue());
        vo.setTotalRetryCount(((Number) trendMap.getOrDefault("total_retry_count", 0)).longValue());
        vo.setAvgProcessTime(((Number) trendMap.getOrDefault("avg_process_time", 0)).doubleValue());
        vo.setAvgReceiveTime(((Number) trendMap.getOrDefault("avg_receive_time", 0)).doubleValue());
        vo.setAvgReadTime(((Number) trendMap.getOrDefault("avg_read_time", 0)).doubleValue());
        
        if (vo.getTotalSendCount() > 0) {
            vo.setSendSuccessRate((double) vo.getTotalSendSuccessCount() / vo.getTotalSendCount() * 100);
            vo.setReceiveRate((double) vo.getTotalReceiveCount() / vo.getTotalSendCount() * 100);
            vo.setReadRate((double) vo.getTotalReadCount() / vo.getTotalSendCount() * 100);
            vo.setErrorRate((double) vo.getTotalErrorCount() / vo.getTotalSendCount() * 100);
        }
        
        return vo;
    }

    private ErrorStatisticsVO convertToErrorStatisticsVO(Map<String, Object> errorMap) {
        ErrorStatisticsVO vo = new ErrorStatisticsVO();
        vo.setErrorType((String) errorMap.get("error_type"));
        vo.setErrorCount(((Number) errorMap.getOrDefault("error_count", 0)).longValue());
        vo.setAppKey((String) errorMap.get("app_key"));
        vo.setMsgType((String) errorMap.get("msg_type"));
        vo.setFirstOccurrence((String) errorMap.get("first_occurrence"));
        vo.setLastOccurrence((String) errorMap.get("last_occurrence"));
        vo.setAffectedApi((String) errorMap.get("affected_api"));
        
        // 计算错误占比
        long totalErrors = ((Number) errorMap.getOrDefault("total_errors", 1)).longValue();
        vo.setErrorRate((double) vo.getErrorCount() / totalErrors * 100);
        
        return vo;
    }

    private MsgStatisticsExportVO convertToExportVO(MsgStatisticsDetailVO detailVO) {
        MsgStatisticsExportVO vo = new MsgStatisticsExportVO();
        BeanUtils.copyProperties(detailVO, vo);
        return vo;
    }
}