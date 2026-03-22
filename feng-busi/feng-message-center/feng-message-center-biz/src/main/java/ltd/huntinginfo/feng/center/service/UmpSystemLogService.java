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
import ltd.huntinginfo.feng.center.api.entity.UmpSystemLog;
import ltd.huntinginfo.feng.center.api.dto.SystemLogQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统日志表服务接口
 */
public interface UmpSystemLogService extends IService<UmpSystemLog> {

    /**
     * 记录操作日志
     *
     * @param logLevel       日志级别
     * @param appKey         应用标识
     * @param operator       操作者
     * @param operation      操作名称
     * @param requestId      请求ID
     * @param apiPath        API路径
     * @param httpMethod     HTTP方法
     * @param requestParams  请求参数
     * @param responseCode   响应代码
     * @param responseMessage 响应消息
     * @param responseData   响应数据
     * @param ipAddress      IP地址
     * @param userAgent      用户代理
     * @param serverHost     服务器主机
     * @param costTime       耗时(ms)
     * @param memoryUsage    内存使用(KB)
     * @param errorMessage   错误信息
     * @param errorStack     错误堆栈
     * @return 日志ID
     */
    String recordOperationLog(String logLevel, String appKey, String operator,
                             String operation, String requestId, String apiPath,
                             String httpMethod, Map<String, Object> requestParams,
                             String responseCode, String responseMessage,
                             Map<String, Object> responseData, String ipAddress,
                             String userAgent, String serverHost, Integer costTime,
                             Integer memoryUsage, String errorMessage,
                             String errorStack);

    /**
     * 记录认证日志
     *
     * @param logLevel       日志级别
     * @param appKey         应用标识
     * @param operator       操作者
     * @param requestId      请求ID
     * @param apiPath        API路径
     * @param httpMethod     HTTP方法
     * @param requestParams  请求参数
     * @param authType       认证类型
     * @param authStatus     认证状态
     * @param authErrorCode  认证错误码
     * @param ipAddress      IP地址
     * @param userAgent      用户代理
     * @param serverHost     服务器主机
     * @param costTime       耗时(ms)
     * @return 日志ID
     */
    String recordAuthLog(String logLevel, String appKey, String operator, String requestId,
                        String apiPath, String httpMethod, Map<String, Object> requestParams,
                        String authType, Integer authStatus, String authErrorCode,
                        String ipAddress, String userAgent, String serverHost, Integer costTime);

    /**
     * 记录系统日志
     *
     * @param logLevel        日志级别
     * @param operation       操作名称
     * @param responseMessage 响应消息
     * @param errorMessage    错误信息
     * @param errorStack      错误堆栈
     * @return 日志ID
     */
    String recordSystemLog(String logLevel, String operation, String responseMessage,
                          String errorMessage, String errorStack);

    /**
     * 根据请求ID查询日志
     *
     * @param requestId 请求ID
     * @return 日志详情VO
     */
    SystemLogDetailVO getByRequestId(String requestId);

    /**
     * 分页查询日志
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<SystemLogPageVO> queryLogPage(SystemLogQueryDTO queryDTO);

    /**
     * 根据日志类型查询日志列表
     *
     * @param logType 日志类型
     * @param limit   限制数量（可选）
     * @return 日志列表
     */
    List<SystemLogDetailVO> getByLogType(String logType, Integer limit);

    /**
     * 根据日志级别查询日志列表
     *
     * @param logLevel 日志级别
     * @param limit    限制数量（可选）
     * @return 日志列表
     */
    List<SystemLogDetailVO> getByLogLevel(String logLevel, Integer limit);

    /**
     * 根据应用标识查询日志列表
     *
     * @param appKey 应用标识
     * @param limit  限制数量（可选）
     * @return 日志列表
     */
    List<SystemLogDetailVO> getByAppKey(String appKey, Integer limit);

    /**
     * 根据操作者查询日志列表
     *
     * @param operator 操作者
     * @param limit    限制数量（可选）
     * @return 日志列表
     */
    List<SystemLogDetailVO> getByOperator(String operator, Integer limit);

    /**
     * 获取日志统计信息
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param logType   日志类型（可选）
     * @param appKey    应用标识（可选）
     * @return 统计信息VO
     */
    SystemLogStatisticsVO getLogStatistics(LocalDateTime startTime, LocalDateTime endTime,
                                          String logType, String appKey);

    /**
     * 获取错误日志统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 错误统计列表
     */
    List<ErrorLogStatisticsVO> getErrorLogStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取性能统计信息
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 性能统计VO
     */
    LogPerformanceStatisticsVO getPerformanceStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取API调用统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return API调用统计列表
     */
    List<ApiCallStatisticsVO> getApiCallStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取操作者统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return 操作者统计列表
     */
    List<OperatorStatisticsVO> getOperatorStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 获取应用统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return 应用统计列表
     */
    List<AppStatisticsVO> getAppStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 清理过期日志（逻辑删除）
     *
     * @param days 保留天数
     * @return 清理数量
     */
    int cleanExpiredLogs(Integer days);

    /**
     * 批量删除日志（逻辑删除）
     *
     * @param ids 日志ID列表
     * @return 删除数量
     */
    int batchDeleteLogs(List<String> ids);

    /**
     * 获取日志趋势统计
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param interval  时间间隔（DAY-按天 HOUR-按小时）
     * @param logType   日志类型（可选）
     * @param logLevel  日志级别（可选）
     * @return 趋势统计列表
     */
    List<LogTrendVO> getLogTrendStatistics(LocalDateTime startTime, LocalDateTime endTime,
                                          String interval, String logType, String logLevel);

    /**
     * 获取错误日志详情
     *
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @param limit     限制数量（可选）
     * @return 错误日志列表
     */
    List<SystemLogDetailVO> getErrorLogs(LocalDateTime startTime, LocalDateTime endTime, Integer limit);

    /**
     * 搜索日志
     *
     * @param keyword 关键词
     * @param limit   限制数量（可选）
     * @return 日志列表
     */
    List<SystemLogDetailVO> searchLogs(String keyword, Integer limit);

    /**
     * 导出日志
     *
     * @param queryDTO 查询条件
     * @return 日志数据
     */
    List<SystemLogDetailVO> exportLogs(SystemLogQueryDTO queryDTO);
}