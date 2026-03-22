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
package ltd.huntinginfo.feng.center.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.dto.BroadcastReceiveRecordQueryDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.entity.UmpTopicSubscription;
import ltd.huntinginfo.feng.center.api.json.BrrId;
import ltd.huntinginfo.feng.center.api.json.MessageContent;
import ltd.huntinginfo.feng.center.api.json.MessageContentFooter;
import ltd.huntinginfo.feng.center.api.json.MessageContentHeader;
import ltd.huntinginfo.feng.center.api.json.MessageReceiver;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.mapper.UmpBroadcastReceiveRecordMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgQueueMapper;
import ltd.huntinginfo.feng.center.mapper.UmpTopicSubscriptionMapper;
import ltd.huntinginfo.feng.center.service.UmpTopicSubscriptionService;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;

@Slf4j
public class ContentUtil {
	/**
	 * 构建结构化的消息内容JSON
	 * 如果原内容已是JSON则直接返回，否则包装为标准格式
	 */
	static public MessageContent buildStructuredContent(MessageSendDTO sendDTO) {
	    // 尝试判断是否为JSON（简单检测首尾字符，或使用JSONUtil）
	    MessageContent messageContent = sendDTO.getMessageContent();
	    if (BeanUtil.isNotEmpty(messageContent)) { // Hutool 5.8+ 提供的方法
	        return messageContent;
	    }
	    if (messageContent == null) {
	    	messageContent = new MessageContent(); // 避免NPE
	    }
	    if (messageContent.getHeader() == null) {
	    	messageContent.setHeader(new MessageContentHeader());
	    }
	    if (StrUtil.isBlank(messageContent.getHeader().getTitle())) {
	    	messageContent.getHeader().setTitle(StrUtil.nullToDefault(sendDTO.getMessageTitle(), ""));
	    }
	    if (StrUtil.isBlank(messageContent.getHeader().getSubTitle())) {
	    	messageContent.getHeader().setSubTitle(StrUtil.nullToDefault(sendDTO.getMessageCode(), ""));
	    }
	    if (CollUtil.isEmpty(messageContent.getBody())) {
	    	messageContent.getBody().add("------");
	    }
	    if (messageContent.getFooter() == null) {
	    	messageContent.setFooter(new MessageContentFooter());
	    }
	    if (StrUtil.isBlank(messageContent.getFooter().getOrg())) {
	    	messageContent.getFooter().setOrg(StrUtil.nullToDefault(sendDTO.getSendUnitName(), ""));
	    }
	    if (messageContent.getFooter().getDate() == null) {
	    	messageContent.getFooter().setDate(new Date());
	    }
	    
	    log.debug("buildStructuredContent sendDTO: {}, messageContent: {}", sendDTO, JSONUtil.toJsonPrettyStr(messageContent));

	    return messageContent;
	}
	
	static public MqMessage<TaskData> convert(MqMessage<Map<String, Object>> message) {
    	MqMessage<TaskData> taskMsg = new MqMessage<TaskData>();
    	taskMsg.setBusinessType(message.getBusinessType());
    	taskMsg.setEventType(message.getEventType());
    	taskMsg.setExtParams(message.getExtParams());
    	taskMsg.setMessageId(message.getMessageId());
    	taskMsg.setPayload(BeanUtil.toBean(message.getPayload(), TaskData.class));
    	taskMsg.setRetryCount(message.getRetryCount());
    	taskMsg.setSendTime(message.getSendTime());
    	return taskMsg;
    }
	
	static public String extractMessageId(MqMessage<TaskData> message) {
//    	TaskData payload = message.getPayload();
//        if (payload != null && StrUtil.isNotBlank(payload.getMessageId())) {
//            return payload.getMessageId();
//        }
        return message.getMessageId();
    }
	
    /**
     * 构建消息基础事件数据（所有事件共有的字段）
     * 使用 TaskData 对象统一封装，再通过 JSON 工具转换为 Map，避免字段名出错
     */
	static public Map<String, Object> buildBaseEventData(MessageRecipient recipient, MessageReceivingUnit unit, MessageReceiver receiver,
    		UmpMsgMain message, String oldStatus, String taskId, 
    		UmpMsgInboxMapper umpMsgInboxMapper, UmpMsgBroadcastMapper umpMsgBroadcastMapper,
    		UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper, UmpTopicSubscriptionMapper umpTopicSubscriptionMapper, 
			RemoteUniqueUserService remoteUniqueUserService, RemoteUniqueDeptService remoteUniqueDeptService) {    	
        TaskData taskData = buildTaskData(recipient, unit, receiver,
    		message, oldStatus, taskId, 
    		umpMsgInboxMapper, umpMsgBroadcastMapper,
    		umpBroadcastReceiveRecordMapper, umpTopicSubscriptionMapper, 
			remoteUniqueUserService, remoteUniqueDeptService);

        return BeanUtil.beanToMap(taskData);
    }
	
