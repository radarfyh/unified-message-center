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
import ltd.huntinginfo.feng.center.api.dto.SystemLogQueryDTO;
import ltd.huntinginfo.feng.center.api.entity.UmpSystemLog;
import ltd.huntinginfo.feng.center.api.vo.*;
import ltd.huntinginfo.feng.center.mapper.UmpSystemLogMapper;
import ltd.huntinginfo.feng.center.service.UmpSystemLogService;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统日志表服务实现类
 * 打印日志（@Slf4j），异常日志使用log.error,一般错误使用log.warn
 * 各个方法返回有效数据，一般不返回错误代码，错误代码（BusinessEnum）使用异常（BusinessException）来控制
 * 使用baseMapper访问自身数据库映射接口（xxxMapper）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpSystemLogServiceImpl extends ServiceImpl<UmpSystemLogMapper, UmpSystemLog> implements UmpSystemLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordOperationLog(String logLevel, String appKey, String operator,
                                    String operation, String requestId, String apiPath,
                                    String httpMethod, Map<String, Object> requestParams,
                                    String responseCode, String responseMessage,
                                    Map<String, Object> responseData, String ipAddress,
                                    String userAgent, String serverHost, Integer costTime,
                                    Integer memoryUsage, String errorMessage,
                                    String errorStack) {
        // 参数校验
        if (!StringUtils.hasText(operation)) {
            throw new IllegalArgumentException("操作名称不能为空");
        }

        UmpSystemLog umpSystemLog = new UmpSystemLog();
        umpSystemLog.setLogType(MqMessageEventConstants.SystemLogType.OPERATION);
        umpSystemLog.setLogLevel(logLevel != null ? logLevel : "INFO");
        umpSystemLog.setAppKey(appKey);
        umpSystemLog.setOperator(operator);
        umpSystemLog.setOperation(operation);
        umpSystemLog.setRequestId(requestId);
        umpSystemLog.setApiPath(apiPath);
        umpSystemLog.setHttpMethod(httpMethod);
        umpSystemLog.setRequestParams(requestParams);
        umpSystemLog.setResponseCode(responseCode);
        umpSystemLog.setResponseMessage(responseMessage);
        umpSystemLog.setResponseData(responseData);
        umpSystemLog.setIpAddress(ipAddress);
        umpSystemLog.setUserAgent(userAgent);
        umpSystemLog.setServerHost(serverHost);
        umpSystemLog.setCostTime(costTime);
        umpSystemLog.setMemoryUsage(memoryUsage);
        umpSystemLog.setErrorMessage(errorMessage);
        umpSystemLog.setErrorStack(errorStack);

        // createTime 由自动填充处理器处理，此处无需手动设置

        if (save(umpSystemLog)) {
            if ("ERROR".equals(logLevel)) {
                log.info("操作日志记录成功（错误日志），请求ID: {}, 操作: {}, 错误: {}",
                         requestId, operation, errorMessage);
            } else {
                log.debug("操作日志记录成功，请求ID: {}, 操作: {}", requestId, operation);
            }
            return umpSystemLog.getId();
        } else {
            log.error("操作日志记录失败，请求ID: {}, 操作: {}", requestId, operation);
            throw new RuntimeException("操作日志记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordAuthLog(String logLevel, String appKey, String operator, String requestId,
                               String apiPath, String httpMethod, Map<String, Object> requestParams,
                               String authType, Integer authStatus, String authErrorCode,
                               String ipAddress, String userAgent, String serverHost, Integer costTime) {
        UmpSystemLog umpSystemLog = new UmpSystemLog();
        umpSystemLog.setLogType(MqMessageEventConstants.SystemLogType.AUTH);
        umpSystemLog.setLogLevel(logLevel != null ? logLevel : (authStatus == 1 ? "INFO" : "ERROR"));
        umpSystemLog.setAppKey(appKey);
        umpSystemLog.setOperator(operator);
        umpSystemLog.setOperation("认证");
        umpSystemLog.setRequestId(requestId);
        umpSystemLog.setApiPath(apiPath);
        umpSystemLog.setHttpMethod(httpMethod);
        umpSystemLog.setRequestParams(requestParams);
        umpSystemLog.setAuthType(authType);
        umpSystemLog.setAuthStatus(authStatus);
        umpSystemLog.setAuthErrorCode(authErrorCode);
        umpSystemLog.setIpAddress(ipAddress);
        umpSystemLog.setUserAgent(userAgent);
        umpSystemLog.setServerHost(serverHost);
        umpSystemLog.setCostTime(costTime);
        // createTime 由自动填充处理器处理

        if (save(umpSystemLog)) {
            log.debug("认证日志记录成功，请求ID: {}, 应用标识: {}, 认证状态: {}",
                     requestId, appKey, authStatus);
            return umpSystemLog.getId();
        } else {
            log.error("认证日志记录失败，请求ID: {}, 应用标识: {}", requestId, appKey);
            throw new RuntimeException("认证日志记录失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recordSystemLog(String logLevel, String operation, String responseMessage,
                                 String errorMessage, String errorStack) {
        if (!StringUtils.hasText(operation)) {
            throw new IllegalArgumentException("操作名称不能为空");
        }

        UmpSystemLog umpSystemLog = new UmpSystemLog();
        umpSystemLog.setLogType(MqMessageEventConstants.SystemLogType.SYSTEM);
        umpSystemLog.setLogLevel(logLevel != null ? logLevel : "INFO");
        umpSystemLog.setOperation(operation);
        umpSystemLog.setResponseMessage(responseMessage);
        umpSystemLog.setErrorMessage(errorMessage);
        umpSystemLog.setErrorStack(errorStack);
        // createTime 由自动填充处理器处理

        if (save(umpSystemLog)) {
            if ("ERROR".equals(logLevel)) {
                log.info("系统日志记录成功（错误日志），操作: {}, 错误: {}", operation, errorMessage);
            } else {
                log.debug("系统日志记录成功，操作: {}", operation);
            }
            return umpSystemLog.getId();
        } else {
            log.error("系统日志记录失败，操作: {}", operation);
            throw new RuntimeException("系统日志记录失败");
        }
    }

    @Override
    public SystemLogDetailVO getByRequestId(String requestId) {
        if (!StringUtils.hasText(requestId)) {
            throw new IllegalArgumentException("请求ID不能为空");
        }

        UmpSystemLog log = baseMapper.selectByRequestId(requestId);
        return convertToDetailVO(log);
    }

    @Override
    public Page<SystemLogPageVO> queryLogPage(SystemLogQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpSystemLog> queryWrapper = buildQueryWrapper(queryDTO);

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
        }

        // 执行分页查询
        Page<UmpSystemLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpSystemLog> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<SystemLogPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<SystemLogPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<SystemLogDetailVO> getByLogType(String logType, Integer limit) {
        if (!StringUtils.hasText(logType)) {
            throw new IllegalArgumentException("日志类型不能为空");
        }

        List<UmpSystemLog> logs = baseMapper.selectByLogType(logType, limit);
        return logs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getByLogLevel(String logLevel, Integer limit) {
        if (!StringUtils.hasText(logLevel)) {
            throw new IllegalArgumentException("日志级别不能为空");
        }

        List<UmpSystemLog> logs = baseMapper.selectByLogLevel(logLevel, limit);
        return logs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getByAppKey(String appKey, Integer limit) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        List<UmpSystemLog> logs = baseMapper.selectByAppKey(appKey, limit);
        return logs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getByOperator(String operator, Integer limit) {
        if (!StringUtils.hasText(operator)) {
            throw new IllegalArgumentException("操作者不能为空");
        }

        List<UmpSystemLog> logs = baseMapper.selectByOperator(operator, limit);
        return logs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    @Override
    public SystemLogStatisticsVO getLogStatistics(LocalDateTime startTime, LocalDateTime endTime,
                                                 String logType, String appKey) {
        Map<String, Object> statsMap = baseMapper.selectLogStatistics(startTime, endTime, logType, appKey);
        
        SystemLogStatisticsVO vo = new SystemLogStatisticsVO();
        if (statsMap == null) {
            return vo;
        }

        vo.setTotalCount(getLong(statsMap, "total_count"));
        vo.setAuthCount(getLong(statsMap, "auth_count"));
        vo.setOperationCount(getLong(statsMap, "operation_count"));
        vo.setSystemCount(getLong(statsMap, "system_count"));
        vo.setInfoCount(getLong(statsMap, "info_count"));
        vo.setWarnCount(getLong(statsMap, "warn_count"));
        vo.setErrorCount(getLong(statsMap, "error_count"));
        vo.setDebugCount(getLong(statsMap, "debug_count"));
        vo.setSuccessAuthCount(getLong(statsMap, "success_auth_count"));
        vo.setFailedAuthCount(getLong(statsMap, "failed_auth_count"));

        if (vo.getTotalCount() > 0) {
            vo.setErrorRate((double) vo.getErrorCount() / vo.getTotalCount() * 100);
        }
        long totalAuth = vo.getSuccessAuthCount() + vo.getFailedAuthCount();
        if (totalAuth > 0) {
            vo.setAuthSuccessRate((double) vo.getSuccessAuthCount() / totalAuth * 100);
        }

        return vo;
    }

    @Override
    public List<ErrorLogStatisticsVO> getErrorLogStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> statsList = baseMapper.selectErrorLogStatistics(startTime, endTime);
        return statsList.stream().map(this::convertToErrorLogStatisticsVO).collect(Collectors.toList());
    }

    @Override
    public LogPerformanceStatisticsVO getPerformanceStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> perfStats = baseMapper.selectPerformanceStatistics(startTime, endTime);
        
        LogPerformanceStatisticsVO vo = new LogPerformanceStatisticsVO();
        if (perfStats == null) {
            return vo;
        }

        vo.setTotalRequests(getLong(perfStats, "total_requests"));
        vo.setAvgCostTime(getDouble(perfStats, "avg_cost_time"));
        vo.setMaxCostTime(getInt(perfStats, "max_cost_time"));
        vo.setMinCostTime(getInt(perfStats, "min_cost_time"));
        vo.setP95CostTime(getInt(perfStats, "p95_cost_time"));
        vo.setP99CostTime(getInt(perfStats, "p99_cost_time"));
        vo.setAvgMemoryUsage(getDouble(perfStats, "avg_memory_usage"));
        vo.setMaxMemoryUsage(getInt(perfStats, "max_memory_usage"));
        vo.setSlowRequests(getInt(perfStats, "slow_requests"));

        if (vo.getTotalRequests() > 0) {
            vo.setSlowRequestRate((double) vo.getSlowRequests() / vo.getTotalRequests() * 100);
        }

        return vo;
    }

    @Override
    public List<ApiCallStatisticsVO> getApiCallStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<Map<String, Object>> statsList = baseMapper.selectApiCallStatistics(startTime, endTime, limit);
        return statsList.stream().map(this::convertToApiCallStatisticsVO).collect(Collectors.toList());
    }

    @Override
    public List<OperatorStatisticsVO> getOperatorStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<Map<String, Object>> statsList = baseMapper.selectOperatorStatistics(startTime, endTime, limit);
        return statsList.stream().map(this::convertToOperatorStatisticsVO).collect(Collectors.toList());
    }

    @Override
    public List<AppStatisticsVO> getAppStatistics(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<Map<String, Object>> statsList = baseMapper.selectAppStatistics(startTime, endTime, limit);
        return statsList.stream().map(this::convertToAppStatisticsVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanExpiredLogs(Integer days) {
        if (days == null || days <= 0) {
            throw new IllegalArgumentException("保留天数必须大于0");
        }

        LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
        int cleanedCount = baseMapper.cleanExpiredLogs(beforeTime);
        
        if (cleanedCount > 0) {
            log.info("逻辑删除过期日志成功，清理{}天前的日志，共{}条", days, cleanedCount);
        }
        return cleanedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteLogs(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int deletedCount = baseMapper.batchDeleteLogs(ids);
        if (deletedCount > 0) {
            log.info("批量逻辑删除日志成功，共删除{}条", deletedCount);
        }
        return deletedCount;
    }

    @Override
    public List<LogTrendVO> getLogTrendStatistics(LocalDateTime startTime, LocalDateTime endTime,
                                                 String interval, String logType, String logLevel) {
        if (startTime == null || endTime == null || !StringUtils.hasText(interval)) {
            throw new IllegalArgumentException("开始时间、结束时间和时间间隔不能为空");
        }

        List<Map<String, Object>> trendStats = baseMapper.selectLogTrendStatistics(startTime, endTime, interval, logType, logLevel);
        return trendStats.stream().map(this::convertToLogTrendVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> getErrorLogs(LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        List<UmpSystemLog> errorLogs = baseMapper.selectErrorLogs(startTime, endTime, limit);
        return errorLogs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> searchLogs(String keyword, Integer limit) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<UmpSystemLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .like(UmpSystemLog::getRequestId, keyword)
            .or().like(UmpSystemLog::getOperation, keyword)
            .or().like(UmpSystemLog::getApiPath, keyword)
            .or().like(UmpSystemLog::getResponseMessage, keyword)
            .or().like(UmpSystemLog::getErrorMessage, keyword)
            .or().like(UmpSystemLog::getIpAddress, keyword)
            .or().like(UmpSystemLog::getUserAgent, keyword)
        );
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
        
        List<UmpSystemLog> logs = list(queryWrapper);
        return logs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDetailVO> exportLogs(SystemLogQueryDTO queryDTO) {
        // 一次性查询所有符合条件的日志（注意数据量）
        LambdaQueryWrapper<UmpSystemLog> queryWrapper = buildQueryWrapper(queryDTO);
        // 不需要分页，直接获取所有记录
        List<UmpSystemLog> logs = list(queryWrapper);
        log.info("导出日志成功，共导出{}条日志", logs.size());
        return logs.stream().map(this::convertToDetailVO).collect(Collectors.toList());
    }

    // ============ 私有方法 ============

    private LambdaQueryWrapper<UmpSystemLog> buildQueryWrapper(SystemLogQueryDTO queryDTO) {
        LambdaQueryWrapper<UmpSystemLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmpSystemLog::getDelFlag, 0);

        if (StringUtils.hasText(queryDTO.getLogType())) {
            wrapper.eq(UmpSystemLog::getLogType, queryDTO.getLogType());
        }
        if (StringUtils.hasText(queryDTO.getLogLevel())) {
            wrapper.eq(UmpSystemLog::getLogLevel, queryDTO.getLogLevel());
        }
        if (StringUtils.hasText(queryDTO.getAppKey())) {
            wrapper.eq(UmpSystemLog::getAppKey, queryDTO.getAppKey());
        }
        if (StringUtils.hasText(queryDTO.getOperator())) {
            wrapper.eq(UmpSystemLog::getOperator, queryDTO.getOperator());
        }
        if (StringUtils.hasText(queryDTO.getOperation())) {
            wrapper.like(UmpSystemLog::getOperation, queryDTO.getOperation());
        }
        if (StringUtils.hasText(queryDTO.getApiPath())) {
            wrapper.like(UmpSystemLog::getApiPath, queryDTO.getApiPath());
        }
        if (StringUtils.hasText(queryDTO.getResponseCode())) {
            wrapper.eq(UmpSystemLog::getResponseCode, queryDTO.getResponseCode());
        }
        if (queryDTO.getAuthStatus() != null) {
            wrapper.eq(UmpSystemLog::getAuthStatus, queryDTO.getAuthStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(UmpSystemLog::getCreateTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(UmpSystemLog::getCreateTime, queryDTO.getEndTime());
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                .like(UmpSystemLog::getRequestId, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getOperation, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getApiPath, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getResponseMessage, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getErrorMessage, queryDTO.getKeyword())
                .or().like(UmpSystemLog::getIpAddress, queryDTO.getKeyword())
            );
        }
        return wrapper;
    }

    private void applySort(LambdaQueryWrapper<UmpSystemLog> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) queryWrapper.orderByAsc(UmpSystemLog::getCreateTime);
                else queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
                break;
            case "costTime":
                if (asc) queryWrapper.orderByAsc(UmpSystemLog::getCostTime);
                else queryWrapper.orderByDesc(UmpSystemLog::getCostTime);
                break;
            case "logLevel":
                if (asc) queryWrapper.orderByAsc(UmpSystemLog::getLogLevel);
                else queryWrapper.orderByDesc(UmpSystemLog::getLogLevel);
                break;
            case "operation":
                if (asc) queryWrapper.orderByAsc(UmpSystemLog::getOperation);
                else queryWrapper.orderByDesc(UmpSystemLog::getOperation);
                break;
            default:
                queryWrapper.orderByDesc(UmpSystemLog::getCreateTime);
                break;
        }
    }

    private SystemLogDetailVO convertToDetailVO(UmpSystemLog log) {
        if (log == null) return null;
        SystemLogDetailVO vo = new SystemLogDetailVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    private SystemLogPageVO convertToPageVO(UmpSystemLog log) {
        if (log == null) return null;
        SystemLogPageVO vo = new SystemLogPageVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    private ErrorLogStatisticsVO convertToErrorLogStatisticsVO(Map<String, Object> map) {
        ErrorLogStatisticsVO vo = new ErrorLogStatisticsVO();
        vo.setErrorType((String) map.get("error_type"));
        vo.setErrorMessage((String) map.get("error_message"));
        vo.setErrorCount(getLong(map, "error_count"));
        vo.setFirstOccurrence((String) map.get("first_occurrence"));
        vo.setLastOccurrence((String) map.get("last_occurrence"));
        vo.setAffectedApi((String) map.get("affected_api"));
        vo.setAffectedApp((String) map.get("affected_app"));
        return vo;
    }

    private ApiCallStatisticsVO convertToApiCallStatisticsVO(Map<String, Object> map) {
        ApiCallStatisticsVO vo = new ApiCallStatisticsVO();
        vo.setApiPath((String) map.get("api_path"));
        vo.setHttpMethod((String) map.get("http_method"));
        vo.setCallCount(getLong(map, "call_count"));
        vo.setSuccessCount(getLong(map, "success_count"));
        vo.setErrorCount(getLong(map, "error_count"));
        vo.setAvgCostTime(getDouble(map, "avg_cost_time"));
        vo.setMaxCostTime(getInt(map, "max_cost_time"));

        if (vo.getCallCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getCallCount() * 100);
            vo.setErrorRate((double) vo.getErrorCount() / vo.getCallCount() * 100);
        }
        return vo;
    }

    private OperatorStatisticsVO convertToOperatorStatisticsVO(Map<String, Object> map) {
        OperatorStatisticsVO vo = new OperatorStatisticsVO();
        vo.setOperator((String) map.get("operator"));
        vo.setOperationCount(getLong(map, "operation_count"));
        vo.setSuccessCount(getLong(map, "success_count"));
        vo.setErrorCount(getLong(map, "error_count"));
        vo.setLastOperationTime((String) map.get("last_operation_time"));
        vo.setFrequentOperation((String) map.get("frequent_operation"));
        if (vo.getOperationCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getOperationCount() * 100);
        }
        return vo;
    }

    private AppStatisticsVO convertToAppStatisticsVO(Map<String, Object> map) {
        AppStatisticsVO vo = new AppStatisticsVO();
        vo.setAppKey((String) map.get("app_key"));
        vo.setRequestCount(getLong(map, "request_count"));
        vo.setSuccessCount(getLong(map, "success_count"));
        vo.setErrorCount(getLong(map, "error_count"));
        vo.setAvgCostTime(getDouble(map, "avg_cost_time"));
        vo.setLastRequestTime((String) map.get("last_request_time"));
        vo.setFrequentApi((String) map.get("frequent_api"));
        if (vo.getRequestCount() > 0) {
            vo.setSuccessRate((double) vo.getSuccessCount() / vo.getRequestCount() * 100);
        }
        return vo;
    }

    private LogTrendVO convertToLogTrendVO(Map<String, Object> map) {
        LogTrendVO vo = new LogTrendVO();
        vo.setTimePeriod((String) map.get("time_period"));
        vo.setTotalCount(getLong(map, "total_count"));
        vo.setAuthCount(getLong(map, "auth_count"));
        vo.setOperationCount(getLong(map, "operation_count"));
        vo.setSystemCount(getLong(map, "system_count"));
        vo.setInfoCount(getLong(map, "info_count"));
        vo.setWarnCount(getLong(map, "warn_count"));
        vo.setErrorCount(getLong(map, "error_count"));
        if (vo.getTotalCount() > 0) {
            vo.setErrorRate((double) vo.getErrorCount() / vo.getTotalCount() * 100);
        }
        return vo;
    }

    // 辅助方法：安全地从Map中获取Long值
    private Long getLong(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? 0L : ((Number) val).longValue();
    }

    private Integer getInt(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? 0 : ((Number) val).intValue();
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? 0.0 : ((Number) val).doubleValue();
    }
}