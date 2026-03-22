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
package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgStatistics;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 消息统计表Mapper接口
 */
@Mapper
public interface UmpMsgStatisticsMapper extends BaseMapper<UmpMsgStatistics> {

    /**
     * 根据统计日期和应用标识查询统计记录
     *
     * @param statDate 统计日期
     * @param appKey   应用标识
     * @return 统计实体
     */
    @Select("SELECT * FROM ump_msg_statistics WHERE stat_date = #{statDate} AND app_key = #{appKey} AND del_flag = '0'")
    UmpMsgStatistics selectByDateAndApp(@Param("statDate") LocalDate statDate,
                                        @Param("appKey") String appKey);

    /**
     * 分页查询统计记录
     *
     * @param page           分页参数
     * @param statDateStart  统计日期开始（可选）
     * @param statDateEnd    统计日期结束（可选）
     * @param appKey         应用标识（可选）
     * @param msgType        消息类型（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_statistics WHERE del_flag = '0' " +
            "<if test='statDateStart != null'> AND stat_date >= #{statDateStart} </if>" +
            "<if test='statDateEnd != null'> AND stat_date &lt;= #{statDateEnd} </if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='msgType != null and msgType != \"\"'> AND msg_type = #{msgType} </if>" +
            " ORDER BY stat_date DESC, send_count DESC" +
            "</script>")
    IPage<UmpMsgStatistics> selectStatisticsPage(IPage<UmpMsgStatistics> page,
                                                @Param("statDateStart") LocalDate statDateStart,
                                                @Param("statDateEnd") LocalDate statDateEnd,
                                                @Param("appKey") String appKey,
                                                @Param("msgType") String msgType);

    /**
     * 根据日期范围查询统计记录
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param appKey    应用标识（可选）
     * @param msgType   消息类型（可选）
     * @return 统计记录列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_statistics WHERE stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='msgType != null and msgType != \"\"'> AND msg_type = #{msgType} </if> AND del_flag = '0'" +
            " ORDER BY stat_date, app_key, msg_type" +
            "</script>")
    List<UmpMsgStatistics> selectByDateRange(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("appKey") String appKey,
                                            @Param("msgType") String msgType);

    /**
     * 根据应用标识查询统计记录
     *
     * @param appKey 应用标识
     * @param limit  限制数量（可选）
     * @return 统计记录列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_statistics WHERE app_key = #{appKey} AND del_flag = '0' " +
            "ORDER BY stat_date DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpMsgStatistics> selectByAppKey(@Param("appKey") String appKey,
                                         @Param("limit") Integer limit);

    /**
     * 根据消息类型查询统计记录
     *
     * @param msgType 消息类型
     * @param limit   限制数量（可选）
     * @return 统计记录列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_statistics WHERE msg_type = #{msgType} AND del_flag = '0' " +
            "ORDER BY stat_date DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpMsgStatistics> selectByMsgType(@Param("msgType") String msgType,
                                          @Param("limit") Integer limit);

    /**
     * 获取统计汇总信息
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @param appKey    应用标识（可选）
     * @param msgType   消息类型（可选）
     * @return 汇总统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "SUM(send_count) AS total_send_count, " +
            "SUM(send_success_count) AS total_send_success_count, " +
            "SUM(send_failed_count) AS total_send_failed_count, " +
            "SUM(receive_count) AS total_receive_count, " +
            "SUM(read_count) AS total_read_count, " +
            "SUM(error_count) AS total_error_count, " +
            "SUM(retry_count) AS total_retry_count, " +
            "AVG(avg_process_time) AS avg_process_time, " +
            "AVG(avg_receive_time) AS avg_receive_time, " +
            "AVG(avg_read_time) AS avg_read_time " +
            "FROM ump_msg_statistics " +
            "WHERE del_flag = '0' " +
            "<if test='startDate != null'> AND stat_date >= #{startDate} </if>" +
            "<if test='endDate != null'> AND stat_date &lt;= #{endDate} </if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='msgType != null and msgType != \"\"'> AND msg_type = #{msgType} </if>" +
            "</script>")
    Map<String, Object> selectStatisticsSummary(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("appKey") String appKey,
                                               @Param("msgType") String msgType);

    /**
     * 获取应用统计排名
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param limit     限制数量
     * @return 应用统计排名
     */
    @Select("SELECT app_key, " +
            "SUM(send_count) AS send_count, " +
            "SUM(send_success_count) AS success_count, " +
            "SUM(receive_count) AS receive_count, " +
            "SUM(read_count) AS read_count, " +
            "SUM(error_count) AS error_count " +
            "FROM ump_msg_statistics " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate} AND del_flag = '0' " +
            "GROUP BY app_key " +
            "ORDER BY send_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectAppRanking(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("limit") Integer limit);

