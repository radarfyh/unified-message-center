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
package ltd.huntinginfo.feng.center.service.state;

import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 消息状态机 - 集中管理 ump_msg_main.status 的所有合法变更
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageStateMachine {

    private final UmpMsgMainService msgMainService;

    // ========== 正向流转 ==========

    /** RECEIVED → DISTRIBUTING（开始分发） */
    @Transactional
    public void onDistributeStart(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
    	
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.DISTRIBUTING, message);
        log.debug("状态变更: 消息ID={} → DISTRIBUTING", msgId);
    }
    
    private String checkMessage(MqMessage<TaskData> message) {
    	if (BeanUtil.isEmpty(message)) {
    		log.error("onDistributeStart入参为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "onDistributeStart入参为空");
    	}
    	TaskData taskData = message.getPayload();
    	if (BeanUtil.isEmpty(taskData)) {
    		log.error("onDistributeStart入参携带数据为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "onDistributeStart入参携带数据为空");
    	}
    	
    	String msgId = message.getMessageId();
//    	if (StrUtil.isBlank(msgId)) {
//    		msgId = taskData.getMessageId();  
//    		message.setMessageId(msgId);
//    	}
    	
    	return msgId;
    }

    /** DISTRIBUTING → DISTRIBUTED（分发成功） */
    @Transactional
    public void onDistributed(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
    	
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.DISTRIBUTED, message);
        log.info("状态变更: 消息ID={} → DISTRIBUTED", msgId);
    }

    /** DISTRIBUTED → PUSHED（发起推送请求） */
    @Transactional
    public void onPushed(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.PUSHED, message);
        log.debug("状态变更: 消息ID={} → PUSHED", msgId);
    }

    /** DISTRIBUTED → PULL（转为待拉取） */
    @Transactional
    public void onPullReady(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.PULL, message);
        log.debug("状态变更: 消息ID={} → PULL", msgId);
    }

    /** PUSHED → BIZ_RECEIVED（业务系统确认接收） */
    @Transactional
    public void onBusinessReceived(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.BIZ_RECEIVED, message);

        log.info("状态变更: 消息ID={} → BIZ_RECEIVED", msgId);
    }

    /** PULL → BIZ_PULLED（业务系统拉取成功） */
    @Transactional
    public void onBusinessPulled(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.BIZ_PULLED, message);

        log.info("状态变更: 消息ID={} → BIZ_PULLED", msgId);
    }

    /** BIZ_RECEIVED / BIZ_PULLED → READ（用户已读） */
    @Transactional
    public void onRead(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.READ, message);

        log.info("状态变更: 消息ID={} → READ", msgId);
    }

    // ========== 失败状态（超过重试次数） ==========

    /** 分发失败超过重试次数 → DIST_FAILED */
    @Transactional
    public void onDistributeFailed(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.DIST_FAILED, message);

        log.warn("状态变更: 消息ID={} → DIST_FAILED（分发失败超限）", msgId);
    }

    /** 推送失败/业务确认失败超过重试次数 → PUSH_FAILED */
    @Transactional
    public void onPushFailed(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.PUSH_FAILED, message);

        log.warn("状态变更: 消息ID={} → PUSH_FAILED（推送失败超限）", msgId);
    }

    /** 拉取超时/过期 → PULL_FAILED */
    @Transactional
    public void onPullFailed(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.PULL_FAILED, message);

        log.warn("状态变更: 消息ID={} → PULL_FAILED（拉取超时）", msgId);
    }

    // ========== 重试回退（未超限） ==========

    /** 分发失败后重试 → DIST_RETRY */
    @Transactional
    public void onRetryDistribute(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.DIST_RETRY, message);
        log.info("状态变更: 消息ID={} → DIST_RETRY（分发重试）", msgId);
    }

    /** 推送失败/业务确认失败后重试 → PUSH_RETRY */
    @Transactional
    public void onRetryPush(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.PUSH_RETRY, message);
        log.info("状态变更: 消息ID={} → PUSH_RETRY（推送重试）", msgId);
    }
    
    // ========== 超时 ==========
    
    /** 超时 → EXPIRED */
    @Transactional
    public void onExpired(MqMessage<TaskData> message) {
    	String msgId = checkMessage(message);
        msgMainService.updateMessageStatus(MqMessageEventConstants.EventTypes.EXPIRED, message);
        log.info("状态变更: 消息ID={} → EXPIRED（超时）", msgId);
    }
}