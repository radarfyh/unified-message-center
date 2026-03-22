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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.api.json.MessageReceiver;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.entity.*;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.service.*;
import ltd.huntinginfo.feng.center.service.state.MessageStateMachine;
import ltd.huntinginfo.feng.center.utils.ContentUtil;
import ltd.huntinginfo.feng.center.utils.ReceiverUtil;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageReadStatus;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageDistributionProcessor {

    private static final String WORKER_PREFIX = "distribution-processor-";

    private final UmpMsgMainService umpMsgMainService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpMsgQueueService umpMsgQueueService;
    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;
    private final MessageStateMachine messageStateMachine;
    private final UmpTopicSubscriptionService umpTopicSubscriptionService;

    // ==================== 消息状态事件处理器 ====================

    @Transactional(rollbackFor = Exception.class)
    public void handleMessageReceived(MqMessage<TaskData> message) {
    	log.debug("接收到消息已接收事件，消息: {}", message);
        processReceivedTask(message);
    }

    @Transactional
    public void handleMessageDistributing(MqMessage<TaskData> message) {
    	log.debug("接收到消息分发中事件，消息: {}", message);
        processDistributingTask(message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleMessageDistributed(MqMessage<TaskData> message) {
        log.debug("接收到消息已分发事件，消息: {}", message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleMessageRead(MqMessage<TaskData> message) {
    	log.debug("处理消息已读事件，消息： {}", message);
//    	
//        String messageId = ContentUtil.extractMessageId(message);
//        TaskData payload = message.getPayload();
//
//        MessageRecipient recipient = payload.getRecipient();
//        String receiverId = recipient.getReceiverId();
//        String receiverType = StrUtil.blankToDefault(recipient.getReceiverType(), MqMessageEventConstants.ReceiverTypes.USER);
//        if (receiverId == null) {
//            log.warn("消息已读事件缺少 receiverId，消息ID: {}", messageId);
//            return;
//        }
//
//        if (MqMessageEventConstants.ReceiverTypes.USER.equals(receiverType)) {
//            // 收件箱已读
//            umpMsgInboxService.lambdaUpdate()
//                    .eq(UmpMsgInbox::getMsgId, messageId)
//                    .eq(UmpMsgInbox::getReceiverId, receiverId)
//                    .set(UmpMsgInbox::getReadStatus, MessageReadStatus.UNREAD.getCode())
//                    .set(UmpMsgInbox::getReadTime, LocalDateTime.now())
//                    .update();
//            updateMessageReadCount(messageId);
//        } else {
//            // 广播已读，需获取 broadcastId
//            List<String> broadcastIds = payload.getBroadcastIds();
//            if (broadcastIds == null) {
//                broadcastIds = new ArrayList<>();
//            }
//            if (broadcastIds.isEmpty()) {
//                BroadcastDetailVO broadcast = umpMsgBroadcastService.getBroadcastByMsgId(messageId);
//
//                if (broadcast != null) {
//                    broadcastIds.add(broadcast.getId());
//                    umpMsgBroadcastService.incrementReadCount(broadcast.getId());
//                }
//            }
//            if (!broadcastIds.isEmpty()) {
//                for(String broadcastId:broadcastIds) {
//                	umpBroadcastReceiveRecordService.markAsRead(broadcastId, receiverId, receiverType);
//                }
//            }
//        }
//
//        messageStateMachine.onRead(message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handleMessageExpired(MqMessage<TaskData> message) {
        log.debug("处理消息过期事件，消息: {}", message);
    }

    @Transactional
    public void handleMessageDistFailed(MqMessage<TaskData> message) {
        log.debug("处理消息分发失败事件，消息: {}", message);
    }

    @Transactional
    public void handleMessagePushed(MqMessage<TaskData> message) {
        log.debug("处理消息已推送事件，消息: {}", message);
    }

    @Transactional
    public void handleMessagePushFailed(MqMessage<TaskData> message) {
        log.debug("处理消息推送失败事件，消息: {}", message);
    }

    @Transactional
    public void handleMessageBizReceived(MqMessage<TaskData> message) {
        log.debug("处理业务系统已接收事件，消息: {}", message);
    }

    @Transactional
    public void handleMessagePoll(MqMessage<TaskData> message) {
        log.debug("处理消息待拉取事件，消息: {}", message);
    }

    @Transactional
    public void handleMessageBizPolled(MqMessage<TaskData> message) {
        log.debug("处理业务系统已拉取事件，消息: {}", message);
    }

    @Transactional
    public void handleMessagePullFailed(MqMessage<TaskData> message) {
        log.debug("处理消息拉取失败事件，消息: {}", message);
    }

    // ==================== 写队列任务 ====================

    @Transactional(rollbackFor = Exception.class)
    public void processReceivedTask(MqMessage<TaskData> message) {
    	log.debug("processReceivedTask message: {}", message);
    	
        String messageId = ContentUtil.extractMessageId(message);
        TaskData payload = message.getPayload();

        if (StrUtil.isBlank(messageId)) {
            log.error("消息ID为空");
            return;
        }

        String title = payload.getTitle();
        if (StrUtil.isBlank(title)) {
            log.error("消息标题为空，消息ID: {}", messageId);
            return;
        }

        String status = message.getEventType();
        if (!MqMessageEventConstants.EventTypes.RECEIVED.equals(status)) {
            log.warn("消息状态不是RECEIVED，跳过创建分发任务，消息ID: {}, 状态: {}", messageId, status);
            return;
        }

        // 创建分发队列任务
        String taskId = umpMsgQueueService.createQueueTask(
                MqMessageEventConstants.QueueTaskTypes.DISTRIBUTE,
                MqMessageEventConstants.QueueNames.MESSAGE_DISTRIBUTE_QUEUE,
                messageId,
                payload,
                MqMessageEventConstants.TaskPriorities.DEFAULT,
                LocalDateTime.now(),
                MqMessageEventConstants.RetryDefaults.MAX_RETRY
        );
        
        payload.setTaskId(taskId);
        message.setPayload(payload);
        
        messageStateMachine.onDistributeStart(message);
        log.info("分发队列任务创建成功，消息ID: {}, 任务ID: {}", messageId, taskId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void processDistributingTask(MqMessage<TaskData> message) {
        if (BeanUtil.isEmpty(message)) {
            log.error("processDistributingTask 入参消息为空");
            return;
        }
        
    	log.debug("processDistributingTask message: {}", message);
    	
        String messageId = ContentUtil.extractMessageId(message);
        TaskData payload = message.getPayload();

        if (StrUtil.isBlank(messageId)) {
            log.error("processDistributingTask 消息ID为空");
            return;
        }

        String title = payload.getTitle();
        if (StrUtil.isBlank(title)) {
            log.error("processDistributingTask 消息标题为空，消息ID: {}", messageId);
            return;
        }

        String status = message.getEventType();
        if (!MqMessageEventConstants.EventTypes.DISTRIBUTING.equals(status)) {
            log.warn("processDistributingTask 消息状态不是DISTRIBUTING，跳过写收件箱/广播筒，消息ID: {}, 状态: {}", messageId, status);
            return;
        }

        String taskId = payload.getTaskId();
        UmpMsgQueue queueTask = umpMsgQueueService.getById(taskId);
        if (StrUtil.isBlank(taskId) || queueTask == null) {
            log.warn("processDistributingTask 任务ID为空或者任务不存在，taskId: {}, msgId: {}", taskId, messageId);
        }
        
        MessageRecipient recipient = payload.getRecipient();
        MessageReceivingUnit unit = payload.getUnit();
        String receiverType = payload.getReceiverType();
        MessageReceiver receiver = payload.getReceiver();
        
        if (BeanUtil.isEmpty(recipient) && BeanUtil.isEmpty(unit) && BeanUtil.isEmpty(receiver)) {
            log.error("processDistributingTask 接收者为空，消息ID: {}", message.getMessageId());
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "接收者不能为空");
        }
        
        String workerId = WORKER_PREFIX + Thread.currentThread().getId();
        payload.setWorkId(workerId);
        message.setPayload(payload);

        try {
            umpMsgQueueService.markAsProcessing(taskId, workerId);
            
            if (recipient != null) {
	            List<String> appKeys =  recipient.getAppKeys();
	            if (appKeys == null || appKeys.size() == 0) {
	            	appKeys = payload.getReceiverAppKeys();
	            	recipient.setAppKeys(appKeys);
	            }
            }
            
            if (unit != null) {
	            List<String> appKeys =  unit.getAppKeys();
	            if (appKeys == null || appKeys.size() == 0) {
	            	appKeys = payload.getReceiverAppKeys();
	            	unit.setAppKeys(appKeys);
	            }
            }
            
            if (receiver != null) {
	            List<String> appKeys =  receiver.getAppKeys();
	            if (appKeys == null || appKeys.size() == 0) {
	            	appKeys = payload.getReceiverAppKeys();
	            	receiver.setAppKeys(appKeys);
	            }
            }

            String pushMode = payload.getPushMode();
            boolean isPushMode = MqMessageEventConstants.PushModes.PUSH.equals(pushMode);

            // 如果接收者类型为USER，那么采用收件箱模式，如果接收者类型为DEPT、ALL、CUSTOM，那么采用广播信息筒模式
            if (receiverType.equals(MqMessageEventConstants.ReceiverTypes.USER)) {
            	if (BeanUtil.isNotEmpty(recipient)) {
	            	String inboxId = umpMsgInboxService.createInboxRecord(messageId, recipient, MqMessageEventConstants.DistributeModes.INBOX);
	                if (isPushMode) {
	                    // 按接收者 appKey 分组，为每个应用创建一个推送任务
	                    createPushTasksByApp(inboxId, messageId, recipient, message);
	                }
	                umpMsgQueueService.markAsSuccess(taskId, workerId, "分发成功");
	                
            	}
            } else if ( receiverType.equals(MqMessageEventConstants.ReceiverTypes.DEPT)) {
                if (BeanUtil.isNotEmpty(unit)) {
	            	// 读扩散 - 广播筒
	                String broadcastType = ReceiverUtil.determineBroadcastType(receiverType);
	                
	                // 对于广播筒：接收者类型为CUSTOM时targetScope/receiverScope不为空，接收者类型为DEPT时/receiverScope为空              
	                String broadcastId = umpMsgBroadcastService.createBroadcast(
	                        messageId,
	                        broadcastType,
	                        unit, 
	                        null,
	                        "指定部门: 1个"
	                );
	
	                if (StrUtil.isBlank(broadcastId)) {
	                    //throw new BusinessException(BusinessEnum.UMP_CREATE_FAILED.getCode(), "写广播信息筒失败");
	                	log.warn("processDistributingTask 写广播信息筒返回为空, messageId: {}， broadcastType:{}, receiverScope:", messageId, broadcastType, receiver.toString());
	                }
	                if (receiver != null && receiver.getTotalRecipientCount() != null) {
	                	umpMsgBroadcastService.updateTotalReceivers(broadcastId, receiver.getTotalRecipientCount());
	                }
	
	                if (isPushMode) {
	                    // 广播模式下，同样需要按应用推送，但广播不需要每个接收者单独推送，而是将整个广播信息推送给订阅的应用。
	                    // 因此，我们根据主题订阅获取所有订阅的应用，并为每个应用创建一个推送任务，任务中包含广播ID。
	                    // 如果主题码为空，则获取所有应用。
	                    createBroadcastPushTasks(messageId, broadcastId, payload, message);
	                }
	                umpMsgQueueService.markAsSuccess(taskId, workerId, "分发成功");
	                
                }
            } else if (receiverType.equals(MqMessageEventConstants.ReceiverTypes.CUSTOM)) {
                if (BeanUtil.isNotEmpty(receiver)) {
	            	// 读扩散 - 广播筒
	                String broadcastType = ReceiverUtil.determineBroadcastType(receiverType);
	                
	                // 对于广播筒：接收者类型为CUSTOM时targetScope/receiverScope不为空，接收者类型为DEPT时/receiverScope为空	                
	                String broadcastId = umpMsgBroadcastService.createBroadcast(
	                        messageId,
	                        broadcastType,
	                        null, 
	                        receiver.toReceivingScope(),
	                        "自定义接受者：" + receiver.getRecipients().size() + "人，" + receiver.getUnits().size() + "个部门"
	                );
	
	                if (StrUtil.isBlank(broadcastId)) {
	                    //throw new BusinessException(BusinessEnum.UMP_CREATE_FAILED.getCode(), "写广播信息筒失败");
	                	log.warn("processDistributingTask 写广播信息筒返回为空, messageId: {}， broadcastType:{}, receiverScope:", messageId, broadcastType, receiver.toString());
	                }
	                if (receiver != null && receiver.getTotalRecipientCount() != null) {
	                	umpMsgBroadcastService.updateTotalReceivers(broadcastId, receiver.getTotalRecipientCount());
	                }
	
	                if (isPushMode) {
	                    // 广播模式下，同样需要按应用推送，但广播不需要每个接收者单独推送，而是将整个广播信息推送给订阅的应用。
	                    // 因此，我们根据主题订阅获取所有订阅的应用，并为每个应用创建一个推送任务，任务中包含广播ID。
	                    // 如果主题码为空，则获取所有应用。
	                    createBroadcastPushTasks(messageId, broadcastId, payload, message);
	                }
	                umpMsgQueueService.markAsSuccess(taskId, workerId, "分发成功");
	                
                }
            } else {
                umpMsgQueueService.markAsSuccess(taskId, workerId, "接收者为空或者类型为ALL（暂不支持ALL）");
                //messageStateMachine.onDistributed(message);
            }
        } catch (BusinessException e) {
            log.error("processDistributingTask 处理分发队列任务失败，任务ID: {}, 消息ID: {}", taskId, messageId, e);
            
            // 获取当前任务信息（从数据库重新查询，确保拿到最新状态）
            UmpMsgQueue task = umpMsgQueueService.getById(taskId);
            if (task != null) {
                // 如果是首次失败（尚未重试过），将主表状态置为 DIST_RETRY
                if (task.getCurrentRetry() == 0) {
                    messageStateMachine.onRetryDistribute(message);
                }
                // 如果已经重试过但未超限，主表状态可能已经是 DIST_RETRY，无需重复更新
            }
            
            ((UmpMsgQueueService) AopContext.currentProxy()).failAndScheduleRetry(taskId, workerId, e.getMessage(), 
            		e.toString(), MqMessageEventConstants.RetryDefaults.MAX_INTERVAL);
        }
    }

    // ==================== 任务创建辅助 ====================

    /**
     * 按接收者所属应用分组，为每个应用创建推送任务（收件箱模式）
     * 注意：一个接收者可能关联多个应用，其收件箱记录将被加入到每个关联应用的推送任务中。
     */
    private void createPushTasksByApp(String msgId, String inboxId, MessageRecipient recipient, MqMessage<TaskData> message) {
    	log.debug("createPushTasksByApp msgId: {}, recipient: {}", msgId, recipient);
    	
        // 构建映射：appKey -> 收件箱ID列表
        Map<String, List<String>> appToInboxIds = new HashMap<>();

        // 如果接收者没有关联任何应用
        if (recipient.getAppKeys() == null || recipient.getAppKeys().isEmpty()) {
        	return;
        }
        
        // 为该接收者的每个应用添加收件箱ID
        List<String> appKeys = recipient.getAppKeys();

        for (String appKey : appKeys) {
            if (StrUtil.isBlank(appKey)) continue;
            appToInboxIds.computeIfAbsent(appKey, k -> new ArrayList<>()).add(inboxId);
        }

        // 为每个应用创建推送任务
        for (Map.Entry<String, List<String>> entry : appToInboxIds.entrySet()) {
            String appKey = entry.getKey();
            List<String> inboxIds = entry.getValue();
            if (inboxIds.isEmpty()) {
                continue;
            }

            TaskData taskData = message.getPayload();
            taskData.setInboxIds(inboxIds);
            // 设置接收者appKey列表（这里只有一个appKey，用单元素列表）
            taskData.setReceiverAppKeys(Collections.singletonList(appKey));
            taskData.setCallbackUrl(getCallbackUrl(msgId, appKey));

            String newTaskId = umpMsgQueueService.createQueueTask(
                    MqMessageEventConstants.QueueTaskTypes.PUSH,
                    MqMessageEventConstants.QueueNames.MESSAGE_PUSH_QUEUE,
                    msgId,
                    taskData,
                    MqMessageEventConstants.TaskPriorities.LOW,
                    LocalDateTime.now(),
                    MqMessageEventConstants.RetryDefaults.MAX_RETRY
            );
            taskData.setTaskId(newTaskId);
            message.setPayload(taskData);
            messageStateMachine.onDistributed(message);
            log.debug("为应用 {} 创建推送任务，收件箱数量: {}", appKey, inboxIds.size());
        }
    }
    
    /**
     * 为广播消息创建推送任务（广播模式）
     */
    private void createBroadcastPushTasks(String msgId, String broadcastId, TaskData originalPayload, MqMessage<TaskData> message) {
    	log.debug("createBroadcastPushTasks msgId: {}, originalPayload: {}, broadcastId: {}", msgId, originalPayload, broadcastId);
    	
        // 获取消息主表，从中获取 topicCode
        UmpMsgMain msgMain = umpMsgMainService.getById(msgId);
        if (msgMain == null) {
            log.warn("消息不存在，无法创建广播推送任务，消息ID: {}", msgId);
            return;
        }

        String topicCode = msgMain.getTopicCode();
        List<SubscriptionDetailVO> subscriptions;

        if (StrUtil.isNotBlank(topicCode)) {
            // 有主题码，查询订阅该主题的所有应用
            subscriptions = umpTopicSubscriptionService.getSubscriptionsByTopic(topicCode, 1); // 状态为1-启用
        } else {
            // 无主题码，不推送
            log.warn("消息主题码为空，无法确定订阅应用，广播推送任务被忽略，消息ID: {}", msgId);
            return;
        }

        for (SubscriptionDetailVO sub : subscriptions) {
            String appKey = sub.getAppKey();
            TaskData taskData = message.getPayload();
            List<String> broadcastIds = new ArrayList<>();
            broadcastIds.add(broadcastId);
            taskData.setBroadcastIds(broadcastIds);
            List<String> appKeys = new ArrayList<>();
            appKeys.add(appKey);
            taskData.setReceiverAppKeys(appKeys);
            taskData.setCallbackUrl(getCallbackUrl(msgId, appKey));

            String taskId = umpMsgQueueService.createQueueTask(
                    MqMessageEventConstants.QueueTaskTypes.PUSH,
                    MqMessageEventConstants.QueueNames.MESSAGE_PUSH_QUEUE,
                    msgId,
                    taskData,
                    MqMessageEventConstants.TaskPriorities.LOW,
                    LocalDateTime.now(),
                    MqMessageEventConstants.RetryDefaults.MAX_RETRY
            );
            taskData.setTaskId(taskId);
            message.setPayload(taskData);
            messageStateMachine.onDistributed(message);
        }
    }

    /**
     * 获取回调地址，优先使用消息主表的 callbackUrl，若无则通过主题订阅获取
     * @param msgId 消息ID
     * @param appKey 接收者所属应用的 appKey
     * @return 回调地址，可能为空字符串
     */
    private String getCallbackUrl(String msgId, String appKey) {
    	log.debug("getCallbackUrl msgId: {}, appKey: {}", msgId, appKey);
    	
        UmpMsgMain umpMsgMain = umpMsgMainService.getById(msgId);
        if (umpMsgMain == null) {
            return "";
        }
        String callbackUrl = umpMsgMain.getCallbackUrl();
        if (StrUtil.isNotBlank(callbackUrl)) {
        	// 优先使用消息发送时指定的回调地址
            return callbackUrl;
        }
        if (StrUtil.isBlank(appKey) || StrUtil.isBlank(umpMsgMain.getTopicCode())) {
            return "";
        }
        SubscriptionDetailVO subscription = umpTopicSubscriptionService.getSubscription(umpMsgMain.getTopicCode(), appKey);
        return subscription != null ? subscription.getCallbackUrl() : "";
    }

    private void updateMessageReadCount(String messageId) {
        Long readCount = umpMsgInboxService.lambdaQuery()
                .eq(UmpMsgInbox::getMsgId, messageId)
                .eq(UmpMsgInbox::getReadStatus, MessageReadStatus.READ.getCode())
                .count();
        umpMsgMainService.lambdaUpdate()
                .eq(UmpMsgMain::getId, messageId)
                .set(UmpMsgMain::getReadCount, readCount)
                .update();
    }
}