	static public TaskData buildTaskData(MessageRecipient recipient, MessageReceivingUnit unit, MessageReceiver receiver,
    		UmpMsgMain message, String oldStatus, String taskId, 
    		UmpMsgInboxMapper umpMsgInboxMapper, UmpMsgBroadcastMapper umpMsgBroadcastMapper,
    		UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper, UmpTopicSubscriptionMapper umpTopicSubscriptionMapper, 
			RemoteUniqueUserService remoteUniqueUserService, RemoteUniqueDeptService remoteUniqueDeptService) {    	
        TaskData taskData = new TaskData();
        if (BeanUtil.isEmpty(message)) {
        	log.warn("buildBaseEventData message为空");
        	return taskData; 
    	}

        // 基础字段填充
        taskData.setSenderAppKey(message.getSenderAppKey());
//        taskData.setMessageId(message.getId());
        taskData.setMsgCode(message.getMsgCode());
        taskData.setTitle(message.getTitle());
        
        List<String> appKeys = getReceiverAppKeys(message.getTopicCode(), umpTopicSubscriptionMapper);
        taskData.setReceiverAppKeys(appKeys);
        
        taskData.setReceiverType(message.getSendTargetType());
        
        if (recipient != null) {
	        recipient.setAppKeys(appKeys);	        
	        taskData.setRecipient(recipient);
        }
        
        if (unit != null) {
        	unit.setAppKeys(appKeys);        
        	taskData.setUnit(unit);
        }
        
        if (receiver != null) {
        	taskData.setReceiver(receiver);
        }
        
        taskData.setPushMode(message.getPushMode());
        taskData.setCallbackUrl(message.getCallbackUrl());
        // callbackConfig 是 CallbackConfig 对象，需转为 JSON 字符串
        if (message.getCallbackConfig() != null) {
            taskData.setCallbackConfig(JSONUtil.toJsonStr(message.getCallbackConfig()));
        }
        // priority 是 Integer，转为 String
        taskData.setPriority(message.getPriority() != null ? String.valueOf(message.getPriority()) : null);

        // 时间字段统一格式化为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        if (message.getExpireTime() != null) {
            taskData.setExpireTime(message.getExpireTime().format(formatter));
        }
        if (message.getSendTime() != null) {
            taskData.setSendTime(message.getSendTime().format(formatter));
        }
        if (message.getCreateTime() != null) {
            taskData.setCreateTime(message.getCreateTime().format(formatter));
        }

        taskData.setStatus(message.getStatus());
        taskData.setOldStatus(oldStatus);
        taskData.setTaskId(taskId);

        // 查询收件箱 ID 列表
        List<UmpMsgInbox> inboxList = umpMsgInboxMapper.selectByMsgId(message.getId());
        if (!CollectionUtils.isEmpty(inboxList)) {
            List<String> inboxIds = inboxList.stream()
                    .map(UmpMsgInbox::getId)
                    .collect(Collectors.toList());
            taskData.setInboxIds(inboxIds);
            
            UmpMsgInbox inbox = inboxList.get(0);
            
            // USER时 获取recipient
            if (message.getSendTargetType().equals(MqMessageEventConstants.ReceiverTypes.USER) && recipient == null ) {
            	recipient = ReceiverUtil.buildRecipient(message.getSendTargetType(), inbox.getReceiverIdNumber(), remoteUniqueUserService, remoteUniqueDeptService);
            	taskData.setRecipient(recipient);
            }
        }

        // 查询广播 ID
        UmpMsgBroadcast broadcast = umpMsgBroadcastMapper.selectByMsgId(message.getId());