    /**
     * 获取消息类型统计排名
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param limit     限制数量
     * @return 消息类型统计排名
     */
    @Select("SELECT msg_type, " +
            "SUM(send_count) AS send_count, " +
            "SUM(send_success_count) AS success_count, " +
            "SUM(receive_count) AS receive_count, " +
            "SUM(read_count) AS read_count, " +
            "SUM(error_count) AS error_count " +
            "FROM ump_msg_statistics " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate} AND del_flag = '0' " +
            "GROUP BY msg_type " +
            "ORDER BY send_count DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectMsgTypeRanking(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("limit") Integer limit);

    /**
     * 获取统计趋势数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param appKey    应用标识（可选）
     * @param msgType   消息类型（可选）
     * @param interval  时间间隔（DAY-按天 WEEK-按周 MONTH-按月）
     * @return 趋势数据
     */
    @Select("<script>" +
            "SELECT " +
            "<choose>" +
            "<when test='interval == \"WEEK\"'>DATE_FORMAT(stat_date, '%x-W%v')</when>" +
            "<when test='interval == \"MONTH\"'>DATE_FORMAT(stat_date, '%Y-%m')</when>" +
            "<otherwise>stat_date</otherwise>" +
            "</choose> AS time_period, " +
            "SUM(send_count) AS total_send_count, " +
            "SUM(send_success_count) AS total_send_success_count, " +
            "SUM(send_failed_count) AS total_send_failed_count, " +
            "SUM(receive_count) AS total_receive_count, " +
            "SUM(read_count) AS total_read_count, " +
            "SUM(error_count) AS total_error_count, " +
            "SUM(retry_count) AS total_retry_count, " +
            "AVG(avg_process_time) AS avg_process_time, " +
            "AVG(avg_receive_time) AS avg_receive_time, " +
            "AVG(avg_read_time) AS avg_read_time " +
            "FROM ump_msg_statistics " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='msgType != null and msgType != \"\"'> AND msg_type = #{msgType} </if> AND del_flag = '0'" +
            "GROUP BY time_period " +
            "ORDER BY time_period" +
            "</script>")
    List<Map<String, Object>> selectStatisticsTrend(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("appKey") String appKey,
                                                   @Param("msgType") String msgType,
                                                   @Param("interval") String interval);

    /**
     * 获取性能统计信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param appKey    应用标识（可选）
     * @return 性能统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "SUM(send_count) AS total_send_count, " +
            "AVG(avg_process_time) AS avg_process_time, " +
            "AVG(avg_receive_time) AS avg_receive_time, " +
            "AVG(avg_read_time) AS avg_read_time, " +
            "MAX(avg_process_time) AS max_process_time, " +
            "MAX(avg_receive_time) AS max_receive_time, " +
            "MAX(avg_read_time) AS max_read_time, " +
            "PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY avg_process_time) OVER() AS p95_process_time, " +
            "PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY avg_receive_time) OVER() AS p95_receive_time, " +
            "PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY avg_read_time) OVER() AS p95_read_time, " +
            "SUM(CASE WHEN avg_process_time > 1000 THEN 1 ELSE 0 END) AS slow_messages " +
            "FROM ump_msg_statistics " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate} " +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if> AND del_flag = '0'" +
            "</script>")
    Map<String, Object> selectPerformanceStatistics(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("appKey") String appKey);

    /**
     * 获取错误统计信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param appKey    应用标识（可选）
     * @return 错误统计结果 注：由于没有记录错误类型，所以假设所有错误均为SYSTEM_ERROR
     */
    @Select("<script>" +
            "SELECT " +
            "error_type, " +
            "SUM(error_count) AS error_count, " +
            "app_key, " +
            "msg_type, " +
            "MIN(first_occurrence) AS first_occurrence, " +
            "MAX(last_occurrence) AS last_occurrence, " +
            "affected_api, " +
            "(SELECT SUM(error_count) FROM ump_msg_statistics WHERE stat_date BETWEEN #{startDate} AND #{endDate}) AS total_errors " +
            "FROM ( " +
            "  SELECT " +
            "  'SYSTEM_ERROR' AS error_type, " +
            "  error_count, " +
            "  app_key, " +
            "  msg_type, " +
            "  MIN(stat_date) AS first_occurrence, " +
            "  MAX(stat_date) AS last_occurrence, " +
            "  NULL AS affected_api " +
            "  FROM ump_msg_statistics " +
            "  WHERE error_count > 0 " +
            "  AND stat_date BETWEEN #{startDate} AND #{endDate} AND del_flag = '0' " +
            "  <if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "  GROUP BY app_key, msg_type " +
            ") AS error_data " +
            "GROUP BY error_type, app_key, msg_type, affected_api " +
            "ORDER BY error_count DESC" +
            "</script>")
    List<Map<String, Object>> selectErrorStatistics(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("appKey") String appKey);

