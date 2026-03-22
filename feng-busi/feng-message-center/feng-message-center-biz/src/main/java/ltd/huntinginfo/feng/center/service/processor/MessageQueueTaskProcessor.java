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
package ltd.huntinginfo.feng.center.service.processor;

import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.mapper.UmpBroadcastReceiveRecordMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgQueueMapper;
import ltd.huntinginfo.feng.center.mapper.UmpTopicSubscriptionMapper;
import ltd.huntinginfo.feng.center.service.*;
import ltd.huntinginfo.feng.center.service.state.MessageStateMachine;
import ltd.huntinginfo.feng.center.utils.ContentUtil;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageProcessStatus;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.bean.BeanUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JOB任务处理器（处理重试、过期、统计）
 * <p>
 * 职责：
 * 1. 重试任务：处理状态为 RETRY 且执行时间已到的队列任务。
 * 2. 过期消息：将已过期的消息置为 EXPIRED 状态。
 * 3. 统计任务：每日统计前一天的发送数据。
 * <p>
 * 注意：初次任务（PENDING）由 MessageDistributionProcessor 处理，本处理器不参与。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageQueueTaskProcessor {

    private final UmpMsgQueueMapper umpMsgQueueMapper;
    private final UmpMsgBroadcastMapper umpMsgBroadcastMapper;
    private final UmpMsgInboxMapper umpMsgInboxMapper;
    private final UmpTopicSubscriptionMapper umpTopicSubscriptionMapper;
    private final UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper;
    private final UmpMsgQueueService umpMsgQueueService;
    private final UmpMsgMainService umpMsgMainService;
    private final MessageDistributionProcessor messageDistributionProcessor;
    private final MessagePushProcessor messagePushProcessor;
    private final MessageStateMachine messageStateMachine;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;
    private final RemoteUniqueUserService remoteUniqueUserService;
    private final RemoteUniqueDeptService remoteUniqueDeptService;
    private final UmpTopicSubscriptionService umpTopicSubscriptionService;

    // ==================== 重试任务处理 ====================

    /**
     * 处理分发类型的重试任务（状态为 RETRY，执行时间已到，且未超过最大重试次数）
     */
    @Transactional(rollbackFor = Exception.class)
    public void processDistributeRetryTasks(int limit) {
        log.debug("开始处理分发重试任务，限制数量: {}", limit);

        List<MsgQueueDetailVO> tasks = umpMsgQueueService.getRetryPendingTasksByType(
                MqMessageEventConstants.QueueTaskTypes.DISTRIBUTE, limit);

        if (CollectionUtils.isEmpty(tasks)) {
            log.debug("没有待处理的分发重试任务");
            return;
        }

        log.info("发现 {} 个待处理的分发重试任务", tasks.size());

        for (MsgQueueDetailVO task : tasks) {
            String taskId = task.getId();
            String workerId = generateWorkerId("distribute-retry");

            try {
                // 乐观锁标记为重试中
                boolean locked = umpMsgQueueService.markAsRetrying(taskId, workerId);
                if (!locked) {
                    log.debug("任务已被其他 worker 处理，跳过 taskId: {}", taskId);
                    continue;
                }

                MqMessage<TaskData> message = buildMqMessage(task);
                messageDistributionProcessor.handleMessageDistributing(message);

                // 若处理器未更新状态（仍为 RETRYING），则手动标记成功
                MsgQueueDetailVO updatedTask = umpMsgQueueService.getQueueTaskDetail(taskId);
                if (updatedTask != null && MessageProcessStatus.RETRYING.getCode().equals(updatedTask.getStatus())) {
                    umpMsgQueueService.markAsRetrySuccess(taskId, workerId, "重试处理完成（状态补充）");
                }

            } catch (Exception e) {
                log.error("处理分发重试任务失败，任务ID: {}", taskId, e);

                umpMsgQueueService.failAndScheduleRetry(taskId, workerId,
                        e.getMessage(), e.toString(), MqMessageEventConstants.RetryDefaults.MAX_INTERVAL);
            }
        }
    }

    /**
     * 处理推送类型的重试任务（状态为 RETRY，执行时间已到，且未超过最大重试次数）
     */
    public void processPushRetryTasks(int limit) {
        log.debug("开始处理推送重试任务，限制数量: {}", limit);

        List<MsgQueueDetailVO> tasks = umpMsgQueueService.getRetryPendingTasksByType(
                MqMessageEventConstants.QueueTaskTypes.PUSH, limit);

        if (CollectionUtils.isEmpty(tasks)) {
            log.debug("没有待处理的推送重试任务");
            return;
        }

        log.info("发现 {} 个待处理的推送重试任务", tasks.size());

        for (MsgQueueDetailVO task : tasks) {
            try {
                processSinglePushRetryTask(task);
            } catch (Exception e) {
                log.error("处理单个推送重试任务失败，任务ID: {}", task.getId(), e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processSinglePushRetryTask(MsgQueueDetailVO task) {
        String taskId = task.getId();
        String workerId = generateWorkerId("push-retry");

        boolean locked = umpMsgQueueService.markAsRetrying(taskId, workerId);
        if (!locked) {
            log.debug("任务已被其他 worker 处理，跳过 taskId: {}", taskId);
            return;
        }

        MqMessage<TaskData> message = buildMqMessage(task);
        messagePushProcessor.pushMessageToReceiver(message);

        MsgQueueDetailVO updatedTask = umpMsgQueueService.getQueueTaskDetail(taskId);
        if (updatedTask != null && MessageProcessStatus.RETRYING.getCode().equals(updatedTask.getStatus())) {
            umpMsgQueueService.markAsRetrySuccess(taskId, workerId, "重试处理完成（状态补充）");
        }
    }

    // ==================== 过期消息处理 ====================

    /**
     * 处理过期消息：将已过期的消息状态置为 EXPIRED
     */
    @Transactional(rollbackFor = Exception.class)
    public int processExpiredMessages() {
        LocalDateTime expiredTime = LocalDateTime.now();
        List<UmpMsgMain> expiredMessages = umpMsgMainService.getExpiredMessages(expiredTime);

        if (CollectionUtils.isEmpty(expiredMessages)) {
            return 0;
        }

        for (UmpMsgMain msg : expiredMessages) {
            try {
                List<MsgQueueDetailVO> list = umpMsgQueueService.getQueueTasksByMsgId(msg.getId());
                for (MsgQueueDetailVO vo : list) {
                	umpMsgQueueService.markAsFailed(vo.getId(), "expired-cleaner", "已过期", null);
                	MqMessage<TaskData> message = ContentUtil.buildMessage(null, null, null, msg, 
                			msg.getId(), vo.getId(), umpMsgInboxMapper, umpMsgBroadcastMapper, umpMsgQueueMapper,
                    		umpBroadcastReceiveRecordMapper, umpTopicSubscriptionMapper, 
                			remoteUniqueUserService, remoteUniqueDeptService);

                    messageStateMachine.onExpired(message);
                }
            } catch (Exception e) {
                log.error("处理过期消息失败，消息ID: {}", msg.getId(), e);
            }
        }

        log.info("已处理 {} 条过期消息", expiredMessages.size());
        return expiredMessages.size();
    }

    // ==================== 统计任务处理 ====================

    /**
     * 统计前一天的发送数据，写入统计表
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean processStatisticsMessages() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        try {
            MessageStatisticsVO msgStat = umpMsgMainService.getMessageStatisticsGroupByAppAndType(targetDate);
            log.info("统计任务完成: {}", msgStat);
            return true;
        } catch (Exception e) {
            log.error("统计任务失败，日期: {}", targetDate, e);
        }
        return false;
    }

    // ==================== 私有方法 ====================

    private String generateWorkerId(String prefix) {
        return prefix + "-" + Thread.currentThread().getId() + "-" + System.currentTimeMillis();
    }

    private MqMessage<TaskData> buildMqMessage(MsgQueueDetailVO task) {
        TaskData payload = task.getTaskData();
        if (payload == null) {
            payload = new TaskData();
        }
        if (BeanUtil.isEmpty(payload)) {
            UmpMsgMain message = umpMsgMainService.getById(task.getMsgId());
            TaskData eventData = ContentUtil.buildTaskData(null, null, null, message, null, task.getId(),
                    umpMsgInboxMapper, umpMsgBroadcastMapper,
                    umpBroadcastReceiveRecordMapper, umpTopicSubscriptionMapper,
                    remoteUniqueUserService, remoteUniqueDeptService);
            BeanUtil.copyProperties(eventData, payload);
        } else {
            // 确保 payload 中的状态与任务类型匹配
            if (MqMessageEventConstants.QueueTaskTypes.DISTRIBUTE.equals(task.getQueueType())) {
                payload.setStatus(MqMessageEventConstants.EventTypes.DISTRIBUTING);
            } else if (MqMessageEventConstants.QueueTaskTypes.PUSH.equals(task.getQueueType())) {
                payload.setStatus(MqMessageEventConstants.EventTypes.DISTRIBUTED);
            } else {
                payload.setStatus(MqMessageEventConstants.EventTypes.PUSH_FAILED);
            }
            payload.setTaskId(task.getId());
        }

        MqMessage<TaskData> message = MqMessage.create(
                payload.getStatus(),
                payload.getReceiverType(),
                payload);
        message.setMessageId(task.getMsgId());
        message.setBusinessType(payload.getReceiverType());
        message.setEventType(payload.getStatus());
        message.setRetryCount(task.getCurrentRetry());

        return message;
    }
}