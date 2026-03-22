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
import ltd.huntinginfo.feng.center.api.entity.UmpSystemLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统日志表Mapper接口
 */
@Mapper
public interface UmpSystemLogMapper extends BaseMapper<UmpSystemLog> {

    /**
     * 根据请求ID查询日志
     *
     * @param requestId 请求ID
     * @return 日志实体
     */
    @Select("SELECT * FROM ump_system_log WHERE request_id = #{requestId} AND del_flag = '0' ORDER BY create_time DESC LIMIT 1")
    UmpSystemLog selectByRequestId(@Param("requestId") String requestId);

    /**
     * 分页查询日志列表
     *
     * @param page         分页参数
     * @param logType      日志类型（可选）
     * @param logLevel     日志级别（可选）
     * @param appKey       应用标识（可选）
     * @param operator     操作者（可选）
     * @param operation    操作名称（可选）
     * @param apiPath      API路径（可选）
     * @param responseCode 响应代码（可选）
     * @param authStatus   认证状态（可选）
     * @param startTime    开始时间（可选）
     * @param endTime      结束时间（可选）
     * @param keyword      关键词（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_log " +
            "WHERE del_flag = '0' " +
            "<if test='logType != null and logType != \"\"'> AND log_type = #{logType} </if>" +
            "<if test='logLevel != null and logLevel != \"\"'> AND log_level = #{logLevel} </if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='operator != null and operator != \"\"'> AND operator = #{operator} </if>" +
            "<if test='operation != null and operation != \"\"'> AND operation LIKE CONCAT('%', #{operation}, '%') </if>" +
            "<if test='apiPath != null and apiPath != \"\"'> AND api_path LIKE CONCAT('%', #{apiPath}, '%') </if>" +
            "<if test='responseCode != null and responseCode != \"\"'> AND response_code = #{responseCode} </if>" +
            "<if test='authStatus != null'> AND auth_status = #{authStatus} </if>" +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            " AND (" +
            "   request_id LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   operation LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   api_path LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   response_message LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   error_message LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   ip_address LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   user_agent LIKE CONCAT('%', #{keyword}, '%')" +
            " )" +
            "</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpSystemLog> selectLogPage(IPage<UmpSystemLog> page,
                                     @Param("logType") String logType,
                                     @Param("logLevel") String logLevel,
                                     @Param("appKey") String appKey,
                                     @Param("operator") String operator,
                                     @Param("operation") String operation,
                                     @Param("apiPath") String apiPath,
                                     @Param("responseCode") String responseCode,
                                     @Param("authStatus") Integer authStatus,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     @Param("keyword") String keyword);

    /**
     * 根据日志类型查询日志列表
     *
     * @param logType 日志类型
     * @param limit   限制数量（可选）
     * @return 日志列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_log WHERE log_type = #{logType} AND del_flag = '0' " +
            "ORDER BY create_time DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpSystemLog> selectByLogType(@Param("logType") String logType,
                                      @Param("limit") Integer limit);

    /**
     * 根据日志级别查询日志列表
     *
     * @param logLevel 日志级别
     * @param limit    限制数量（可选）
     * @return 日志列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_log WHERE log_level = #{logLevel} AND del_flag = '0' " +
            "ORDER BY create_time DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpSystemLog> selectByLogLevel(@Param("logLevel") String logLevel,
                                       @Param("limit") Integer limit);

    /**
     * 根据应用标识查询日志列表
     *
     * @param appKey 应用标识
     * @param limit  限制数量（可选）
     * @return 日志列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_log WHERE app_key = #{appKey} AND del_flag = '0' " +
            "ORDER BY create_time DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpSystemLog> selectByAppKey(@Param("appKey") String appKey,
                                     @Param("limit") Integer limit);

    /**
     * 根据操作者查询日志列表
     *
     * @param operator 操作者
     * @param limit    限制数量（可选）
     * @return 日志列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_log WHERE operator = #{operator} AND del_flag = '0' " +
            "ORDER BY create_time DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpSystemLog> selectByOperator(@Param("operator") String operator,
                                       @Param("limit") Integer limit);

    /**
     * 获取日志统计信息
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param logType   日志类型（可选）
     * @param appKey    应用标识（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN log_type = 'AUTH' THEN 1 ELSE 0 END) AS auth_count, " +
            "SUM(CASE WHEN log_type = 'OPERATION' THEN 1 ELSE 0 END) AS operation_count, " +
            "SUM(CASE WHEN log_type = 'SYSTEM' THEN 1 ELSE 0 END) AS system_count, " +
            "SUM(CASE WHEN log_level = 'INFO' THEN 1 ELSE 0 END) AS info_count, " +
            "SUM(CASE WHEN log_level = 'WARN' THEN 1 ELSE 0 END) AS warn_count, " +
            "SUM(CASE WHEN log_level = 'ERROR' THEN 1 ELSE 0 END) AS error_count, " +
            "SUM(CASE WHEN log_level = 'DEBUG' THEN 1 ELSE 0 END) AS debug_count, " +
            "SUM(CASE WHEN log_type = 'AUTH' AND auth_status = 1 THEN 1 ELSE 0 END) AS success_auth_count, " +
            "SUM(CASE WHEN log_type = 'AUTH' AND auth_status = 0 THEN 1 ELSE 0 END) AS failed_auth_count " +
            "FROM ump_system_log " +
            "WHERE del_flag = '0' " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            "<if test='logType != null and logType != \"\"'> AND log_type = #{logType} </if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "</script>")
    Map<String, Object> selectLogStatistics(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("logType") String logType,
                                           @Param("appKey") String appKey);

    /**
     * 获取错误日志统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 错误统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "log_level AS error_type, " +
            "error_message, " +
            "COUNT(*) AS error_count, " +
            "MIN(create_time) AS first_occurrence, " +
            "MAX(create_time) AS last_occurrence, " +
            "api_path AS affected_api, " +
            "app_key AS affected_app " +
            "FROM ump_system_log " +
            "WHERE log_level = 'ERROR' " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if> AND del_flag = '0'" +
            "GROUP BY error_message, api_path, app_key " +
            "ORDER BY error_count DESC" +
            "</script>")
    List<Map<String, Object>> selectErrorLogStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 获取性能统计信息（使用窗口函数，MySQL 8.0+）
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 性能统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_requests, " +
            "AVG(cost_time) AS avg_cost_time, " +
            "MAX(cost_time) AS max_cost_time, " +
            "MIN(cost_time) AS min_cost_time, " +
            "AVG(memory_usage) AS avg_memory_usage, " +
            "MAX(memory_usage) AS max_memory_usage, " +
            "SUM(CASE WHEN cost_time > 1000 THEN 1 ELSE 0 END) AS slow_requests, " +
            // 使用 PERCENTILE_CONT 窗口函数计算分位数 (MySQL 8.0.4+)
            "(SELECT PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY cost_time) OVER() FROM ump_system_log WHERE cost_time IS NOT NULL AND create_time BETWEEN #{startTime} AND #{endTime} LIMIT 1) AS p95_cost_time, " +
            "(SELECT PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY cost_time) OVER() FROM ump_system_log WHERE cost_time IS NOT NULL AND create_time BETWEEN #{startTime} AND #{endTime} LIMIT 1) AS p99_cost_time " +
            "FROM ump_system_log " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} AND del_flag = '0'" +
            "</script>")
    Map<String, Object> selectPerformanceStatistics(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 获取API调用统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return API调用统计
     */
    @Select("<script>" +
            "SELECT " +
            "api_path, " +
            "http_method, " +
            "COUNT(*) AS call_count, " +
            "SUM(CASE WHEN response_code = 'SUCCESS' OR response_code = '200' THEN 1 ELSE 0 END) AS success_count, " +
            "SUM(CASE WHEN response_code = 'ERROR' OR response_code LIKE '5%' THEN 1 ELSE 0 END) AS error_count, " +
            "AVG(cost_time) AS avg_cost_time, " +
            "MAX(cost_time) AS max_cost_time " +
            "FROM ump_system_log " +
            "WHERE api_path IS NOT NULL " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if> AND del_flag = '0'" +
            "GROUP BY api_path, http_method " +
            "ORDER BY call_count DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<Map<String, Object>> selectApiCallStatistics(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime,
                                                     @Param("limit") Integer limit);