    /**
     * 批量插入统计记录
     *
     * @param statisticsList 统计记录列表
     * @return 插入条数
     */
    @Insert("<script>" +
            "INSERT INTO ump_msg_statistics (stat_date, app_key, msg_type, " +
            "send_count, send_success_count, send_failed_count, receive_count, read_count, " +
            "avg_process_time, avg_receive_time, avg_read_time, error_count, retry_count) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>(" +
            "#{item.statDate}, #{item.appKey}, #{item.msgType}, " +
            "#{item.sendCount}, #{item.sendSuccessCount}, #{item.sendFailedCount}, " +
            "#{item.receiveCount}, #{item.readCount}, #{item.avgProcessTime}, " +
            "#{item.avgReceiveTime}, #{item.avgReadTime}, #{item.errorCount}, #{item.retryCount})" +
            "</foreach>" +
            "</script>")
    int batchInsertStatistics(@Param("list") List<UmpMsgStatistics> statisticsList);

    /**
     * 原子性插入或更新统计记录（使用 ON DUPLICATE KEY UPDATE）
     * 要求表上有唯一索引 (stat_date, app_key, msg_type)
     *
     * @param id                主键ID
     * @param statDate          统计日期
     * @param appKey            应用标识
     * @param msgType           消息类型
     * @param sendCount         发送数量（不能为 null，若需增加0则传0）
     * @param sendSuccessCount  发送成功数量（不能为 null）
     * @param sendFailedCount   发送失败数量（不能为 null）
     * @param receiveCount      接收数量（不能为 null）
     * @param readCount         阅读数量（不能为 null）
     * @param errorCount        错误数量（不能为 null）
     * @param retryCount        重试数量（不能为 null）
     * @param processTime       处理时间（若为 null，更新时不改变原有值；插入时默认0）
     * @param receiveTime       接收时间（同上）
     * @param readTime          阅读时间（同上）
     * @return 影响行数（插入返回1，更新返回2）
     */
    @Insert("INSERT INTO ump_msg_statistics " +
            "(id, stat_date, app_key, msg_type, send_count, send_success_count, send_failed_count, " +
            "receive_count, read_count, error_count, retry_count, avg_process_time, avg_receive_time, avg_read_time) " +
            "VALUES " +
            "(#{id}, #{statDate}, #{appKey}, #{msgType}, #{sendCount}, #{sendSuccessCount}, #{sendFailedCount}, " +
            "#{receiveCount}, #{readCount}, #{errorCount}, #{retryCount}, " +
            "COALESCE(#{processTime}, 0), COALESCE(#{receiveTime}, 0), COALESCE(#{readTime}, 0)) " +
            "ON DUPLICATE KEY UPDATE " +
            "send_count = send_count + #{sendCount}, " +
            "send_success_count = send_success_count + #{sendSuccessCount}, " +
            "send_failed_count = send_failed_count + #{sendFailedCount}, " +
            "receive_count = receive_count + #{receiveCount}, " +
            "read_count = read_count + #{readCount}, " +
            "error_count = error_count + #{errorCount}, " +
            "retry_count = retry_count + #{retryCount}, " +
            "avg_process_time = CASE WHEN #{processTime} IS NOT NULL THEN #{processTime} ELSE avg_process_time END, " +
            "avg_receive_time = CASE WHEN #{receiveTime} IS NOT NULL THEN #{receiveTime} ELSE avg_receive_time END, " +
            "avg_read_time = CASE WHEN #{readTime} IS NOT NULL THEN #{readTime} ELSE avg_read_time END, " +
            "update_time = NOW()")
    int upsertStatistics(@Param("id") String id,
                         @Param("statDate") LocalDate statDate,
                         @Param("appKey") String appKey,
                         @Param("msgType") String msgType,
                         @Param("sendCount") Integer sendCount,
                         @Param("sendSuccessCount") Integer sendSuccessCount,
                         @Param("sendFailedCount") Integer sendFailedCount,
                         @Param("receiveCount") Integer receiveCount,
                         @Param("readCount") Integer readCount,
                         @Param("errorCount") Integer errorCount,
                         @Param("retryCount") Integer retryCount,
                         @Param("processTime") Integer processTime,
                         @Param("receiveTime") Integer receiveTime,
                         @Param("readTime") Integer readTime);

    /**
     * 检查统计记录是否存在
     *
     * @param statDate 统计日期
     * @param appKey   应用标识
     * @param msgType  消息类型
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) > 0 FROM ump_msg_statistics WHERE stat_date = #{statDate} AND app_key = #{appKey} AND msg_type = #{msgType} AND del_flag = '0'")
    boolean existsStatistics(@Param("statDate") LocalDate statDate,
                            @Param("appKey") String appKey,
                            @Param("msgType") String msgType);

    /**
     * 清理过期统计记录
     *
     * @param beforeDate 清理此日期之前的记录
     * @return 清理数量
     */
    @Delete("UPDATE ump_msg_statistics SET del_flag = '1' WHERE stat_date < #{beforeDate} AND del_flag = '0'")
    int cleanExpiredStatistics(@Param("beforeDate") LocalDate beforeDate);
}