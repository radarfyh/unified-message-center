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
package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgStatistics;
import ltd.huntinginfo.feng.center.api.dto.MsgStatisticsQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 消息统计表服务接口
 */
public interface UmpMsgStatisticsService extends IService<UmpMsgStatistics> {

    /**
     * 创建或更新统计记录
     *
     * @param statDate 统计日期
     * @param appKey 应用标识
     * @param msgType 消息类型
     * @param sendCount 发送数量增量
     * @param sendSuccessCount 发送成功数量增量
     * @param sendFailedCount 发送失败数量增量
     * @param receiveCount 接收数量增量
     * @param readCount 阅读数量增量
     * @param errorCount 错误数量增量
     * @param retryCount 重试数量增量
     * @param processTime 处理时间
     * @param receiveTime 接收时间
     * @param readTime 阅读时间
     * @return 是否成功
     */
    boolean upsertStatistics(LocalDate statDate, String appKey, String msgType,
                            Integer sendCount, Integer sendSuccessCount, Integer sendFailedCount,
                            Integer receiveCount, Integer readCount, Integer errorCount,
                            Integer retryCount, Integer processTime, Integer receiveTime,
                            Integer readTime);

    /**
     * 根据统计日期和应用标识查询统计记录
     *
     * @param statDate 统计日期
     * @param appKey 应用标识
     * @return 统计详情VO
     */
    MsgStatisticsDetailVO getByDateAndApp(LocalDate statDate, String appKey);

    /**
     * 分页查询统计记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<MsgStatisticsPageVO> queryStatisticsPage(MsgStatisticsQueryDTO queryDTO);

    /**
     * 根据日期范围查询统计记录
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param appKey 应用标识（可选）
     * @param msgType 消息类型（可选）
     * @return 统计记录列表
     */
    List<MsgStatisticsDetailVO> getByDateRange(LocalDate startDate, LocalDate endDate,
                                              String appKey, String msgType);

    /**
     * 根据应用标识查询统计记录
     *
     * @param appKey 应用标识
     * @param limit 限制数量（可选）
     * @return 统计记录列表
     */
    List<MsgStatisticsDetailVO> getByAppKey(String appKey, Integer limit);

    /**
     * 根据消息类型查询统计记录
     *
     * @param msgType 消息类型
     * @param limit 限制数量（可选）
     * @return 统计记录列表
     */
    List<MsgStatisticsDetailVO> getByMsgType(String msgType, Integer limit);

    /**
     * 获取统计汇总信息
     *
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param appKey 应用标识（可选）
     * @param msgType 消息类型（可选）
     * @return 汇总统计VO
     */
    MsgStatisticsSummaryVO getStatisticsSummary(LocalDate startDate, LocalDate endDate,
                                               String appKey, String msgType);

    /**
     * 获取应用统计排名
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制数量
     * @return 应用统计排名列表
     */
    List<AppStatisticsRankingVO> getAppRanking(LocalDate startDate, LocalDate endDate, Integer limit);

    /**
     * 获取消息类型统计排名
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制数量
     * @return 消息类型统计排名列表
     */
    List<MsgTypeStatisticsRankingVO> getMsgTypeRanking(LocalDate startDate, LocalDate endDate, Integer limit);

    /**
     * 获取统计趋势数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param appKey 应用标识（可选）
     * @param msgType 消息类型（可选）
     * @param interval 时间间隔（DAY-按天 WEEK-按周 MONTH-按月）
     * @return 趋势数据列表
     */
    List<StatisticsTrendVO> getStatisticsTrend(LocalDate startDate, LocalDate endDate,
                                              String appKey, String msgType, String interval);

    /**
     * 获取性能统计信息
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param appKey 应用标识（可选）
     * @return 性能统计VO
     */
    PerformanceStatisticsVO getPerformanceStatistics(LocalDate startDate, LocalDate endDate, String appKey);

    /**
     * 获取错误统计信息
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param appKey 应用标识（可选）
     * @return 错误统计列表
     */
    List<ErrorStatisticsVO> getErrorStatistics(LocalDate startDate, LocalDate endDate, String appKey);

    /**
     * 批量生成统计记录
     *
     * @param statisticsList 统计记录列表
     * @return 是否成功
     */
    boolean batchCreateStatistics(List<UmpMsgStatistics> statisticsList);

    /**
     * 检查统计记录是否存在
     *
     * @param statDate 统计日期
     * @param appKey 应用标识
     * @param msgType 消息类型
     * @return 是否存在
     */
    boolean existsStatistics(LocalDate statDate, String appKey, String msgType);

    /**
     * 清理过期统计记录
     *
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanExpiredStatistics(Integer days);

    /**
     * 获取统计图表数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param appKey 应用标识（可选）
     * @param chartType 图表类型（SEND-发送统计 RECEIVE-接收统计 READ-阅读统计 ERROR-错误统计）
     * @return 图表数据
     */
    Map<String, Object> getChartData(LocalDate startDate, LocalDate endDate,
                                    String appKey, String chartType);

    /**
     * 导出统计报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param appKey 应用标识（可选）
     * @param msgType 消息类型（可选）
     * @return 统计报表数据
     */
    List<MsgStatisticsExportVO> exportStatisticsReport(LocalDate startDate, LocalDate endDate,
                                                      String appKey, String msgType);

    /**
     * 获取实时统计概览
     *
     * @return 实时统计概览VO
     */
    RealTimeStatisticsOverviewVO getRealTimeOverview();
}