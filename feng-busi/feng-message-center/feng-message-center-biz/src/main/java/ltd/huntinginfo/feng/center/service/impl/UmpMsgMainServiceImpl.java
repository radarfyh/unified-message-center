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
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.validation.Valid;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpPollCursor;
import ltd.huntinginfo.feng.center.api.json.CallbackConfig;
import ltd.huntinginfo.feng.center.api.json.MessageReceiver;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.utils.CodeGeneratorUtil;
import ltd.huntinginfo.feng.center.mapper.UmpBroadcastReceiveRecordMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgMainMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgQueueMapper;
import ltd.huntinginfo.feng.center.mapper.UmpTopicSubscriptionMapper;
import ltd.huntinginfo.feng.center.service.UmpAppPermissionService;
import ltd.huntinginfo.feng.center.service.UmpBroadcastReceiveRecordService;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.center.service.UmpMsgQueueService;
import ltd.huntinginfo.feng.center.service.UmpMsgStatisticsService;
import ltd.huntinginfo.feng.center.service.UmpMsgTemplateService;
import ltd.huntinginfo.feng.center.service.UmpPollCursorService;
import ltd.huntinginfo.feng.center.service.UmpTopicSubscriptionService;
import ltd.huntinginfo.feng.center.utils.ContentUtil;
import ltd.huntinginfo.feng.center.utils.ReceiverUtil;
import ltd.huntinginfo.feng.center.utils.ReceiverUtil.ReceivingInfoResult;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MsgCodingDTO;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessagePollRequest;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessageUnreadRequest;
import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.dto.InboxQueryDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageReceivingUnitDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageRecipientDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.MsgCodingVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateRenderResult;
import ltd.huntinginfo.feng.center.api.vo.UnifiedMessageDetail;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageReadStatus;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.mq.producer.MqMessageProducer;
import ltd.huntinginfo.feng.common.core.util.UnifiedMessageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息主表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgMainServiceImpl extends ServiceImpl<UmpMsgMainMapper, UmpMsgMain> implements UmpMsgMainService {
    private final UmpMsgQueueMapper umpMsgQueueMapper;
    private final UmpMsgBroadcastMapper umpMsgBroadcastMapper;
    private final UmpMsgInboxMapper umpMsgInboxMapper;
    private final UmpTopicSubscriptionMapper umpTopicSubscriptionMapper;
    private final UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper;
    
    private final MqMessageProducer mqMessageProducer;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService;
    private final RemoteUniqueUserService remoteUniqueUserService;
    private final RemoteUniqueDeptService remoteUniqueDeptService;
    private final UmpMsgTemplateService umpMsgTemplateService;
    private final UmpTopicSubscriptionService umpTopicSubscriptionService;
    private final UmpMsgStatisticsService umpMsgStatisticsService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final UmpPollCursorService umpPollCursorService;
    
    private static final Integer MESSAGE_DEFAULT_EXPIRED_DAYS = 7;
    private static final Integer MESSAGE_DEFAULT_PRIORITY = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMessage(MessageSendDTO sendDTO) {
    	log.debug("createMessage sendDTO: {}", sendDTO);
    	
        // 验证参数
        validateMessageSendDTO(sendDTO);
        
        // 处理模板
        if (StringUtils.hasText(sendDTO.getTemplateCode())) {
            processTemplate(sendDTO);
        }

        // 构建消息实体
        UmpMsgMain message = buildMessageFromDTO(sendDTO);
        
        // 更新接收信息
        MessageReceivingUnit unit = null;
        MessageRecipient recipient = null;
        MessageReceiver receiver = null;
        
        String sendTargetType = sendDTO.getSendTargetType();
        String receivingUnitCode = sendDTO.getReceivingUnitCode();
        String receiverIdNumber = sendDTO.getReceiverIdNumber();
        Map<String, Object> rawReceivingScope = sendDTO.getReceivingScope();
        
        ReceivingInfoResult receivingInfoResult = ReceiverUtil.updateReceivingInfo(unit, recipient, receiver, 
        		sendTargetType, receivingUnitCode, receiverIdNumber, 
        		rawReceivingScope, message, 
        		umpMsgBroadcastMapper, umpMsgInboxMapper,  
        		umpTopicSubscriptionMapper, remoteUniqueUserService, remoteUniqueDeptService);
        unit = receivingInfoResult.getUnit();
        recipient = receivingInfoResult.getRecipient();
        receiver = receivingInfoResult.getReceiver();
        Integer totalReceivers = receivingInfoResult.getTotalReceivers();
        
        String oldStatus = "";

        // 保存到数据库
        if (save(message)) {
            // 异步发布消息事件到RabbitMQ            
            publishEventByStatus(recipient, unit, receiver, message, oldStatus, "");
            
            return message.getId();
        } else {
            log.error("消息创建失败，发送方: {}", sendDTO.getSenderAppKey());
            throw new RuntimeException("消息创建失败");
        }
    }

    private void processTemplate(MessageSendDTO sendDTO) {
        // 1. 获取模板并校验
        TemplateDetailVO template = umpMsgTemplateService.getByTemplateCode(sendDTO.getTemplateCode());
        if (template == null) {
            throw new IllegalArgumentException("模板不存在: " + sendDTO.getTemplateCode());
        }
        if (template.getStatus() != 1) {
            throw new IllegalStateException("模板未启用: " + sendDTO.getTemplateCode());
        }

        // 2. 验证变量（可选）
        Map<String, Object> validation = umpMsgTemplateService.validateTemplateVariables(
                sendDTO.getTemplateCode(), sendDTO.getTemplateVariables());
        if (!Boolean.TRUE.equals(validation.get("valid"))) {
            throw new IllegalArgumentException("模板变量验证失败: " + validation.get("message"));
        }

        // 3. 渲染模板
        TemplateRenderResult renderResult = umpMsgTemplateService.renderTemplate(
                sendDTO.getTemplateCode(), sendDTO.getTemplateVariables());

        // 4. 将渲染结果填充到 DTO
        if (renderResult.getTitle() != null) {
            sendDTO.setMessageTitle(renderResult.getTitle());
        }
        if (renderResult.getContent() != null) {
            // 将 MessageContent 对象序列化为 JSON 字符串，供后续 buildMessageFromDTO 处理
            sendDTO.setMessageContent(renderResult.getContent());
        }

        // 5. 合并模板默认配置（优先级、推送方式、回调地址等）
        if (sendDTO.getPriority() == null && template.getDefaultPriority() != null) {
            sendDTO.setPriority(String.valueOf(template.getDefaultPriority()));
        }
        if (!StringUtils.hasText(sendDTO.getPushMode()) && StringUtils.hasText(template.getDefaultPushMode())) {
            sendDTO.setPushMode(template.getDefaultPushMode());
        }
        if (!StringUtils.hasText(sendDTO.getProcessUrl()) && StringUtils.hasText(template.getDefaultCallbackUrl())) {
            sendDTO.setProcessUrl(template.getDefaultCallbackUrl());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createAgentMessage(MessageSendDTO sendDTO, String agentAppKey, String agentMsgId) {
        // 验证参数
        validateMessageSendDTO(sendDTO);
        
        if (!StringUtils.hasText(agentAppKey) || !StringUtils.hasText(agentMsgId)) {
            throw new IllegalArgumentException("代理平台标识和代理消息ID不能为空");
        }
        
        // 处理模板
        if (StringUtils.hasText(sendDTO.getTemplateCode())) {
            processTemplate(sendDTO);
        }

        // 构建消息实体
        UmpMsgMain message = buildMessageFromDTO(sendDTO);
        message.setAgentAppKey(agentAppKey);
        message.setAgentMsgId(agentMsgId);
        
        // 更新接收信息
        MessageReceivingUnit unit = null;
        MessageRecipient recipient = null;
        MessageReceiver receiver = null;
        
        String sendTargetType = sendDTO.getSendTargetType();
        String receivingUnitCode = sendDTO.getReceivingUnitCode();
        String receiverIdNumber = sendDTO.getReceiverIdNumber();
        Map<String, Object> rawReceivingScope = sendDTO.getReceivingScope();
        
        ReceivingInfoResult receivingInfoResult = ReceiverUtil.updateReceivingInfo(unit, recipient, receiver, 
        		sendTargetType, receivingUnitCode, receiverIdNumber, 
        		rawReceivingScope, message, 
        		umpMsgBroadcastMapper, umpMsgInboxMapper,
        		umpTopicSubscriptionMapper, remoteUniqueUserService, remoteUniqueDeptService);
        
        unit = receivingInfoResult.getUnit();
        recipient = receivingInfoResult.getRecipient();
        receiver = receivingInfoResult.getReceiver();
        Integer totalReceivers = receivingInfoResult.getTotalReceivers();
        
        String oldStatus = "";

        // 保存到数据库
        if (save(message)) {
            log.info("代理消息创建成功，消息ID: {}, 代理消息ID: {}", message.getId(), agentMsgId);
            
            // 异步发布消息事件到RabbitMQ
            publishEventByStatus(recipient, unit, receiver, message, oldStatus, "");
            
            return message.getId();
        } else {
            log.error("代理消息创建失败，代理平台: {}, 代理消息ID: {}", agentAppKey, agentMsgId);
            throw new RuntimeException("代理消息创建失败");
        }
    }

    @Override
    public MessageDetailVO getMessageByCode(String msgCode) {
        if (!StringUtils.hasText(msgCode)) {
            throw new IllegalArgumentException("消息编码不能为空");
        }

        UmpMsgMain message = baseMapper.selectByMsgCode(msgCode);
        if (message == null) {
            log.warn("消息不存在，消息编码: {}", msgCode);
            return null;
        }

        return convertToDetailVO(message);
    }
    
    @Override
    public MessageDetailVO getMessageById(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        UmpMsgMain message = baseMapper.selectById(msgId);
        if (message == null) {
            log.warn("消息不存在，消息ID: {}", msgId);
            return null;
        }

        return convertToDetailVO(message);
    }

    @Override
    public Page<MessagePageVO> queryMessagePage(MessageQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getSenderAppKey())) {
            queryWrapper.eq(UmpMsgMain::getSenderAppKey, queryDTO.getSenderAppKey());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgType())) {
            queryWrapper.eq(UmpMsgMain::getMsgType, queryDTO.getMsgType());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgMain::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getTitle())) {
            queryWrapper.like(UmpMsgMain::getTitle, queryDTO.getTitle());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgMain::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgMain::getCreateTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgMain::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgMain> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgMain> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<MessagePageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<MessagePageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    private void applySort(LambdaQueryWrapper<UmpMsgMain> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getCreateTime);
                }
                break;
            case "sendTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getSendTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getSendTime);
                }
                break;
            case "priority":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getPriority);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getPriority);
                }
                break;
            case "title":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgMain::getTitle);
                } else {
                    queryWrapper.orderByDesc(UmpMsgMain::getTitle);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgMain::getCreateTime);
                break;
        }
    }
    
    @Override
    public boolean updateMessageStatus(String status, MqMessage<TaskData> message) {
        if (!StringUtils.hasText(status)) {
    		log.error("updateMessageStatus 入参status为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "updateMessageStatus 入参status为空");
        }
    	if (BeanUtil.isEmpty(message)) {
    		log.error("updateMessageStatus 入参message为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "updateMessageStatus 入参message为空");
    	}
    	TaskData taskData = message.getPayload();
    	if (BeanUtil.isEmpty(taskData)) {
    		log.error("updateMessageStatus 入参携带数据payload为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "updateMessageStatus 入参携带数据payload为空");
    	}
    	
        MessageReceivingUnit unit = taskData.getUnit();
        MessageRecipient recipient = taskData.getRecipient();
        MessageReceiver receiver = taskData.getReceiver();
        
        String msgId = message.getMessageId();
//    	if (StrUtil.isBlank(msgId)) {
//    		msgId = taskData.getMessageId();  
//    		message.setMessageId(msgId);
//    	}
        if (!StringUtils.hasText(msgId)) {
    		log.error("updateMessageStatus msgId为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "updateMessageStatus msgId为空");
        }
    	
        String sendTargetType = taskData.getReceiverType();
    	if (StrUtil.isBlank(sendTargetType)) {
    		log.error("发送对象类型为空，消息ID: {}", msgId);
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "发送对象类型为空,消息ID：" + msgId);
    	}
    	
        UmpMsgMain umpMsgMain = getById(msgId);
        if (BeanUtil.isEmpty(umpMsgMain)) {
            log.error("updateMessageStatus 消息主记录不存在，消息ID: {}", msgId);
            //throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "消息主记录不存在，消息ID: " + msgId);
            return false;
        }
        
        // 更新接收信息
        String receivingUnitCode = unit != null ? unit.getReceivingUnitCode() : null;
        String receiverIdNumber = recipient != null ? recipient.getReceiverIdNumber() : null;
        Map<String, Object> rawReceivingScope = receiver != null ? BeanUtil.beanToMap(receiver.toReceivingScope()) : null;
        
        ReceivingInfoResult receivingInfoResult = ReceiverUtil.updateReceivingInfo(unit, recipient, receiver, 
        		sendTargetType, receivingUnitCode, receiverIdNumber, 
        		rawReceivingScope, umpMsgMain, 
        		umpMsgBroadcastMapper, umpMsgInboxMapper,
        		umpTopicSubscriptionMapper, remoteUniqueUserService, remoteUniqueDeptService);
        
        unit = receivingInfoResult.getUnit();
        recipient = receivingInfoResult.getRecipient();
        receiver = receivingInfoResult.getReceiver();
        Integer totalReceivers = receivingInfoResult.getTotalReceivers();
        
        taskData.setUnit(unit);
        taskData.setReceiver(receiver);
        taskData.setRecipient(recipient);
        
        // 旧状态
        String oldStatus = taskData.getStatus();
        
        // 新状态
        taskData.setStatus(status);
        umpMsgMain.setStatus(status);
        
        // 根据状态设置相应的时间字段
        switch (status) {
            case MqMessageEventConstants.EventTypes.RECEIVED:
            case MqMessageEventConstants.EventTypes.BIZ_PULLED:
            case MqMessageEventConstants.EventTypes.BIZ_RECEIVED:
            	umpMsgMain.setSendTime(LocalDateTime.now());
                break;
            case MqMessageEventConstants.EventTypes.DISTRIBUTED:
            	umpMsgMain.setDistributeTime(LocalDateTime.now());
            	umpMsgMain.setTotalReceivers(totalReceivers);
                break;
            case MqMessageEventConstants.EventTypes.READ:
            case MqMessageEventConstants.EventTypes.PULL_FAILED:
            case MqMessageEventConstants.EventTypes.PUSH_FAILED:
            case MqMessageEventConstants.EventTypes.DIST_FAILED:
            case MqMessageEventConstants.EventTypes.EXPIRED:
            	umpMsgMain.setCompleteTime(LocalDateTime.now());
                break;
        }
        if (baseMapper.updateById(umpMsgMain) <= 0) {
        	return false;
        }        
        
        String topicCode = ContentUtil.getTopicCode(umpMsgMain.getTopicCode(), umpMsgMain.getSenderAppKey(), umpTopicSubscriptionService);
        // 补全主题代码后才发送消息，注意即使有变化，也不更新消息主表记录，保留主表遵从初始消息发送时原样
        umpMsgMain.setTopicCode(topicCode);
        
        String taskId = taskData.getTaskId();
        
        publishEventByStatus(recipient, unit, receiver, umpMsgMain, oldStatus, taskId);
        
        log.debug("updateMessageStatus 消息状态更新成功，消息ID: {}, 旧状态: {}, 新状态: {}, 任务ID：{}", 
                msgId, oldStatus, status, taskId);

        return true;
    }

    @Override
    public boolean updateReadStatistics(String msgId, int readCount) {
        if (!StringUtils.hasText(msgId) || readCount < 0) {
            throw new IllegalArgumentException("消息ID和已读人数不能为空");
        }

        UmpMsgMain message = getById(msgId);
        if (BeanUtil.isEmpty(message)) {
            log.warn("消息不存在，消息ID: {}", msgId);
            return false;
        }

        int updated = baseMapper.updateReadCount(msgId, readCount);
        if (updated > 0) {
            log.debug("消息已读统计更新成功，消息ID: {}, 已读人数: {}", msgId, readCount);
            return true;
        }

        return false;
    }

    @Override
    public MessageStatisticsVO getMessageStatistics(LocalDateTime startTime, LocalDateTime endTime, String appKey) {
        List<Map<String, Object>> statsList = baseMapper.selectMessageStatistics(startTime, endTime, appKey);
        
        MessageStatisticsVO statisticsVO = new MessageStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setAppKey(appKey);
        
        // 计算汇总统计
        int totalCount = 0;
        int successCount = 0;
        int failedCount = 0;
        int totalReceivers = 0;
        int totalRead = 0;
        
        for (Map<String, Object> stat : statsList) {
            Integer count = (Integer) stat.get("count");
            String status = (String) stat.get("status");
            Integer receivers = (Integer) stat.getOrDefault("receivers", 0);
            Integer readCount = (Integer) stat.getOrDefault("read_count", 0);
            
            totalCount += count;
            totalReceivers += receivers;
            totalRead += readCount;
            
            if ("SENT".equals(status) || "READ".equals(status)) {
                successCount += count;
            } else if ("FAILED".equals(status)) {
                failedCount += count;
            }
        }
        
        statisticsVO.setTotalCount(totalCount);
        statisticsVO.setSuccessCount(successCount);
        statisticsVO.setFailedCount(failedCount);
        statisticsVO.setTotalReceivers(totalReceivers);
        statisticsVO.setTotalRead(totalRead);
        
        if (totalCount > 0) {
            statisticsVO.setSuccessRate((double) successCount / totalCount * 100);
            statisticsVO.setReadRate(totalReceivers > 0 ? (double) totalRead / totalReceivers * 100 : 0);
        }
        
        return statisticsVO;
    }
    
    @Override
    public MessageStatisticsVO getMessageStatisticsGroupByAppAndType(LocalDate targetDate) {
    	log.debug("开始统计日期 {} 的消息数据", targetDate);
        LocalDateTime startTime = targetDate.atStartOfDay();
        LocalDateTime endTime = targetDate.atTime(LocalTime.MAX);
    	
    	List<Map<String, Object>> msgStats = baseMapper.selectMessageStatisticsGroupByAppAndType(startTime, endTime);
    	
        MessageStatisticsVO statisticsVO = new MessageStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        
        int savedCount = 0;        
        for (Map<String, Object> stat : msgStats) {
            String appKey = (String) stat.get("app_key");
            String msgType = (String) stat.get("msg_type");
            long sendCount = ((Number) stat.getOrDefault("send_count", 0)).longValue();
            long sendSuccessCount = ((Number) stat.getOrDefault("send_success_count", 0)).longValue();
            long sendFailedCount = ((Number) stat.getOrDefault("send_failed_count", 0)).longValue();
            long receiveCount = ((Number) stat.getOrDefault("receive_count", 0)).longValue();
            long readCount = ((Number) stat.getOrDefault("read_count", 0)).longValue();
            long errorCount = ((Number) stat.getOrDefault("error_count", 0)).longValue(); 
            long retryCount = ((Number) stat.getOrDefault("retry_count", 0)).longValue();

            // 调用统计服务保存
            boolean success = umpMsgStatisticsService.upsertStatistics(
                    targetDate, appKey, msgType,
                    (int) sendCount,
                    (int) sendSuccessCount,
                    (int) sendFailedCount,
                    (int) receiveCount,
                    (int) readCount,
                    (int) errorCount,
                    (int) retryCount,
                    null, null, null // 时间指标暂不统计
            );
            if (success) savedCount++;
        }
        log.debug("停止统计日期 {} 的消息数据，条数：", targetDate, savedCount);
        return statisticsVO;
    }

    @Override
    public List<MessageDetailVO> getAllUnreadMessages(int limit) {
    	// 直接读状态，前提是收件箱和广播信息筒状态改变时同步修改主表的状态
    	// 用户可以读但是未读只有两种状态：PUSHED， BIZ_PULLED
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgMain::getDelFlag, 0)
                   .in(UmpMsgMain::getStatus, MqMessageEventConstants.EventTypes.PUSHED, MqMessageEventConstants.EventTypes.BIZ_PULLED)
                   .orderByDesc(UmpMsgMain::getCreateTime)
                   .last("LIMIT " + limit);
        
        List<UmpMsgMain> messages = list(queryWrapper);
        return messages.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageDetailVO> getUnreadMessages(String receiverId, String receiverType, int limit) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        List<MessageDetailVO> result = new ArrayList<>();

        // 1. 查询收件箱中未读的点对点消息
        List<UmpMsgInbox> inboxList = umpMsgInboxService.lambdaQuery()
                .eq(UmpMsgInbox::getReceiverId, receiverId)
                .eq(UmpMsgInbox::getReceiverType, receiverType)
                .eq(UmpMsgInbox::getReadStatus, MessageReadStatus.UNREAD)
                .orderByDesc(UmpMsgInbox::getDistributeTime)
                .last("LIMIT " + limit)
                .list();

        if (!CollectionUtils.isEmpty(inboxList)) {
            List<String> msgIds = inboxList.stream()
                    .map(UmpMsgInbox::getMsgId)
                    .collect(Collectors.toList());

            // 使用 lambdaQuery 查询消息主表，并手动添加 del_flag = '0' 条件
            List<UmpMsgMain> msgList = lambdaQuery()
                    .in(UmpMsgMain::getId, msgIds)
                    .eq(UmpMsgMain::getDelFlag, 0)
                    .list();

            Map<String, UmpMsgMain> msgMap = msgList.stream()
                    .collect(Collectors.toMap(UmpMsgMain::getId, Function.identity()));

            for (UmpMsgInbox inbox : inboxList) {
                UmpMsgMain msg = msgMap.get(inbox.getMsgId());
                if (msg != null) {
                    MessageDetailVO vo = convertToDetailVO(msg);
                    result.add(vo);
                }
            }
        }

        // 2. 如果数量不足，再查询广播中未读的消息
        if (result.size() < limit) {
            int remaining = limit - result.size();
            List<UmpBroadcastReceiveRecord> broadcastRecords = umpBroadcastReceiveRecordService.lambdaQuery()
                    .eq(UmpBroadcastReceiveRecord::getReceiverId, receiverId)
                    .eq(UmpBroadcastReceiveRecord::getReceiverType, receiverType)
                    .eq(UmpBroadcastReceiveRecord::getReadStatus, MessageReadStatus.UNREAD)
                    .orderByDesc(UmpBroadcastReceiveRecord::getCreateTime)
                    .last("LIMIT " + remaining)
                    .list();

            if (!CollectionUtils.isEmpty(broadcastRecords)) {
                List<String> broadcastIds = broadcastRecords.stream()
                        .map(UmpBroadcastReceiveRecord::getBroadcastId)
                        .collect(Collectors.toList());

                List<UmpMsgBroadcast> broadcasts = umpMsgBroadcastService.listByIds(broadcastIds);

                List<String> msgIds = broadcasts.stream()
                        .map(UmpMsgBroadcast::getMsgId)
                        .collect(Collectors.toList());

                // 同样使用 lambdaQuery 查询消息主表，确保逻辑删除过滤
                List<UmpMsgMain> msgList = lambdaQuery()
                        .in(UmpMsgMain::getId, msgIds)
                        .eq(UmpMsgMain::getDelFlag, 0)
                        .list();

                Map<String, UmpMsgMain> msgMap = msgList.stream()
                        .collect(Collectors.toMap(UmpMsgMain::getId, Function.identity()));

                for (UmpBroadcastReceiveRecord record : broadcastRecords) {
                    UmpMsgBroadcast broadcast = broadcasts.stream()
                            .filter(b -> b.getId().equals(record.getBroadcastId()))
                            .findFirst()
                            .orElse(null);
                    if (broadcast != null) {
                        UmpMsgMain msg = msgMap.get(broadcast.getMsgId());
                        if (msg != null) {
                            MessageDetailVO vo = convertToDetailVO(msg);
                            result.add(vo);
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public boolean existsAndValid(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            return false;
        }
        
        LambdaQueryWrapper<UmpMsgMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgMain::getId, msgId)
                   .eq(UmpMsgMain::getDelFlag, 0);
        
        return count(queryWrapper) > 0;
    }

    // ============ 私有方法 ============

    private void validateMessageSendDTO(MessageSendDTO sendDTO) {
        if (sendDTO == null) {
            throw new IllegalArgumentException("消息发送DTO不能为空");
        }
        
        if (!StringUtils.hasText(sendDTO.getSenderAppKey())) {
            throw new IllegalArgumentException("发送应用标识不能为空");
        }
        
        if (!StringUtils.hasText(sendDTO.getMessageTitle())) {
            throw new IllegalArgumentException("消息标题不能为空");
        }
        
        if (sendDTO.getMessageContent() == null || 
        		BeanUtil.isEmpty(sendDTO.getMessageContent())) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        
        if (!ReceiverUtil.isValidReceiver(sendDTO)) {
        	throw new IllegalArgumentException("接收者不能为空（登录ID列表和单位ID列表不能同时为空）");
        }
    }

    private UmpMsgMain buildMessageFromDTO(MessageSendDTO sendDTO) {
        UmpMsgMain message = new UmpMsgMain();

        message.setSenderUnitName(sendDTO.getSendUnitName());
        message.setSenderUnitCode(sendDTO.getSendUnitCode());
        message.setSenderName(sendDTO.getSenderName());
        message.setSenderId(sendDTO.getSenderId());
        message.setSenderIdNumber(sendDTO.getSenderIdNumber());
        message.setSenderAppKey(sendDTO.getSenderAppKey());
        message.setSenderType(sendDTO.getSenderType());  
        
        message.setSendTargetType(sendDTO.getSendTargetType());
        
        if (StrUtil.isBlank(message.getMsgCode())) {
            message.setMsgCode(ContentUtil.generateMsgCode());
        } else {
        	message.setMsgCode(sendDTO.getMessageCode());
        }
        
        if (StrUtil.isBlank(message.getMsgType())) {
            message.setMsgType(MqMessageEventConstants.BusinessTypes.NOTICE);
        } else {
        	message.setMsgType(sendDTO.getMessageType());
        }
        message.setTitle(sendDTO.getMessageTitle());
        message.setContent(ContentUtil.buildStructuredContent(sendDTO));
        
        message.setProcessUrl(sendDTO.getProcessUrl());
        message.setBusinessParam(sendDTO.getBusinessParam());        
        
        if (StrUtil.isBlank(message.getPushMode())) {
            message.setPushMode(MqMessageEventConstants.PushModes.PUSH);
        } else {
        	message.setPushMode(sendDTO.getPushMode());
        }
        message.setCallbackUrl(sendDTO.getProcessUrl());
        if (sendDTO.getCallbackConfig() != null) {
            message.setCallbackConfig(BeanUtil.toBean(sendDTO.getCallbackConfig(), CallbackConfig.class));
        }
        
        if (StrUtil.isNotBlank(sendDTO.getPriority())) {
            try {
                message.setPriority(Integer.parseInt(sendDTO.getPriority()));
            } catch (NumberFormatException e) {
                message.setPriority(3);
            }
        } else {
            message.setPriority(MESSAGE_DEFAULT_PRIORITY);
        }
        
        if (StrUtil.isNotBlank(sendDTO.getBusinessParam())) {
            message.setExtParams(sendDTO.getBusinessParam());
        }
        
        // 设置过期时间（默认7天）
        if (message.getExpireTime() == null) {
            message.setExpireTime(LocalDateTime.now().plusDays(MESSAGE_DEFAULT_EXPIRED_DAYS));
        } else {
        	message.setExpireTime(sendDTO.getExpireTime());
        }

        // 代理相关字段置空（非代理消息）
        message.setAgentAppKey(null);
        message.setAgentMsgId(null);
        
        // 主题处理
        String topicCode = ContentUtil.getTopicCode(sendDTO.getTopicCode(), sendDTO.getSenderAppKey(), umpTopicSubscriptionService);
        message.setTopicCode(topicCode);
        
        // 设置初始状态
        message.setStatus(MqMessageEventConstants.EventTypes.RECEIVED);

        return message;
    }
    
    private MessageDetailVO convertToDetailVO(UmpMsgMain message) {
        MessageDetailVO vo = new MessageDetailVO();
        BeanUtils.copyProperties(message, vo);
    	
        return vo;
    }
    
    private UnifiedMessageDetail convertToUnifiedDetail(UmpMsgMain message) {
    	if (BeanUtil.isEmpty(message)) {
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "消息不能为空");
    	}
    	
    	UnifiedMessageDetail detail = new UnifiedMessageDetail();
        BeanUtils.copyProperties(message, detail);
        detail.setBusinessParam(message.getExtParams());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        detail.setDistributeTime(message.getDistributeTime().format(formatter));
        detail.setIcon(null);
        detail.setMessageCode(message.getMsgCode());
        detail.setMessageContent(message.getContent());
        detail.setMessageId(message.getId());
        detail.setMessageTitle(message.getTitle());
        detail.setMessageType(message.getMsgType());
        detail.setPriority(message.getPriority().toString());
        detail.setProcessStatus(message.getStatus());
        detail.setProcessUrl(message.getCallbackUrl());
        
        switch (message.getSendTargetType()) {
        case MqMessageEventConstants.ReceiverTypes.USER:
            // 查询收件箱 ID 列表
            InboxQueryDTO inboxQuery = new InboxQueryDTO();
            inboxQuery.setMsgId(message.getId());
            List<InboxDetailVO> inboxList = umpMsgInboxService.queryInboxList(inboxQuery);
            for(InboxDetailVO inbox : inboxList) {
            	if (BeanUtil.isNotEmpty(inbox)) {

            		if (StrUtil.isBlank(inbox.getReceiverId())) {
                    	Map<String, Object> result = remoteUniqueUserService.getDetailById(inbox.getReceiverId());
                    	
                    	UniqueUser user = BeanUtil.toBean(result, UniqueUser.class);
                    	
                    	String idNumber = user.getIdCard();
                        detail.setReceiverIdNumber(idNumber);
                        detail.setReceiverName(user.getName());
            		} else {
                		detail.setReceiverIdNumber(inbox.getReceiverIdNumber());
                		detail.setReceiverName(inbox.getReceiverName());
            		}
            		if (StrUtil.isBlank(inbox.getReceivingUnitId())) {
                    	Map<String, Object> result = remoteUniqueDeptService.getAgencyById(inbox.getReceivingUnitId());
                    	GovAgency agency = BeanUtil.toBean(result, GovAgency.class);
                    	
                        detail.setReceivingUnitCode(agency.getCode());
                        detail.setReceivingUnitName(agency.getName());
            		} else {
	            		detail.setReceiverPhone(inbox.getReceiverPhone());
	            		detail.setReceivingUnitCode(inbox.getReceivingUnitCode());
	            		detail.setReceivingUnitName(inbox.getReceivingUnitName());
            		}
            		break;
            	}
            }
            break;
        case MqMessageEventConstants.ReceiverTypes.DEPT:
            // 查询广播 ID
            BroadcastDetailVO broadcast = umpMsgBroadcastService.getBroadcastByMsgId(message.getId());
            detail.setReceivingUnitCode(broadcast.getReceivingUnitCode());
            detail.setReceivingUnitName(broadcast.getReceivingUnitName());
            detail.setReceivingScope(JSONUtil.toJsonStr(broadcast.getReceivingScope()));
            break;
        case MqMessageEventConstants.ReceiverTypes.CUSTOM:
        default:
            // 查询广播 ID
            BroadcastDetailVO vo = umpMsgBroadcastService.getBroadcastByMsgId(message.getId());
        	detail.setReceivingScope(JSONUtil.toJsonStr(vo.getReceivingScope()));
        }

        String senderId = message.getSenderId();
        if (StrUtil.isNotBlank(senderId)) {
        	Map<String, Object> result = remoteUniqueUserService.getDetailById(senderId);
        	
        	UniqueUser user = BeanUtil.toBean(result, UniqueUser.class);
        	
        	String idNumber = user.getIdCard();
            detail.setSenderIdNumber(idNumber);
            detail.setSenderName(user.getName());
        }

        detail.setSendTargetType(message.getSendTargetType());
        detail.setSendTime(message.getSendTime() != null ? message.getSendTime().format(formatter) : null);
        detail.setSendUnitCode(message.getSenderUnitCode());
        detail.setSendUnitName(message.getSenderUnitName());

        return detail;
    }

    private MessagePageVO convertToPageVO(UmpMsgMain message) {
        MessagePageVO vo = new MessagePageVO();
        BeanUtils.copyProperties(message, vo);
        
        // 计算已读率
        if (message.getTotalReceivers() != null && message.getTotalReceivers() > 0 
                && message.getReadCount() != null) {
            vo.setReadRate((double) message.getReadCount() / message.getTotalReceivers() * 100);
        }
        
        return vo;
    }

	@Override
	public boolean updateReceiverCount(String messageId, Integer totalReceivers, 
			Integer receivedCount, Integer readCount) {
		
		UmpMsgMain message = this.getById(messageId);
		message.setReceivedCount(receivedCount);
		message.setTotalReceivers(totalReceivers);
		message.setReadCount(readCount);
		
		int ret = baseMapper.updateById(message);
		
		return (ret > 0) ? true : false;
	}

    // ==================== 事件发布统一入口 ====================

    /**
     * 根据消息新状态发布对应的事件
     */
    private void publishEventByStatus(MessageRecipient recipient, MessageReceivingUnit unit, MessageReceiver receiver, 
    		UmpMsgMain message, String oldStatus, String taskId) {
        if (BeanUtil.isEmpty(recipient) && BeanUtil.isEmpty(unit) && BeanUtil.isEmpty(receiver)) {
            log.error("接收者为空，消息ID: {}", message.getId());
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "接收者为空，消息ID: " + message.getId());
        }

        log.info("发送SPRING事件 unit: {} recipient: {} receiver: {} message: {} oldStatus: {} taskId: {}", unit, recipient, receiver, message, oldStatus, taskId);
        
    	// 改为发布 Spring 事件
        applicationEventPublisher.publishEvent(new MessageStatusChangedEvent(unit, recipient, receiver, message, oldStatus, taskId));
    }
    
    /**
     * 定义内部事件监听器，确保事务提交后执行。
     * 解決问题：MQ消费者收到MQ消息后，数据库记录却还没有写完成
     * @param event
     */
    @Async("messageTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageStatusChanged(MessageStatusChangedEvent event) {
    	log.info("接收到SPRING事件 MessageStatusChangedEvent: {}", event);

        // 真正发送 MQ 消息
    	publishMqEventByStatus(event);
    }
    
    /**
     * 根据消息新状态发布对应的事件
     */
    private void publishMqEventByStatus(MessageStatusChangedEvent event) {
    	if (BeanUtil.isEmpty(event)) {
    		log.warn("publishMqEventByStatus 入参为空");
    		return;
    	}
    	
    	MessageRecipient recipient = event.getRecipient();
    	MessageReceivingUnit unit = event.getUnit(); 
    	MessageReceiver receiver = event.getReceiver();
    	UmpMsgMain message = event.getMessage();
    	String oldStatus = event.getOldStatus();
    	String taskId = event.getTaskId();
        executePublish(() -> {
            Map<String, Object> eventData = ContentUtil.buildBaseEventData(recipient, unit, receiver, message, oldStatus, taskId,
            		umpMsgInboxMapper, umpMsgBroadcastMapper,
            		umpBroadcastReceiveRecordMapper, umpTopicSubscriptionMapper, 
        			remoteUniqueUserService, remoteUniqueDeptService);
            
            log.info("发送MQ事件 eventData: {}", eventData);
            
            switch (message.getStatus()) {
		        case MqMessageEventConstants.EventTypes.RECEIVED:
		        	// 将会进入ump_msg_queue表，队列类型：分发
		        	mqMessageProducer.sendMessageReceived(eventData, message.getMsgType(), message.getId());
		            break;	
		        case MqMessageEventConstants.EventTypes.DISTRIBUTING:
		        	// 将会写收件箱或者广播信息筒
		        	mqMessageProducer.sendMessageDistributeStart(eventData, message.getMsgType(), message.getId());
		            break;	
		        case MqMessageEventConstants.EventTypes.DISTRIBUTED:
		        	// 将会进入ump_msg_queue表，队列类型：推送
		        	mqMessageProducer.sendMessageDistributed(eventData, message.getMsgType(), message.getId());
	                break;
	            case MqMessageEventConstants.EventTypes.DIST_FAILED:
	                break;
	            case MqMessageEventConstants.EventTypes.DIST_RETRY:
	            	// 不发送MQ事件，由定时调度任务触发
	                break;
	            case MqMessageEventConstants.EventTypes.PUSHED:
	            	// 不会发布事件，不会进入ump_msg_queue表
	            	//mqMessageProducer.sendMessagePushed(eventData, message.getMsgType(), message.getId());
	                break;
	            case MqMessageEventConstants.EventTypes.PUSH_FAILED:
	                break;
	            case MqMessageEventConstants.EventTypes.PUSH_RETRY:
	            	// 不发送MQ事件，由定时调度任务触发
	                break;
	            case MqMessageEventConstants.EventTypes.BIZ_RECEIVED:
	            	//业务系统上报接收成功（暂定调用回调地址成功就表示业务已接收）（不直接更改状态，而是发布到MQ，进入ump_msg_queue表进行专门处理）
	            	mqMessageProducer.sendMessageBusinessReceived(eventData, message.getMsgType(), message.getId());
	                break;
	            case MqMessageEventConstants.EventTypes.PULL:
	            	//mqMessageProducer.sendMessagePullReady(eventData, message.getMsgType(), message.getId());
	                break;
	            case MqMessageEventConstants.EventTypes.BIZ_PULLED:
	            	//业务系统上报拉取成功（暂定查询成功就表示业务已拉取）（不直接更改状态，而是发布到MQ，进入ump_msg_queue表进行专门处理）
	            	mqMessageProducer.sendMessageBusinessPulled(eventData, message.getMsgType(), message.getId());
	                break;
	            case MqMessageEventConstants.EventTypes.PULL_FAILED:
	            	// 永久失败
	                break;
	            case MqMessageEventConstants.EventTypes.READ:
	            	//业务系统上报已读（不直接更改状态，而是发布到MQ，进入ump_msg_queue表进行专门处理）
	            	mqMessageProducer.sendMessageRead(eventData, message.getMsgType(), message.getId());
	                break;
	            case MqMessageEventConstants.EventTypes.EXPIRED:
	            	// 不发布MQ事件，由定时调度任务触发
	                break;
	            default:
	                log.debug("状态 {} 无需发布事件", message.getStatus());
	        }
            
            log.debug("MQ消息已接收事件发布成功, 消息: {}, 事件数据: {}, 任务ID: {}", message, eventData, taskId);
        }, "消息已接收事件发布失败, 消息: {}, 任务ID: {}", message, taskId);

    }

    // ==================== 辅助方法 ====================

    /**
     * 统一执行事件发布，捕获异常并记录日志
     */
    private void executePublish(Runnable publishAction, String errorMsg, Object... args) {
        try {
            publishAction.run();
        } catch (Exception e) {
            log.error(errorMsg, args, e);
            // 不抛出异常，不影响主流程
        }
    }
    
    // ========================================
    
    @Override
    public UnifiedMessageResponse getUnreadMessagesByApp(String appKey, UnifiedMessageUnreadRequest request) {
        // 1. 获取身份证号
        String idCard = request.getCxrxx() != null ? request.getCxrxx().getCxrzjhm() : null;
        if (StrUtil.isBlank(idCard)) {
            log.warn("查询未读消息缺少身份证号");
            return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), "身份证号不能为空");
        }

        // 2. 获取用户信息
        UniqueUser user = getUserByIdCard(idCard);
        if (user == null) {
            log.warn("用户不存在，身份证号: {}", idCard);
            return UnifiedMessageResponse.success(Collections.emptyList(), null);
        }
        String userId = user.getId();
        String userDeptCode = user.getAgencyCode();
        if (StrUtil.isBlank(userDeptCode)) {
            userDeptCode = user.getUniqueOrgCode();
        }

        // 3. 个人未读消息
        List<UnifiedMessageDetail> inboxUnread = queryUnreadFromInboxForUser(userId);

        // 4. 广播未读消息（部门消息 + 自定义消息）
        List<UnifiedMessageDetail> broadcastUnread = queryUnreadFromBroadcastForUser(userId, userDeptCode);

        // 5. 合并并按时间倒序，限制数量
        List<UnifiedMessageDetail> allUnread = new ArrayList<>();
        allUnread.addAll(inboxUnread);
        allUnread.addAll(broadcastUnread);
        allUnread.sort(Comparator.comparing(UnifiedMessageDetail::getDistributeTime).reversed());

        if (allUnread.size() > CommonConstants.PAGE_LIST_QUERY_LIMIT) {
            allUnread = allUnread.subList(0, CommonConstants.PAGE_LIST_QUERY_LIMIT);
        }

        return UnifiedMessageResponse.success(allUnread, null);
    }

    /**
     * 根据身份证号获取用户信息
     */
    private UniqueUser getUserByIdCard(String idCard) {
        UniqueUser user = new UniqueUser();
        user.setIdCard(idCard);
        return remoteUniqueUserService.info(user);
    }

    /**
     * 查询收件箱中指定用户的未读个人消息
     */
    private List<UnifiedMessageDetail> queryUnreadFromInboxForUser(String userId) {
        LambdaQueryWrapper<UmpMsgInbox> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UmpMsgInbox::getReceiverId, userId)
               .eq(UmpMsgInbox::getReceiverType, MqMessageEventConstants.ReceiverTypes.USER)
               .eq(UmpMsgInbox::getReadStatus, MessageReadStatus.UNREAD)
               .orderByDesc(UmpMsgInbox::getCreateTime)
               .last("LIMIT " + CommonConstants.PAGE_LIST_QUERY_LIMIT);
        List<UmpMsgInbox> inboxList = umpMsgInboxService.list(wrapper);
        if (CollectionUtils.isEmpty(inboxList)) {
            return Collections.emptyList();
        }

        List<String> msgIds = inboxList.stream().map(UmpMsgInbox::getMsgId).collect(Collectors.toList());
        List<UmpMsgMain> msgList = lambdaQuery().in(UmpMsgMain::getId, msgIds)
                                                 .eq(UmpMsgMain::getDelFlag, 0)
                                                 .list();
        return msgList.stream().map(this::convertToUnifiedDetail).collect(Collectors.toList());
    }

    /**
     * 查询指定用户的未读广播消息（部门 + 自定义）
     */
    private List<UnifiedMessageDetail> queryUnreadFromBroadcastForUser(String userId, String userDeptCode) {
        List<UmpMsgBroadcast> candidateBroadcasts = new ArrayList<>();

        // 1. 部门消息
        if (StrUtil.isNotBlank(userDeptCode)) {
            LambdaQueryWrapper<UmpMsgBroadcast> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(UmpMsgBroadcast::getBroadcastType, MqMessageEventConstants.ReceiverTypes.DEPT)
                       .eq(UmpMsgBroadcast::getReceivingUnitCode, userDeptCode)
                       .eq(UmpMsgBroadcast::getDelFlag, 0)
                       .orderByDesc(UmpMsgBroadcast::getCreateTime);
            candidateBroadcasts.addAll(umpMsgBroadcastService.list(deptWrapper));
        }

        // 2. 自定义消息（筛选包含该用户或部门的）
        LambdaQueryWrapper<UmpMsgBroadcast> customWrapper = new LambdaQueryWrapper<>();
        customWrapper.eq(UmpMsgBroadcast::getBroadcastType, MqMessageEventConstants.ReceiverTypes.CUSTOM)
                     .eq(UmpMsgBroadcast::getDelFlag, 0)
                     .orderByDesc(UmpMsgBroadcast::getCreateTime);
        List<UmpMsgBroadcast> customList = umpMsgBroadcastService.list(customWrapper);
        if (!CollectionUtils.isEmpty(customList)) {
            List<UmpMsgBroadcast> filtered = customList.stream()
                    .filter(b -> isUserInReceivingScope(b.getReceivingScope(), userId, userDeptCode))
                    .collect(Collectors.toList());
            candidateBroadcasts.addAll(filtered);
        }

        if (CollectionUtils.isEmpty(candidateBroadcasts)) {
            return Collections.emptyList();
        }

        // 3. 获取该用户已上报的广播ID（有记录即视为已处理）
        List<String> candidateIds = candidateBroadcasts.stream().map(UmpMsgBroadcast::getId).collect(Collectors.toList());
        LambdaQueryWrapper<UmpBroadcastReceiveRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.in(UmpBroadcastReceiveRecord::getBroadcastId, candidateIds)
                     .eq(UmpBroadcastReceiveRecord::getReceiverId, userId)
                     .eq(UmpBroadcastReceiveRecord::getReceiverType, MqMessageEventConstants.ReceiverTypes.USER);
        List<UmpBroadcastReceiveRecord> reportedRecords = umpBroadcastReceiveRecordService.list(recordWrapper);
        Set<String> reportedIds = reportedRecords.stream().map(UmpBroadcastReceiveRecord::getBroadcastId).collect(Collectors.toSet());

        // 4. 未读广播 = 候选广播 - 已上报
        List<UmpMsgBroadcast> unreadBroadcasts = candidateBroadcasts.stream()
                .filter(b -> !reportedIds.contains(b.getId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(unreadBroadcasts)) {
            return Collections.emptyList();
        }

        // 5. 关联消息主表
        List<String> msgIds = unreadBroadcasts.stream().map(UmpMsgBroadcast::getMsgId).collect(Collectors.toList());
        List<UmpMsgMain> msgList = lambdaQuery().in(UmpMsgMain::getId, msgIds)
                                                 .eq(UmpMsgMain::getDelFlag, 0)
                                                 .list();
        Map<String, UmpMsgMain> msgMap = msgList.stream().collect(Collectors.toMap(UmpMsgMain::getId, Function.identity()));

        // 6. 转换为 UnifiedMessageDetail
        List<UnifiedMessageDetail> result = new ArrayList<>();
        for (UmpMsgBroadcast broadcast : unreadBroadcasts) {
            UmpMsgMain msg = msgMap.get(broadcast.getMsgId());
            if (msg == null) continue;

            UnifiedMessageDetail detail = convertToUnifiedDetail(msg);
            // 覆盖接收者信息（广播消息按部门显示）
            detail.setReceiverIdNumber(null);
            detail.setReceiverName(null);
            detail.setReceivingUnitCode(broadcast.getReceivingUnitCode());
            detail.setReceivingUnitName(broadcast.getReceivingUnitName());
            result.add(detail);
        }
        return result;
    }

    /**
     * 判断用户是否在自定义接收范围内
     */
    private boolean isUserInReceivingScope(ReceivingScope scope, String userId, String userDeptCode) {
        if (scope == null || scope.getInclude() == null) {
            return false;
        }
        List<String> loginIds = scope.getInclude().getLoginIds();
        List<String> deptIds = scope.getInclude().getDeptIds();
        return (loginIds != null && loginIds.contains(userId)) ||
               (deptIds != null && deptIds.contains(userDeptCode));
    }
    @Override
    public UnifiedMessageResponse<UnifiedMessageDetail> getUnreceivedMessagesByCursor(String appKey, UnifiedMessagePollRequest request) {
        String cursorKey = appKey;

        // 2. 获取或创建游标记录
        UmpPollCursor cursor = umpPollCursorService.getOrCreateCursor(appKey, cursorKey, null);

        // 3. 从请求中获取游标ID，若无则使用数据库中保存的游标（或为空）
        String requestCursor = request.getCursorId();
        if (StrUtil.isBlank(requestCursor)) {
            requestCursor = cursor.getCursorId();
        }

        // 4. 获取发送目标类型（fsdx）
        String sendTargetType = request.getSendTargetType();
        if (StrUtil.isBlank(sendTargetType)) {
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "发送对象类型不能为空");
        }

        // 5. 获取用户信息（用于部门/自定义消息匹配）
        String userId = null;
        String userDeptCode = null;
        String receiverIdNumber = request.getReceiverIdNumber();
        String receivingUnitCode = request.getReceivingUnitCode();

        if (StrUtil.isNotBlank(receiverIdNumber)) {
            // 通过身份证号获取用户信息
            UniqueUser user = getUserByIdCard(receiverIdNumber);
            if (user != null) {
                userId = user.getId();
                userDeptCode = user.getAgencyCode();
                if (StrUtil.isBlank(userDeptCode)) {
                    userDeptCode = user.getUniqueOrgCode();
                }
            }
        } else if (StrUtil.isNotBlank(receivingUnitCode)) {
            // 通过部门代码获取部门ID（用于后续匹配）
            userDeptCode = receivingUnitCode;
        }

        List<UnifiedMessageDetail> messages = null;
        Integer limit = CommonConstants.PAGE_LIST_QUERY_LIMIT;

        if (MqMessageEventConstants.ReceiverTypes.USER.equals(sendTargetType)) {
            // 个人消息：查询收件箱中未读的消息（可视为未接收）
            MessageRecipient recipient = ReceiverUtil.buildRecipient(sendTargetType, receiverIdNumber,
                    remoteUniqueUserService, remoteUniqueDeptService);
            messages = queryUnreceivedFromInbox(appKey, requestCursor, sendTargetType, recipient, limit);
        } else if (MqMessageEventConstants.ReceiverTypes.DEPT.equals(sendTargetType)
                || MqMessageEventConstants.ReceiverTypes.CUSTOM.equals(sendTargetType)) {
            // 部门/自定义消息：查询未被业务系统接收的广播
            messages = queryUnreceivedFromBroadcast(appKey, requestCursor, sendTargetType, userId, userDeptCode, limit);
        } else {
            log.warn("不支持的接收者类型: {}", sendTargetType);
            messages = Collections.emptyList();
        }

        // 生成新的游标ID
        String newCursor = "";
        if (!messages.isEmpty()) {
            UnifiedMessageDetail last = messages.get(messages.size() - 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            long timestamp = LocalDateTime.parse(last.getDistributeTime(), formatter)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli();
            newCursor = timestamp + "," + last.getMessageId();
        }

        // 记录轮询成功
        umpPollCursorService.recordPollSuccess(appKey, cursorKey, newCursor, messages.size());

        return UnifiedMessageResponse.success(messages, newCursor);
    }
    
    /**
     * 查询收件箱中未接收的个人消息（保持原逻辑）
     */
    private List<UnifiedMessageDetail> queryUnreceivedFromInbox(String appKey, String cursor, String receiverType,
                                                                MessageRecipient recipient, Integer limit) {
        // 解析游标
        LocalDateTime cursorTime = null;
        String cursorId = null;
        if (StrUtil.isNotBlank(cursor)) {
            String[] parts = cursor.split(",", 2);
            if (parts.length == 2) {
                try {
                    long timestamp = Long.parseLong(parts[0]);
                    cursorTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
                    cursorId = parts[1];
                } catch (Exception e) {
                    log.warn("游标格式错误: {}", cursor);
                }
            }
        }

        final LocalDateTime finalCursorTime = cursorTime;
        final String finalCursorId = cursorId;

        // 构建查询条件（未读即未接收）
        LambdaQueryWrapper<UmpMsgInbox> wrapper = new LambdaQueryWrapper<UmpMsgInbox>()
                .eq(UmpMsgInbox::getReceiverId, recipient.getReceiverId())
                .eq(UmpMsgInbox::getReceiverType, receiverType)
                .eq(UmpMsgInbox::getReadStatus, MessageReadStatus.UNREAD);

        if (finalCursorTime != null && finalCursorId != null) {
            wrapper.and(w -> w.gt(UmpMsgInbox::getDistributeTime, finalCursorTime)
                    .or(w2 -> w2.eq(UmpMsgInbox::getDistributeTime, finalCursorTime)
                            .gt(UmpMsgInbox::getMsgId, finalCursorId)));
        }

        wrapper.orderByAsc(UmpMsgInbox::getDistributeTime)
                .orderByAsc(UmpMsgInbox::getMsgId)
                .last("LIMIT " + limit);

        List<UmpMsgInbox> inboxList = umpMsgInboxService.list(wrapper);
        if (CollectionUtils.isEmpty(inboxList)) {
            return Collections.emptyList();
        }

        List<String> msgIds = inboxList.stream().map(UmpMsgInbox::getMsgId).collect(Collectors.toList());
        List<UmpMsgMain> msgList = lambdaQuery().in(UmpMsgMain::getId, msgIds)
                .eq(UmpMsgMain::getDelFlag, 0)
                .list();

        return msgList.stream().map(this::convertToUnifiedDetail).collect(Collectors.toList());
    }

    /**
     * 查询未被业务系统接收的广播消息（部门 + 自定义）
     */
    private List<UnifiedMessageDetail> queryUnreceivedFromBroadcast(String appKey, String cursor, String receiverType,
                                                                     String userId, String userDeptCode, Integer limit) {
        // 1. 解析游标
        LocalDateTime cursorTime = null;
        String cursorId = null;
        if (StrUtil.isNotBlank(cursor)) {
            String[] parts = cursor.split(",", 2);
            if (parts.length == 2) {
                try {
                    long timestamp = Long.parseLong(parts[0]);
                    cursorTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
                    cursorId = parts[1];
                } catch (Exception e) {
                    log.warn("游标格式错误: {}", cursor);
                }
            }
        }

        final LocalDateTime finalCursorTime = cursorTime;
        final String finalCursorId = cursorId;

        // 2. 获取候选广播
        List<UmpMsgBroadcast> candidateBroadcasts = new ArrayList<>();

        // 2.1 部门消息（接收类型为 DEPT，且部门代码匹配）
        if (StrUtil.isNotBlank(userDeptCode)) {
            LambdaQueryWrapper<UmpMsgBroadcast> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(UmpMsgBroadcast::getBroadcastType, MqMessageEventConstants.ReceiverTypes.DEPT)
                    .eq(UmpMsgBroadcast::getReceivingUnitCode, userDeptCode)
                    .eq(UmpMsgBroadcast::getDelFlag, 0);

            if (finalCursorTime != null && finalCursorId != null) {
                deptWrapper.and(w -> w.gt(UmpMsgBroadcast::getCreateTime, finalCursorTime)
                        .or(w2 -> w2.eq(UmpMsgBroadcast::getCreateTime, finalCursorTime)
                                .gt(UmpMsgBroadcast::getId, finalCursorId)));
            }

            deptWrapper.orderByAsc(UmpMsgBroadcast::getCreateTime)
                    .orderByAsc(UmpMsgBroadcast::getId)
                    .last("LIMIT " + limit);
            candidateBroadcasts.addAll(umpMsgBroadcastService.list(deptWrapper));
        }

        // 2.2 自定义消息（接收类型为 CUSTOM，且用户或部门在 receiving_scope 内）
        LambdaQueryWrapper<UmpMsgBroadcast> customWrapper = new LambdaQueryWrapper<>();
        customWrapper.eq(UmpMsgBroadcast::getBroadcastType, MqMessageEventConstants.ReceiverTypes.CUSTOM)
                .eq(UmpMsgBroadcast::getDelFlag, 0);

        if (finalCursorTime != null && finalCursorId != null) {
            customWrapper.and(w -> w.gt(UmpMsgBroadcast::getCreateTime, finalCursorTime)
                    .or(w2 -> w2.eq(UmpMsgBroadcast::getCreateTime, finalCursorTime)
                            .gt(UmpMsgBroadcast::getId, finalCursorId)));
        }

        customWrapper.orderByAsc(UmpMsgBroadcast::getCreateTime)
                .orderByAsc(UmpMsgBroadcast::getId)
                .last("LIMIT " + limit);
        List<UmpMsgBroadcast> customList = umpMsgBroadcastService.list(customWrapper);
        if (!CollectionUtils.isEmpty(customList)) {
            List<UmpMsgBroadcast> filteredCustom = customList.stream()
                    .filter(b -> isUserInReceivingScope(b.getReceivingScope(), userId, userDeptCode))
                    .collect(Collectors.toList());
            candidateBroadcasts.addAll(filteredCustom);
        }

        if (CollectionUtils.isEmpty(candidateBroadcasts)) {
            return Collections.emptyList();
        }

        // 3. 排除已上报接收的广播（即有接收记录）
        List<String> candidateIds = candidateBroadcasts.stream().map(UmpMsgBroadcast::getId).collect(Collectors.toList());
        LambdaQueryWrapper<UmpBroadcastReceiveRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.in(UmpBroadcastReceiveRecord::getBroadcastId, candidateIds)
                .eq(UmpBroadcastReceiveRecord::getReceiverId, userId)
                .eq(UmpBroadcastReceiveRecord::getReceiverType, MqMessageEventConstants.ReceiverTypes.USER);
        List<UmpBroadcastReceiveRecord> reportedRecords = umpBroadcastReceiveRecordService.list(recordWrapper);
        Set<String> reportedIds = reportedRecords.stream()
                .map(UmpBroadcastReceiveRecord::getBroadcastId)
                .collect(Collectors.toSet());

        List<UmpMsgBroadcast> unreceivedBroadcasts = candidateBroadcasts.stream()
                .filter(b -> !reportedIds.contains(b.getId()))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(unreceivedBroadcasts)) {
            return Collections.emptyList();
        }

        // 4. 关联消息主表
        List<String> msgIds = unreceivedBroadcasts.stream().map(UmpMsgBroadcast::getMsgId).collect(Collectors.toList());
        List<UmpMsgMain> msgList = lambdaQuery().in(UmpMsgMain::getId, msgIds)
                .eq(UmpMsgMain::getDelFlag, 0)
                .list();
        Map<String, UmpMsgMain> msgMap = msgList.stream()
                .collect(Collectors.toMap(UmpMsgMain::getId, Function.identity()));

        // 5. 转换为 UnifiedMessageDetail
        List<UnifiedMessageDetail> result = new ArrayList<>();
        for (UmpMsgBroadcast broadcast : unreceivedBroadcasts) {
            UmpMsgMain msg = msgMap.get(broadcast.getMsgId());
            if (msg == null) continue;

            UnifiedMessageDetail detail = convertToUnifiedDetail(msg);
            // 覆盖接收者信息（广播消息按部门显示）
            detail.setReceiverIdNumber(null);
            detail.setReceiverName(null);
            detail.setReceivingUnitCode(broadcast.getReceivingUnitCode());
            detail.setReceivingUnitName(broadcast.getReceivingUnitName());
            result.add(detail);
        }
        return result;
    }
    
    @Override
    @Transactional
    public void reportBizReceived(String messageId, String receiverId, String receiverType, String broadcastId, String appKey) {
        MessageReceivingUnitDTO unit = null;
        MessageRecipientDTO recipient = null;
        MessageReceiver receiver = null;
        if (StrUtil.isNotBlank(broadcastId)) {
            // 广播消息：先确保接收记录存在，再更新接收状态
            umpBroadcastReceiveRecordService.upsertReceiveRecord(broadcastId, receiverId, receiverType);
            umpBroadcastReceiveRecordService.markAsDelivered(broadcastId, receiverId, receiverType);
            
            List<String> appKeys = new ArrayList<>();
            appKeys.add(appKey);
            BroadcastDetailVO detail = umpMsgBroadcastService.getBroadcastByMsgId(broadcastId);
            if (BeanUtil.isNotEmpty(detail)) {
                receiver = ReceiverUtil.resolveReceivers(receiverType, detail.getReceivingScope(), remoteUniqueUserService, appKeys);
            }
        } else {
            // 收件箱消息
            InboxDetailVO inbox = umpMsgInboxService.getByMsgAndReceiver(messageId, receiverId, receiverType);
            umpMsgInboxService.markAsReceived(inbox.getId());
            unit = ReceiverUtil.buildReceivingUnit(receiverType, inbox.getReceivingUnitCode(), remoteUniqueUserService, remoteUniqueDeptService);
            recipient = ReceiverUtil.buildRecipient(receiverType, inbox.getReceiverIdNumber(), remoteUniqueUserService, remoteUniqueDeptService);
        }
        
        // 更新主表状态
        UmpMsgMain umpMsgMain = this.getById(messageId);
        String oldStatus = umpMsgMain.getStatus();
        umpMsgMain.setStatus(MqMessageEventConstants.EventTypes.BIZ_RECEIVED);
        String taskId = null;
        MqMessage<TaskData> message = ContentUtil.buildMessage(recipient, unit, receiver, umpMsgMain, oldStatus, taskId,
                umpMsgInboxMapper, umpMsgBroadcastMapper, umpMsgQueueMapper, umpBroadcastReceiveRecordMapper,
                umpTopicSubscriptionMapper, remoteUniqueUserService, remoteUniqueDeptService);
        updateMessageStatus(MqMessageEventConstants.EventTypes.BIZ_RECEIVED, message);
    }

    @Override
    @Transactional
    public void reportBizPulled(String messageId, String receiverId, String receiverType, String broadcastId, String appKey) {
        MessageReceivingUnitDTO unit = null;
        MessageRecipientDTO recipient = null;
        MessageReceiver receiver = null;
        if (StrUtil.isNotBlank(broadcastId)) {
            // 广播消息：先确保接收记录存在，再更新接收状态
            umpBroadcastReceiveRecordService.upsertReceiveRecord(broadcastId, receiverId, receiverType);
            umpBroadcastReceiveRecordService.markAsDelivered(broadcastId, receiverId, receiverType);
            
            List<String> appKeys = new ArrayList<>();
            appKeys.add(appKey);
            BroadcastDetailVO detail = umpMsgBroadcastService.getBroadcastByMsgId(broadcastId);
            if (BeanUtil.isNotEmpty(detail)) {
                receiver = ReceiverUtil.resolveReceivers(receiverType, detail.getReceivingScope(), remoteUniqueUserService, appKeys);
            }
        } else {
            // 收件箱消息
            InboxDetailVO inbox = umpMsgInboxService.getByMsgAndReceiver(messageId, receiverId, receiverType);
            umpMsgInboxService.markAsReceived(inbox.getId());
            unit = ReceiverUtil.buildReceivingUnit(receiverType, inbox.getReceivingUnitCode(), remoteUniqueUserService, remoteUniqueDeptService);
            recipient = ReceiverUtil.buildRecipient(receiverType, inbox.getReceiverIdNumber(), remoteUniqueUserService, remoteUniqueDeptService);
        }
        
        UmpMsgMain umpMsgMain = this.getById(messageId);
        String oldStatus = umpMsgMain.getStatus();
        umpMsgMain.setStatus(MqMessageEventConstants.EventTypes.BIZ_PULLED);
        String taskId = null;
        MqMessage<TaskData> message = ContentUtil.buildMessage(recipient, unit, receiver, umpMsgMain, oldStatus, taskId,
                umpMsgInboxMapper, umpMsgBroadcastMapper, umpMsgQueueMapper, umpBroadcastReceiveRecordMapper,
                umpTopicSubscriptionMapper, remoteUniqueUserService, remoteUniqueDeptService);
        updateMessageStatus(MqMessageEventConstants.EventTypes.BIZ_PULLED, message);
    }

    @Override
    @Transactional
    public void reportBizRead(String messageId, String receiverId, String receiverType, String broadcastId, String appKey) {
        MessageReceivingUnitDTO unit = null;
        MessageRecipientDTO recipient = null;
        MessageReceiver receiver = null;
        if (StrUtil.isNotBlank(broadcastId)) {
            // 广播消息：先确保接收记录存在，再更新阅读状态
            umpBroadcastReceiveRecordService.upsertReceiveRecord(broadcastId, receiverId, receiverType);
            umpBroadcastReceiveRecordService.markAsDelivered(broadcastId, receiverId, receiverType);
            umpBroadcastReceiveRecordService.markAsRead(broadcastId, receiverId, receiverType);
            umpMsgBroadcastService.incrementReadCount(broadcastId);
            
            List<String> appKeys = new ArrayList<>();
            appKeys.add(appKey);
            BroadcastDetailVO detail = umpMsgBroadcastService.getBroadcastByMsgId(broadcastId);
            if (BeanUtil.isNotEmpty(detail)) {
                receiver = ReceiverUtil.resolveReceivers(receiverType, detail.getReceivingScope(), remoteUniqueUserService, appKeys);
            }
        } else {
            // 收件箱消息
            InboxDetailVO inbox = umpMsgInboxService.getByMsgAndReceiver(messageId, receiverId, receiverType);
            umpMsgInboxService.markAsRead(inbox.getId());
            unit = ReceiverUtil.buildReceivingUnit(receiverType, inbox.getReceivingUnitCode(), remoteUniqueUserService, remoteUniqueDeptService);
            recipient = ReceiverUtil.buildRecipient(receiverType, inbox.getReceiverIdNumber(), remoteUniqueUserService, remoteUniqueDeptService);
        }
        
        UmpMsgMain umpMsgMain = this.getById(messageId);
        String oldStatus = umpMsgMain.getStatus();
        umpMsgMain.setStatus(MqMessageEventConstants.EventTypes.READ);
        String taskId = null;
        MqMessage<TaskData> message = ContentUtil.buildMessage(recipient, unit, receiver, umpMsgMain, oldStatus, taskId,
                umpMsgInboxMapper, umpMsgBroadcastMapper, umpMsgQueueMapper, umpBroadcastReceiveRecordMapper,
                umpTopicSubscriptionMapper, remoteUniqueUserService, remoteUniqueDeptService);
        updateMessageStatus(MqMessageEventConstants.EventTypes.READ, message);
    }

	@Override
	public MsgCodingVO generateMessageCode(@Valid MsgCodingDTO request, String appKey) {
	    try {
	        // 1. 生成唯一消息编码：UM+时间戳+请求签名
	    	String messageCode = CodeGeneratorUtil.UnifiedMessageCodeGenerator(request, appKey, 32);
	        
	        // 2. 生成条形码和二维码图片（base64编码）
	        String barcodeBase64 = CodeGeneratorUtil.BarcodeGenerator(messageCode);
	        String qrcodeBase64 = CodeGeneratorUtil.QrcodeGenerator(messageCode);
	        
	        // 3. 构建返回VO
	        return MsgCodingVO.builder()
	                .messageCode(messageCode)
	                .txm(barcodeBase64)
	                .ewm(qrcodeBase64)
	                .build();
	    } catch (IOException e) {
	        log.error("生成条形码失败", e);
	        throw new BusinessException("生成消息编码图片失败", e);
	    }
	}

	@Override
	public List<UmpMsgMain> getExpiredMessages(LocalDateTime expiredTime) {
		return baseMapper.selectExpiredMessages(expiredTime);
	}
	
	// 定义一个内部事件类
	@Data
	@AllArgsConstructor
	public class MessageStatusChangedEvent {
		/**
		 * 接收单位（单个）
		 */
		private MessageReceivingUnit unit;
		/**
		 * 接收个人（单个）
		 */
		private MessageRecipient recipient;
		/**
		 * 接收人（多个）
		 */
		private MessageReceiver receiver;
		/**
		 * 消息主体
		 */
	    private UmpMsgMain message;
	    /**
	     * 旧状态
	     */
	    private String oldStatus;
	    /**
	     * 任务ID
	     */
	    private String taskId;
	}
}