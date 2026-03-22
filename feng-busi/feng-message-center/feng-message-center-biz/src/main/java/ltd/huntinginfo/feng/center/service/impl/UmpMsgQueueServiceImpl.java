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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.json.MessageReceiver;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.mapper.UmpBroadcastReceiveRecordMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgMainMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgQueueMapper;
import ltd.huntinginfo.feng.center.mapper.UmpTopicSubscriptionMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgQueueService;
import ltd.huntinginfo.feng.center.service.state.MessageStateMachine;
import ltd.huntinginfo.feng.center.utils.ContentUtil;
import ltd.huntinginfo.feng.center.utils.ReceiverUtil;
import ltd.huntinginfo.feng.center.utils.ReceiverUtil.ReceivingInfoResult;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageProcessStatus;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.dto.MsgQueueQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueuePageVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueStatisticsVO;
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
 * 消息队列表服务实现类
 * 主要业务逻辑参见数据库表脚本和UmpMsgQueueService的注释
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgQueueServiceImpl extends ServiceImpl<UmpMsgQueueMapper, UmpMsgQueue> implements UmpMsgQueueService {

    private final UmpMsgBroadcastMapper umpMsgBroadcastMapper;
    private final UmpMsgInboxMapper umpMsgInboxMapper;
    private final UmpTopicSubscriptionMapper umpTopicSubscriptionMapper;
    private final UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper;
    private final MessageStateMachine messageStateMachine;
    private final UmpMsgMainMapper umpMsgMainMapper;
    private final RemoteUniqueUserService remoteUniqueUserService;
    private final RemoteUniqueDeptService remoteUniqueDeptService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createQueueTask(String queueType, String queueName, String msgId,
                                 TaskData taskData, Integer priority,
                                 LocalDateTime executeTime, Integer maxRetry) {
        if (!StringUtils.hasText(queueType) || !StringUtils.hasText(queueName) || 
            !StringUtils.hasText(msgId) || taskData == null) {
            throw new IllegalArgumentException("队列类型、队列名称、消息ID和任务数据不能为空");
        }

        // 创建任务
        UmpMsgQueue queueTask = new UmpMsgQueue();
        queueTask.setQueueType(queueType);
        queueTask.setQueueName(queueName);
        queueTask.setMsgId(msgId);
        queueTask.setTaskData(taskData);
        queueTask.setPriority(priority != null ? priority : 5);
        queueTask.setExecuteTime(executeTime != null ? executeTime : LocalDateTime.now());
        queueTask.setMaxRetry(maxRetry != null ? maxRetry : 3);
        queueTask.setCurrentRetry(0);
        queueTask.setStatus(MessageProcessStatus.PENDING.getCode());

        if (save(queueTask)) {
            return queueTask.getId();
        } else {
            log.error("队列任务创建失败，队列类型: {}, 消息ID: {}", queueType, msgId);
            throw new RuntimeException("队列任务创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateQueueTasks(List<UmpMsgQueue> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return 0;
        }

        List<UmpMsgQueue> queueTasks = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (UmpMsgQueue task : tasks) {
            if (!StringUtils.hasText(task.getQueueType()) || !StringUtils.hasText(task.getQueueName()) || 
                !StringUtils.hasText(task.getMsgId()) || BeanUtil.isEmpty(task.getTaskData())) {
                continue;
            }
            task.setPriority(task.getPriority() != null ? task.getPriority() : 5);
            task.setExecuteTime(task.getExecuteTime() != null ? task.getExecuteTime() : now);
            task.setMaxRetry(task.getMaxRetry() != null ? task.getMaxRetry() : 3);
            task.setCurrentRetry(0);
            task.setStatus(MessageProcessStatus.PENDING.getCode());
            queueTasks.add(task);
        }

        if (!CollectionUtils.isEmpty(queueTasks)) {
            boolean success = saveBatch(queueTasks);
            if (success) {
                log.info("批量创建队列任务成功，数量: {}", queueTasks.size());
                return queueTasks.size();
            }
        }
        
        return 0;
    }

    @Override
    public List<MsgQueueDetailVO> getQueueTasksByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        List<UmpMsgQueue> tasks = baseMapper.selectByMsgId(msgId);
        return tasks.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MsgQueuePageVO> queryQueuePage(MsgQueueQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgQueue> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getQueueType())) {
            queryWrapper.eq(UmpMsgQueue::getQueueType, queryDTO.getQueueType());
        }
        
        if (StringUtils.hasText(queryDTO.getQueueName())) {
            queryWrapper.eq(UmpMsgQueue::getQueueName, queryDTO.getQueueName());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgQueue::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgQueue::getMsgId, queryDTO.getMsgId());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgQueue::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgQueue::getCreateTime, queryDTO.getEndTime());
        }
        
        if (queryDTO.getPriorityMin() != null) {
            queryWrapper.ge(UmpMsgQueue::getPriority, queryDTO.getPriorityMin());
        }
        
        if (queryDTO.getPriorityMax() != null) {
            queryWrapper.le(UmpMsgQueue::getPriority, queryDTO.getPriorityMax());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByAsc(UmpMsgQueue::getPriority)
                       .orderByAsc(UmpMsgQueue::getExecuteTime);
        }

        // 执行分页查询
        Page<UmpMsgQueue> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgQueue> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<MsgQueuePageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<MsgQueuePageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<MsgQueueDetailVO> getPendingTasks(String queueType, String queueName, int limit) {
        List<UmpMsgQueue> tasks = baseMapper.selectPendingTasks(queueType, queueName, limit);
        return tasks.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public MsgQueueDetailVO getQueueTaskDetail(String taskId) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return null;
        }
        return convertToDetailVO(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStatus(String taskId, String status, String workerId,
                                   String resultCode, String resultMessage, String errorStack) {
    	log.debug("updateTaskStatus taskId: {}, status: {}, workerId: {}, resultCode: {}, resultMessage: {}, errorStack: {}", 
    			taskId, status, workerId, resultCode, resultMessage, errorStack);
    	
        if (!StringUtils.hasText(taskId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("任务ID和状态不能为空");
        }

        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return false;
        }

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        
        if (MessageProcessStatus.PROCESSING.getCode().equals(status)) {
            startTime = LocalDateTime.now();
        } else if (MessageProcessStatus.SUCCESS.getCode().equals(status) 
                || MessageProcessStatus.FAILED.getCode().equals(status)
                || MessageProcessStatus.RETRY_SUCCESS.getCode().equals(status)
                || MessageProcessStatus.RETRY_FAILED.getCode().equals(status)) {
            endTime = LocalDateTime.now();
        }

        int updated = baseMapper.updateTaskStatus(
                taskId, status, workerId, startTime, endTime, 
                resultCode, resultMessage, errorStack);
        
        boolean success = updated > 0;
        if (success) {
            log.info("队列任务状态更新成功，任务ID: {}, 状态: {}", taskId, status);
            // 异步触发状态更新事件
//            triggerTaskStatusUpdateEvent(taskId, status);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsProcessing(String taskId, String workerId) {
        return updateTaskStatus(taskId, MessageProcessStatus.PROCESSING.getCode(), workerId, null, null, null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRetrying(String taskId, String workerId) {
        return updateTaskStatus(taskId, MessageProcessStatus.RETRYING.getCode(), workerId, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRetrySuccess(String taskId, String workerId, String resultMessage) {
        return updateTaskStatus(taskId, MessageProcessStatus.RETRY_SUCCESS.getCode(), workerId, MessageProcessStatus.RETRY_SUCCESS.getCode(), resultMessage, null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsSuccess(String taskId, String workerId, String resultMessage) {
        return updateTaskStatus(taskId, MessageProcessStatus.SUCCESS.getCode(), workerId, MessageProcessStatus.SUCCESS.getCode(), resultMessage, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsFailed(String taskId, String workerId, String errorMessage, String errorStack) {
        return updateTaskStatus(taskId, MessageProcessStatus.FAILED.getCode(), workerId, MessageProcessStatus.FAILED.getCode(), errorMessage, errorStack);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRetryFailed(String taskId, String workerId, String errorMessage, String errorStack) {
        return updateTaskStatus(taskId, MessageProcessStatus.RETRY_FAILED.getCode(), workerId, MessageProcessStatus.RETRY_FAILED.getCode(), errorMessage, errorStack);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean retryFailedTask(String taskId, int retryDelayMinutes) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return false;
        }

        if (!MessageProcessStatus.FAILED.getCode().equals(task.getStatus())) {
            log.warn("任务不是失败状态，无法重试，任务ID: {}, 状态: {}", taskId, task.getStatus());
            return false;
        }

        if (task.getCurrentRetry() >= task.getMaxRetry()) {
            log.warn("任务已达到最大重试次数，任务ID: {}, 当前重试: {}, 最大重试: {}", 
                    taskId, task.getCurrentRetry(), task.getMaxRetry());
            return false;
        }

        int newRetryCount = task.getCurrentRetry() + 1;
        LocalDateTime newExecuteTime = LocalDateTime.now().plusMinutes(retryDelayMinutes);
        
        int updated = baseMapper.updateRetryCount(taskId, newRetryCount, newExecuteTime);
        
        if (updated > 0) {
            // 更新状态为PENDING，以便重新执行
            updateTaskStatus(taskId, MessageProcessStatus.PENDING.getCode(), null, null, null, null);
            log.info("任务重试成功，任务ID: {}, 重试次数: {}, 下次执行时间: {}", 
                    taskId, newRetryCount, newExecuteTime);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchRetryFailedTasks(List<String> taskIds, int retryDelayMinutes) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return 0;
        }

        int retriedCount = 0;
        for (String taskId : taskIds) {
            try {
                boolean success = retryFailedTask(taskId, retryDelayMinutes);
                if (success) {
                    retriedCount++;
                }
            } catch (Exception e) {
                log.error("重试任务失败，任务ID: {}", taskId, e);
            }
        }
        
        if (retriedCount > 0) {
            log.info("批量重试失败任务成功，数量: {}", retriedCount);
        }
        
        return retriedCount;
    }

    @Override
    public MsgQueueStatisticsVO getQueueStatistics(LocalDateTime startTime,
                                                  LocalDateTime endTime,
                                                  String queueType) {
        Map<String, Object> statsMap = baseMapper.selectQueueStatistics(
                startTime, endTime, queueType);
        
        MsgQueueStatisticsVO statisticsVO = new MsgQueueStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setQueueType(queueType);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setPendingCount(((Number) statsMap.getOrDefault("pending_count", 0)).longValue());
            statisticsVO.setProcessingCount(((Number) statsMap.getOrDefault("processing_count", 0)).longValue());
            statisticsVO.setSuccessCount(((Number) statsMap.getOrDefault("success_count", 0)).longValue());
            statisticsVO.setFailedCount(((Number) statsMap.getOrDefault("failed_count", 0)).longValue());
            statisticsVO.setAvgPriority(((Number) statsMap.getOrDefault("avg_priority", 0)).doubleValue());
            statisticsVO.setAvgRetryCount(((Number) statsMap.getOrDefault("avg_retry_count", 0)).doubleValue());
            
            // 计算成功率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setSuccessRate((double) statisticsVO.getSuccessCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQueueTask(String taskId) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("队列任务不存在，任务ID: {}", taskId);
            return false;
        }

        boolean success = removeById(taskId);
        if (success) {
            log.info("队列任务删除成功，任务ID: {}", taskId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        LambdaQueryWrapper<UmpMsgQueue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgQueue::getMsgId, msgId);
        
        long count = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据消息ID删除队列任务成功，消息ID: {}, 数量: {}", msgId, count);
            return count;
        }
        
        return 0L;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgQueue> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getCreateTime);
                }
                break;
            case "executeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getExecuteTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getExecuteTime);
                }
                break;
            case "priority":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getPriority);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getPriority);
                }
                break;
            case "startTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getStartTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getStartTime);
                }
                break;
            case "endTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgQueue::getEndTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgQueue::getEndTime);
                }
                break;
            default:
                queryWrapper.orderByAsc(UmpMsgQueue::getPriority)
                           .orderByAsc(UmpMsgQueue::getExecuteTime);
                break;
        }
    }

    private MsgQueueDetailVO convertToDetailVO(UmpMsgQueue task) {
        MsgQueueDetailVO vo = new MsgQueueDetailVO();
        BeanUtils.copyProperties(task, vo);
        
        // 计算耗时
        if (task.getStartTime() != null && task.getEndTime() != null) {
            long costSeconds = java.time.Duration.between(task.getStartTime(), task.getEndTime()).getSeconds();
            vo.setCostSeconds(costSeconds);
        }
        
        return vo;
    }

    private MsgQueuePageVO convertToPageVO(UmpMsgQueue task) {
        MsgQueuePageVO vo = new MsgQueuePageVO();
        BeanUtils.copyProperties(task, vo);
        return vo;
    }
    
    @Override
    public List<MsgQueueDetailVO> getPendingTasksByType(String queueType, int limit) {
        LambdaQueryWrapper<UmpMsgQueue> wrapper = new LambdaQueryWrapper<UmpMsgQueue>()
                .eq(UmpMsgQueue::getQueueType, queueType)
                .eq(UmpMsgQueue::getStatus, MessageProcessStatus.PENDING.getCode())
                .le(UmpMsgQueue::getExecuteTime, LocalDateTime.now())
                .orderByAsc(UmpMsgQueue::getPriority)
                .orderByAsc(UmpMsgQueue::getExecuteTime)
                .last("LIMIT " + limit);
        return baseMapper.selectList(wrapper).stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MsgQueueDetailVO> getRetryPendingTasksByType(String queueType, int limit) {
        LambdaQueryWrapper<UmpMsgQueue> wrapper = new LambdaQueryWrapper<UmpMsgQueue>()
                .eq(UmpMsgQueue::getQueueType, queueType)
                .eq(UmpMsgQueue::getStatus, MessageProcessStatus.RETRY.getCode())
                .le(UmpMsgQueue::getExecuteTime, LocalDateTime.now())
                .apply("current_retry < max_retry")          // 仅重试任务
                .orderByAsc(UmpMsgQueue::getPriority)
                .orderByAsc(UmpMsgQueue::getExecuteTime)
                .last("LIMIT " + limit);
        return baseMapper.selectList(wrapper).stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MsgQueueDetailVO> getRetryableTasks(int limit) {
        LambdaQueryWrapper<UmpMsgQueue> wrapper = new LambdaQueryWrapper<UmpMsgQueue>()
                .eq(UmpMsgQueue::getStatus, MessageProcessStatus.RETRY.getCode())
                .le(UmpMsgQueue::getExecuteTime, LocalDateTime.now())
                // 当前重试次数 < 最大重试次数
                .apply("current_retry < max_retry")
                .orderByAsc(UmpMsgQueue::getExecuteTime)
                .last("LIMIT " + limit);
        return baseMapper.selectList(wrapper).stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetToPending(String taskId) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("任务不存在，taskId: {}", taskId);
            return false;
        }
        // 重置为待处理状态，清除执行现场
        task.setStatus(MessageProcessStatus.PENDING.getCode());
        task.setWorkerId(null);
        task.setStartTime(null);
        task.setEndTime(null);
        task.setResultMessage(null);
        task.setErrorStack(null);
        return updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean failAndScheduleRetry(String taskId, String workerId, String errorMessage,
                                        String errorStack, Long delaySeconds) {
        UmpMsgQueue task = getById(taskId);
        if (task == null) {
            log.warn("任务不存在，taskId: {}", taskId);
            return false;
        }

        // 已达最大重试次数 -> 永久失败
        if (task.getCurrentRetry() >= task.getMaxRetry()) {
        	UmpMsgMain umpMsgMain = umpMsgMainMapper.selectById(task.getMsgId());
        	
            MessageReceivingUnit unit = null;
            MessageRecipient recipient = null;
            MessageReceiver receiver = null;
            String sendTargetType = umpMsgMain.getSendTargetType();
            String receivingUnitCode = null;
            String receiverIdNumber = null; 
			Map<String, Object> rawReceivingScope = null;
			ReceivingInfoResult receivingInfoResult = ReceiverUtil.updateReceivingInfo(unit, recipient, receiver,
					sendTargetType, receivingUnitCode, receiverIdNumber, 
					rawReceivingScope, umpMsgMain, 	
					umpMsgBroadcastMapper, umpMsgInboxMapper,
					umpTopicSubscriptionMapper, remoteUniqueUserService,
					remoteUniqueDeptService);
            unit = receivingInfoResult.getUnit();
            recipient = receivingInfoResult.getRecipient();
            receiver = receivingInfoResult.getReceiver();
            Integer totalReceivers = receivingInfoResult.getTotalReceivers();
        	
        	MqMessage<TaskData> message = ContentUtil.buildMessage(recipient, unit, receiver, 
        			umpMsgMain, task.getMsgId(), task.getId(), 
        			umpMsgInboxMapper, umpMsgBroadcastMapper, baseMapper,
            		umpBroadcastReceiveRecordMapper, umpTopicSubscriptionMapper, 
        			remoteUniqueUserService, remoteUniqueDeptService);
        	
        	if (task.getQueueType().equals(MqMessageEventConstants.QueueTaskTypes.DISTRIBUTE)) {
        		messageStateMachine.onDistributeFailed(message);       
        	} else if (task.getQueueType().equals(MqMessageEventConstants.QueueTaskTypes.PUSH)){
        		messageStateMachine.onPushFailed(message);
        	} else {
        		messageStateMachine.onPullFailed(message);
        	}
            return updateTaskStatus(taskId, MessageProcessStatus.FAILED.getCode(), workerId, MessageProcessStatus.FAILED.getCode(), errorMessage, errorStack);
        }

        // 未达最大重试次数：增加重试次数，设置下次执行时间，状态改为RETRY
        task.setCurrentRetry(task.getCurrentRetry() + 1);
        task.setExecuteTime(LocalDateTime.now().plusSeconds(delaySeconds));
        task.setStatus(MessageProcessStatus.RETRY.getCode());  // 状态仍为 RETRY待重试
        task.setWorkerId(null);    // 释放工作者
        task.setResultMessage(errorMessage);
        task.setErrorStack(errorStack);
        return updateById(task);
    }
    
}