    /**
     * 获取操作者统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return 操作者统计
     */
    @Select("<script>" +
            "SELECT " +
            "operator, " +
            "COUNT(*) AS operation_count, " +
            "SUM(CASE WHEN response_code = 'SUCCESS' OR response_code = '200' THEN 1 ELSE 0 END) AS success_count, " +
            "SUM(CASE WHEN response_code = 'ERROR' OR response_code LIKE '5%' THEN 1 ELSE 0 END) AS error_count, " +
            "MAX(create_time) AS last_operation_time, " +
            // 这里使用子查询获取最频繁操作，避免 MAX 的不准确
            "(SELECT operation FROM ump_system_log op2 WHERE op2.operator = main.operator GROUP BY operation ORDER BY COUNT(*) DESC LIMIT 1) AS frequent_operation " +
            "FROM ump_system_log main " +
            "WHERE operator IS NOT NULL " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if> AND del_flag = '0'" +
            "GROUP BY operator " +
            "ORDER BY operation_count DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<Map<String, Object>> selectOperatorStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("limit") Integer limit);

    /**
     * 获取应用统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return 应用统计
     */
    @Select("<script>" +
            "SELECT " +
            "app_key, " +
            "COUNT(*) AS request_count, " +
            "SUM(CASE WHEN response_code = 'SUCCESS' OR response_code = '200' THEN 1 ELSE 0 END) AS success_count, " +
            "SUM(CASE WHEN response_code = 'ERROR' OR response_code LIKE '5%' THEN 1 ELSE 0 END) AS error_count, " +
            "AVG(cost_time) AS avg_cost_time, " +
            "MAX(create_time) AS last_request_time, " +
            // 子查询获取最频繁API
            "(SELECT api_path FROM ump_system_log api WHERE api.app_key = main.app_key GROUP BY api_path ORDER BY COUNT(*) DESC LIMIT 1) AS frequent_api " +
            "FROM ump_system_log main " +
            "WHERE app_key IS NOT NULL " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if> AND del_flag = '0'" +
            "GROUP BY app_key " +
            "ORDER BY request_count DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<Map<String, Object>> selectAppStatistics(@Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("limit") Integer limit);

    /**
     * 逻辑删除过期日志（替代物理删除）
     *
     * @param beforeTime 清理此时间之前的日志
     * @return 更新条数
     */
    @Update("UPDATE ump_system_log SET del_flag = '1' WHERE create_time < #{beforeTime} AND del_flag = '0'")
    int cleanExpiredLogs(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量逻辑删除日志
     *
     * @param ids 日志ID列表
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_system_log SET del_flag = '1' WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
            "</script>")
    int batchDeleteLogs(@Param("ids") List<String> ids);

    /**
     * 获取日志趋势统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔（DAY-按天 HOUR-按小时）
     * @param logType   日志类型（可选）
     * @param logLevel  日志级别（可选）
     * @return 趋势统计
     */
    @Select("<script>" +
            "SELECT " +
            "DATE_FORMAT(create_time, " +
            "<choose>" +
            "<when test='interval == \"HOUR\"'>'%Y-%m-%d %H:00'</when>" +
            "<otherwise>'%Y-%m-%d'</otherwise>" +
            "</choose>" +
            ") AS time_period, " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN log_type = 'AUTH' THEN 1 ELSE 0 END) AS auth_count, " +
            "SUM(CASE WHEN log_type = 'OPERATION' THEN 1 ELSE 0 END) AS operation_count, " +
            "SUM(CASE WHEN log_type = 'SYSTEM' THEN 1 ELSE 0 END) AS system_count, " +
            "SUM(CASE WHEN log_level = 'INFO' THEN 1 ELSE 0 END) AS info_count, " +
            "SUM(CASE WHEN log_level = 'WARN' THEN 1 ELSE 0 END) AS warn_count, " +
            "SUM(CASE WHEN log_level = 'ERROR' THEN 1 ELSE 0 END) AS error_count " +
            "FROM ump_system_log " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "<if test='logType != null and logType != \"\"'> AND log_type = #{logType} </if>" +
            "<if test='logLevel != null and logLevel != \"\"'> AND log_level = #{logLevel} </if> AND del_flag = '0'" +
            "GROUP BY time_period " +
            "ORDER BY time_period ASC" +
            "</script>")
    List<Map<String, Object>> selectLogTrendStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("interval") String interval,
                                                      @Param("logType") String logType,
                                                      @Param("logLevel") String logLevel);

    /**
     * 获取错误日志详情
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return 错误日志列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_log WHERE log_level = 'ERROR' " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if> AND del_flag = '0'" +
            "ORDER BY create_time DESC " +
            "<if test='limit != null'> LIMIT #{limit} </if>" +
            "</script>")
    List<UmpSystemLog> selectErrorLogs(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("limit") Integer limit);
}