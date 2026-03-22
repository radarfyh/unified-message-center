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

import cn.hutool.core.bean.BeanUtil;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgCallback;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigRequest;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigResponse;
import ltd.huntinginfo.feng.center.mapper.UmpMsgCallbackMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgCallbackService;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageProcessStatus;
import ltd.huntinginfo.feng.center.api.dto.CallbackQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.CallbackDetailVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackPageVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackStatisticsVO;
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
 * 回调记录表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgCallbackServiceImpl extends ServiceImpl<UmpMsgCallbackMapper, UmpMsgCallback> implements UmpMsgCallbackService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createCallback(String msgId, String callbackUrl, 
    		Map<String, Object> callbackData, String signature) {
        if (!StringUtils.hasText(msgId) || 
            !StringUtils.hasText(callbackUrl) || callbackData == null) {
            throw new IllegalArgumentException("消息ID、回调地址和回调数据不能为空");
        }

        // 创建回调记录
        UmpMsgCallback callback = new UmpMsgCallback();
        callback.setMsgId(msgId);
        callback.setCallbackUrl(callbackUrl);
        callback.setCallbackData(callbackData);
        callback.setSignature(signature);
        callback.setStatus(MessageProcessStatus.PENDING.getCode());
        callback.setRetryCount(0);

        if (save(callback)) {
            return callback.getId();
        } else {
            log.error("回调记录创建失败，消息ID: {}", msgId);
            throw new RuntimeException("回调记录创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateCallbacks(List<UmpMsgCallback> callbacks) {
        if (CollectionUtils.isEmpty(callbacks)) {
            return 0;
        }

        // 为每个回调对象设置默认值
        callbacks.forEach(callback -> {
            // 状态默认 PENDING
            if (callback.getStatus() == null) {
                callback.setStatus(MessageProcessStatus.PENDING.getCode());
            }
            // 重试次数默认为 0
            if (callback.getRetryCount() == null) {
                callback.setRetryCount(0);
            }
            // 注意：createTime 由自动填充处理器处理，无需手动设置
        });

        boolean success = saveBatch(callbacks);
        if (success) {
            log.info("批量创建回调记录成功，数量: {}", callbacks.size());
            return callbacks.size();
        }
        return 0;
    }

    @Override
    public List<CallbackDetailVO> getCallbacksByMsgAndReceiver(String msgId, String receiverId) {
        if (!StringUtils.hasText(msgId) || !StringUtils.hasText(receiverId)) {
            throw new IllegalArgumentException("消息ID和接收者ID不能为空");
        }

        List<UmpMsgCallback> callbacks = baseMapper.selectByMsgAndReceiver(msgId, receiverId);
        return callbacks.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CallbackPageVO> queryCallbackPage(CallbackQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgCallback> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgCallback::getMsgId, queryDTO.getMsgId());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgCallback::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getCallbackUrl())) {
            queryWrapper.like(UmpMsgCallback::getCallbackUrl, queryDTO.getCallbackUrl());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgCallback::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgCallback::getCreateTime, queryDTO.getEndTime());
        }
        
        if (queryDTO.getMinRetryCount() != null) {
            queryWrapper.ge(UmpMsgCallback::getRetryCount, queryDTO.getMinRetryCount());
        }
        
        if (queryDTO.getMaxRetryCount() != null) {
            queryWrapper.le(UmpMsgCallback::getRetryCount, queryDTO.getMaxRetryCount());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgCallback::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgCallback> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgCallback> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<CallbackPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<CallbackPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public CallbackDetailVO getCallbackDetail(String callbackId) {
        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return null;
        }
        return convertToDetailVO(callback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCallbackStatus(String callbackId, String status, 
    		Map<String, Object> responseBody, String errorMessage) {
        if (!StringUtils.hasText(callbackId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("回调记录ID和状态不能为空");
        }

        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return false;
        }

        LocalDateTime sendTime = null;
        LocalDateTime responseTime = null;
        Integer costTime = null;
        
        if (MessageProcessStatus.PROCESSING.getCode().equals(status)) {
            sendTime = LocalDateTime.now();
        } else if (MessageProcessStatus.SUCCESS.getCode().equals(status) || MessageProcessStatus.FAILED.getCode().equals(status)) {
            responseTime = LocalDateTime.now();
            if (callback.getSendTime() != null) {
                costTime = (int) java.time.Duration.between(callback.getSendTime(), responseTime).toMillis();
            }
        }
        
        // 报错：Data truncation: Cannot create a JSON value from a string with CHARACTER SET 'binary'
        // 要在mapper中指定typeHandler
        // response_body = #{responseBody, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}
        int updated = baseMapper.updateCallbackStatus(
                callbackId, status, responseBody, errorMessage,
                sendTime, responseTime, costTime);
        
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsProcessing(String callbackId) {
        return updateCallbackStatus(callbackId, MessageProcessStatus.PROCESSING.getCode(), null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsSuccess(String callbackId, Map<String, Object> responseBody) {
        return updateCallbackStatus(callbackId, MessageProcessStatus.SUCCESS.getCode(), responseBody, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsFailed(String callbackId, String errorMessage) {
        return updateCallbackStatus(callbackId, MessageProcessStatus.FAILED.getCode(), null, errorMessage);
    }

    @Override
    public CallbackStatisticsVO getCallbackStatistics(LocalDateTime startTime,
                                                    LocalDateTime endTime,
                                                    String msgId) {
        Map<String, Object> statsMap = baseMapper.selectCallbackStatistics(
                startTime, endTime, msgId);
        
        CallbackStatisticsVO statisticsVO = new CallbackStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setMsgId(msgId);
        
        if (statsMap != null) {
        	Long totalCount = statsMap.get("total_count") == null ? 0L : ((Number) statsMap.get("total_count")).longValue();
            statisticsVO.setTotalCount(totalCount);
            
            Long pendingCount = statsMap.get("pending_count") == null ? 0L : ((Number) statsMap.get("pending_count")).longValue();
            statisticsVO.setPendingCount(pendingCount);
            
            Long processingCount = statsMap.get("processing_count") == null ? 0L : ((Number) statsMap.get("processing_count")).longValue();
            statisticsVO.setProcessingCount(processingCount);
            
            statisticsVO.setSuccessCount(statsMap.get("success_count") == null ? 0L : ((Number) statsMap.get("success_count")).longValue());
            statisticsVO.setFailedCount(statsMap.get("failed_count") == null ? 0L : ((Number) statsMap.get("failed_count")).longValue());
            statisticsVO.setAvgCostTime(statsMap.get("avg_cost_time") == null ? 0L : ((Number) statsMap.get("avg_cost_time")).doubleValue());
            statisticsVO.setAvgRetryCount(statsMap.get("avg_retry_count") == null ? 0L : ((Number) statsMap.get("avg_retry_count")).doubleValue());
            
            // 计算成功率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setSuccessRate((double) statisticsVO.getSuccessCount() / statisticsVO.getTotalCount() * 100);
            }
            
            // 计算平均响应时间
            if (statisticsVO.getAvgCostTime() > 0) {
                statisticsVO.setAvgResponseTime(statisticsVO.getAvgCostTime());
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCallback(String callbackId) {
        UmpMsgCallback callback = getById(callbackId);
        if (callback == null) {
            log.warn("回调记录不存在，ID: {}", callbackId);
            return false;
        }

        boolean success = removeById(callbackId);
        if (success) {
            log.info("回调记录删除成功，ID: {}", callbackId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        LambdaQueryWrapper<UmpMsgCallback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgCallback::getMsgId, msgId);
        
        long count = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据消息ID删除回调记录成功，消息ID: {}, 数量: {}", msgId, count);
            return count;
        }
        
        return 0L;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgCallback> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getCreateTime);
                }
                break;
            case "sendTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getSendTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getSendTime);
                }
                break;
            case "responseTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getResponseTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getResponseTime);
                }
                break;
            case "retryCount":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getRetryCount);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getRetryCount);
                }
                break;
            case "nextRetryTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgCallback::getNextRetryTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgCallback::getNextRetryTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgCallback::getCreateTime);
                break;
        }
    }

    private CallbackDetailVO convertToDetailVO(UmpMsgCallback callback) {
        CallbackDetailVO vo = new CallbackDetailVO();
        BeanUtils.copyProperties(callback, vo);
        return vo;
    }

    private CallbackPageVO convertToPageVO(UmpMsgCallback callback) {
        CallbackPageVO vo = new CallbackPageVO();
        BeanUtils.copyProperties(callback, vo);
        return vo;
    }
}