        if (broadcast != null) {
            taskData.setBroadcastIds(Collections.singletonList(broadcast.getId()));
            
            // DEPT时 获取unit
        	if (message.getSendTargetType().equals(MqMessageEventConstants.ReceiverTypes.DEPT) && unit == null) {
        		unit = ReceiverUtil.buildReceivingUnit(message.getSendTargetType(), broadcast.getReceivingUnitCode(), remoteUniqueUserService, remoteUniqueDeptService);
            	taskData.setUnit(unit);
        	}
        	
            ReceivingScope receiverScope = BeanUtil.toBean(broadcast.getReceivingScope(), ReceivingScope.class);
            
            // CUSTOM时 获取receiver
            if (message.getSendTargetType().equals(MqMessageEventConstants.ReceiverTypes.CUSTOM) && receiver == null ) {
            	receiver = ReceiverUtil.resolveReceivers(message.getSendTargetType(), receiverScope, remoteUniqueUserService, appKeys); 
            	taskData.setReceiver(receiver);
            }
            
            // 查询广播接收记录 ID 列表
            BroadcastReceiveRecordQueryDTO brrQuery = new BroadcastReceiveRecordQueryDTO();
            brrQuery.setMsgId(message.getId());
            List<UmpBroadcastReceiveRecord> brrList = umpBroadcastReceiveRecordMapper.selectByBroadcastId(broadcast.getId());
            if (!CollectionUtils.isEmpty(brrList)) {
            	List<BrrId> brrIds = new ArrayList<>();
            	for (UmpBroadcastReceiveRecord umpBroadcastReceiveRecord : brrList) {
            		BrrId brr = new BrrId(umpBroadcastReceiveRecord.getBroadcastId(), 
            				umpBroadcastReceiveRecord.getReceiverId(), umpBroadcastReceiveRecord.getReceiverType());
            		brrIds.add(brr);
            	}
                taskData.setBrrIds(brrIds);
            }
        }


        
        if (BeanUtil.isEmpty(recipient) && BeanUtil.isEmpty(unit) && BeanUtil.isEmpty(receiver)) {
            log.error("接收者为空，消息ID: {}", message.getId());
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "接收者为空，消息ID: " + message.getId());
        }
        
        return taskData;
    }
    
	static public List<String> getReceiverAppKeys(String topicCode, UmpTopicSubscriptionMapper umpTopicSubscriptionMapper) {

        List<UmpTopicSubscription> umpTopicSubscriptionList = umpTopicSubscriptionMapper.selectByTopicCode(topicCode, 1);
        List<String> receiverAppKeys = new ArrayList<>();
        for(UmpTopicSubscription detail:umpTopicSubscriptionList) {
        	receiverAppKeys.add(detail.getAppKey());
        }
        return receiverAppKeys;
    }
    
	static public String generateMsgCode() {
        // 生成唯一消息编码，格式：MSG-时间戳-随机数
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 10000));
        return "MSG-" + timestamp + "-" + random;
    }
    
	static public String getTopicCode(String rawTopicCode, String senderAppKey, UmpTopicSubscriptionService umpTopicSubscriptionService) {
        String topicCode = rawTopicCode;
    	if (StrUtil.isBlank(topicCode)) {
    	    //throw new BusinessException(BusinessEnum.UMP_CODE_INVALID.getCode(), "主题代码 [" + sendDTO.getTopicCode() + "] 为空");
    		// 业务系统没有传递topicCode，则按照发送者appkey尝试查询主题订阅，也就是说接收者appkey和发送者appkey相同
    		List<SubscriptionDetailVO> umpTopicSubscriptionList = umpTopicSubscriptionService.getSubscriptionsByApp(senderAppKey, 1);
    		if (umpTopicSubscriptionList != null && umpTopicSubscriptionList.size() > 0) {
    			topicCode = umpTopicSubscriptionList.get(0).getTopicCode();
    		}
    	}

    	return topicCode;
    }
    
	/**
	 * 构建消息
	 * @param recipient 接收个人信息（单个）
	 * @param unit 接收单位信息（单个）
	 * @param receiver 多个接收者信息
	 * @param message 主消息（注意status要设置为新状态）
	 * @param oldStatus 旧状态
	 * @param taskId 任务ID
	 * @param umpMsgInboxService 收件箱服务
	 * @param umpMsgBroadcastService 广播筒服务
	 * @param umpBroadcastReceiveRecordService 消息接收记录服务
	 * @param umpTopicSubscriptionService 消息订阅服务
	 * @param remoteUniqueUserService 远程统一用户服务
	 * @param remoteUniqueDeptService 远程统一部门（机关）服务
	 * @return 消息
	 */
	static public MqMessage<TaskData> buildMessage(MessageRecipient recipient, MessageReceivingUnit unit, MessageReceiver receiver,
	        UmpMsgMain message, String oldStatus, String taskId, 
	        UmpMsgInboxMapper umpMsgInboxMapper, UmpMsgBroadcastMapper umpMsgBroadcastMapper, UmpMsgQueueMapper umpMsgQueueMapper,
	        UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper, UmpTopicSubscriptionMapper umpTopicSubscriptionMapper, 
	        RemoteUniqueUserService remoteUniqueUserService, RemoteUniqueDeptService remoteUniqueDeptService) {
	    
	    MqMessage<TaskData> mqMessage = new MqMessage<TaskData>();
	    if (BeanUtil.isNotEmpty(message)) {
	        mqMessage.setBusinessType(message.getMsgType());
	        mqMessage.setEventType(message.getStatus());
	        mqMessage.setExtParams(JSONUtil.parseObj(message.getExtParams()));
	        mqMessage.setMessageId(message.getId());        
	        mqMessage.setSendTime(message.getSendTime());
	    }
	    
	    // 处理 retryCount：只有当 taskId 非空且存在对应记录时才设置
	    if (StrUtil.isNotBlank(taskId)) {
	        UmpMsgQueue task = umpMsgQueueMapper.selectById(taskId);
	        if (task != null) {
	            mqMessage.setRetryCount(task.getCurrentRetry());
	        } else {
	            log.warn("任务记录不存在，taskId: {}", taskId);
	            mqMessage.setRetryCount(0); // 默认值
	        }
	    } else {
	        // 没有 taskId 时，设置默认重试次数为 0
	        mqMessage.setRetryCount(0);
	    }
	    
	    Map<String, Object> eventData = ContentUtil.buildBaseEventData(recipient, unit, receiver, message, oldStatus, taskId,
	            umpMsgInboxMapper, umpMsgBroadcastMapper,
	            umpBroadcastReceiveRecordMapper, umpTopicSubscriptionMapper, 
	            remoteUniqueUserService, remoteUniqueDeptService);
	    mqMessage.setPayload(BeanUtil.toBean(eventData, TaskData.class));
	    
	    return mqMessage;
	}
}
