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
package ltd.huntinginfo.feng.center.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.dto.AppKeyAuthRequest;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MsgCodingDTO;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessagePollRequest;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessageReportRequest;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessageUnreadRequest;
import ltd.huntinginfo.feng.center.api.json.MessageContent;
import ltd.huntinginfo.feng.center.api.json.MessageContentHeader;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageReceiveRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageReceiveResponse;
import ltd.huntinginfo.feng.center.api.strategy.MessageRecord;
import ltd.huntinginfo.feng.center.api.strategy.MessageSendRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageSendResponse;
import ltd.huntinginfo.feng.center.api.strategy.MessageStatusUpdateRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageStatusUpdateResponse;
import ltd.huntinginfo.feng.center.api.strategy.UnreadMessageRequest;
import ltd.huntinginfo.feng.center.api.strategy.UnreadMessageRequest.QueryPersonInfo;
import ltd.huntinginfo.feng.center.api.strategy.UnreadMessageRequest.QueryUnitInfo;
import ltd.huntinginfo.feng.center.api.strategy.UnreadMessageResponse;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyRequest.ApplyPersonInfo;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyRequest.ApplyUnitInfo;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyResponse;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.api.vo.AppKeyAuthResponse;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgCodingVO;
import ltd.huntinginfo.feng.center.api.vo.ServerTimestamp;
import ltd.huntinginfo.feng.center.config.MinistryMessageCenterProperties;
import ltd.huntinginfo.feng.center.service.AppAuthService;
import ltd.huntinginfo.feng.center.service.UmpAppCredentialService;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.center.strategy.MessageCenterClientService;
import ltd.huntinginfo.feng.center.utils.ReceiverUtil;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.UnifiedMessageData;
import ltd.huntinginfo.feng.common.core.util.UnifiedMessageResponse;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.service.FengUser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开放式API控制器
 * 供外部业务系统调用
 */
@Slf4j
@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Tag(name = "开放式API", description = "供业务系统调用的消息接口")
public class UnifiedMessageController {

    private final UmpMsgMainService umpMsgMainService;
    private final AppAuthService appAuthService;
    private final UmpAppCredentialService umpAppCredentialService;
    private final MinistryMessageCenterProperties ministryMessageCenterProperties;
    private final MessageCenterClientService messageCenterClientService;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final RemoteUniqueUserService remoteUniqueUserService;
    private final RemoteUniqueDeptService remoteUniqueDeptService;

    /**
     * 发送消息
     */
    @PostMapping("/message/send")
    @Operation(summary = "发送消息")
    public UnifiedMessageResponse<?> sendMessage(@Valid @RequestBody MessageSendDTO sendDTO,
            Authentication authentication) {
        log.debug("sendMessage sendDTO: {}", sendDTO);
        
        String appKey = extractAppKey(authentication);
        if (StrUtil.isBlank(sendDTO.getSenderAppKey())) sendDTO.setSenderAppKey(appKey);
        
        if (BeanUtil.isEmpty(sendDTO.getMessageContent()) && StrUtil.isBlank(sendDTO.getMessageContentString())) {
            return UnifiedMessageResponse.fail(
                BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), 
                "内容不能为空"
            );
        }
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return sendMessageToMinistry(sendDTO, appKey);
        }
        
        // 本地消息中心模式
        return sendMessageLocal(sendDTO, appKey);
    }
    
    private String extractAppKey(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(BusinessEnum.WEB_UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            return ((Jwt) principal).getSubject();
        } else if (principal instanceof FengUser) {
            return ((FengUser) principal).getUsername();
        } else if (principal instanceof DefaultOAuth2AuthenticatedPrincipal) {
            return ((DefaultOAuth2AuthenticatedPrincipal) principal).getName();
        } else if (principal instanceof OAuth2AuthenticatedPrincipal) {
            return ((OAuth2AuthenticatedPrincipal) principal).getName();
        }

        return authentication.getName();
    }
    
    /**
     * 发送消息到部级消息中心
     */
    private UnifiedMessageResponse<?> sendMessageToMinistry(MessageSendDTO sendDTO, String appKey) {
        MessageSendRequest request = buildMessageSendRequest(sendDTO);
        List<MessageSendRequest> requests = Collections.singletonList(request);
        
        MessageSendResponse response = messageCenterClientService.sendMessages(appKey, requests);
        
        return buildUnifiedResponse(response);
    }
    
    /**
     * 发送消息到本地消息中心
     */
    private UnifiedMessageResponse<?> sendMessageLocal(MessageSendDTO sendDTO, String appKey) {
        AppDetailVO app = umpAppCredentialService.getAppByKey(appKey);
        if (app == null) {
            return UnifiedMessageResponse.fail(
                BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), 
                "AppKey不正确"
            );
        }
        
        String messageId = umpMsgMainService.createMessage(sendDTO);
        return UnifiedMessageResponse.success(Collections.singletonList(messageId), "");
    }
    
    /**
     * 构建消息发送请求
     */
    private MessageSendRequest buildMessageSendRequest(MessageSendDTO sendDTO) {
        MessageSendRequest request = new MessageSendRequest();
        request.setCldz(sendDTO.getProcessUrl());
        request.setFsdw(sendDTO.getSendUnitName());
        request.setFsdwdm(sendDTO.getSendUnitCode());
        request.setFsdx(convertSendTargetType(sendDTO.getSendTargetType()));
        request.setFsr(sendDTO.getSenderName());
        request.setFsrzjhm(sendDTO.getSenderIdNumber());
        request.setJjcd(sendDTO.getPriority());
        request.setJsdw(sendDTO.getReceivingUnitName());
        request.setJsdwdm(sendDTO.getReceivingUnitCode());
        request.setJsr(sendDTO.getReceiverName());
        request.setJsrzjhm(sendDTO.getReceiverIdNumber());
        request.setTb(sendDTO.getIcon());
        request.setToken(sendDTO.getToken());
        request.setXxbm(sendDTO.getMessageCode());
        request.setXxbt(sendDTO.getMessageTitle());
        //request.setXxlx(sendDTO.getMessageType());
        // 直接重置为部级消息中心分配的消息类型
        request.setXxlx(ministryMessageCenterProperties.getMsgType());
        
        MessageContent mc = sendDTO.getMessageContent();
        if (BeanUtil.isEmpty(mc)) {
        	mc = new MessageContent();
        	List<String> body = new ArrayList<>();
        	body.add(sendDTO.getMessageContentString());
        	mc.setBody(body);
        	MessageContentHeader mch = new MessageContentHeader();
        	mch.setTitle(sendDTO.getMessageTitle());
        }
        request.setXxnr(JSONUtil.toJsonStr(sendDTO.getMessageContent()));
        request.setYwcs(sendDTO.getBusinessParam());
        //request.setZtbm(sendDTO.getTopicCode());
        // 直接重置为部级消息中心分配的主题编码
        request.setZtbm(ministryMessageCenterProperties.getTopicCode());
        return request;
    }
    
    /**
     * 转换发送目标类型（部级消息中心格式）
     * 0-个人 1-单位 2-全体
     */
    private String convertSendTargetType(String type) {
        if (MqMessageEventConstants.ReceiverTypes.ALL.equals(type)) {
            return "2";
        } else if (MqMessageEventConstants.ReceiverTypes.DEPT.equals(type)) {
            return "1";
        }
        return "0";
    }
    
    /**
     * 拉取消息
     */
    @PostMapping("/message/poll")
    @Operation(summary = "拉取消息")
    public UnifiedMessageResponse<?> pollMessages(@Valid @RequestBody UnifiedMessagePollRequest request,
                                                  Authentication authentication) {
        String appKey = extractAppKey(authentication);
        if (StrUtil.isBlank(appKey)) {
            return UnifiedMessageResponse.fail(
                BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), 
                "应用标识不能为空"
            );
        }
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return pollMessagesFromMinistry(request, appKey);
        }
        
        // 本地消息中心模式
        return umpMsgMainService.getUnreceivedMessagesByCursor(appKey, request);
    }
    
    /**
     * 从部级消息中心拉取消息
     */
    private UnifiedMessageResponse<?> pollMessagesFromMinistry(UnifiedMessagePollRequest request, String appKey) {
        MessageReceiveRequest messageReceiveRequest = new MessageReceiveRequest();
        messageReceiveRequest.setToken(request.getToken());
        messageReceiveRequest.setYbid(request.getCursorId());
        //messageReceiveRequest.setZtbm(request.getTopicCode());
        // 直接重置为部级消息中心分配的主题编码
        messageReceiveRequest.setZtbm(ministryMessageCenterProperties.getTopicCode());
        
        MessageReceiveResponse response = messageCenterClientService.receiveMessages(appKey, messageReceiveRequest);
        return buildUnifiedResponse(response);
    }
    
    /**
     * 查询未读消息
     */
    @PostMapping("/message/unread")
    @Operation(summary = "查询未读消息")
    public UnifiedMessageResponse<?> getUnreadMessages(@Valid @RequestBody UnifiedMessageUnreadRequest request,
                                                  Authentication authentication) {
        String appKey = extractAppKey(authentication);
        if (StrUtil.isBlank(appKey)) {
            return UnifiedMessageResponse.fail(
                BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), 
                "应用标识不能为空"
            );
        }
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return getUnreadMessagesFromMinistry(request, appKey);
        }
        
        // 本地消息中心模式
        return umpMsgMainService.getUnreadMessagesByApp(appKey, request);
    }
    
    /**
     * 从部级消息中心查询未读消息
     */
    private UnifiedMessageResponse<?> getUnreadMessagesFromMinistry(UnifiedMessageUnreadRequest request, String appKey) {
        UnreadMessageRequest unreadMessageRequest = buildUnreadMessageRequest(request);
        UnreadMessageResponse response = messageCenterClientService.queryUnreadMessages(appKey, unreadMessageRequest);
        return buildUnifiedResponse(response);
    }
    
    /**
     * 构建未读消息查询请求
     */
    private UnreadMessageRequest buildUnreadMessageRequest(UnifiedMessageUnreadRequest request) {
        UnreadMessageRequest unreadMessageRequest = new UnreadMessageRequest();
        
        if (request.getCxdwxx() != null) {
            QueryUnitInfo queryUnitInfo = new QueryUnitInfo();
            queryUnitInfo.setCxdw(request.getCxdwxx().getCxdw());
            queryUnitInfo.setCxdwdm(request.getCxdwxx().getCxdwdm());
            unreadMessageRequest.setCxdwxx(queryUnitInfo);
        }
        
        if (request.getCxrxx() != null) {
            QueryPersonInfo queryPersonInfo = new QueryPersonInfo();
            queryPersonInfo.setCxr(request.getCxrxx().getCxr());
            queryPersonInfo.setCxrzjhm(request.getCxrxx().getCxrzjhm());
            unreadMessageRequest.setCxrxx(queryPersonInfo);
        }
        
        unreadMessageRequest.setToken(request.getToken());
        return unreadMessageRequest;
    }
    
    /**
     * 上报消息已接收
     */
    @PostMapping("/message/received")
    @Operation(summary = "上报消息已接收")
    public UnifiedMessageResponse<?> reportReceived(@Valid @RequestBody UnifiedMessageReportRequest request,
            Authentication authentication) {
        String appKey = extractAppKey(authentication);
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return reportReceivedToMinistry(request, appKey);
        }
        
        // 本地消息中心模式
        return reportReceivedLocal(request, appKey);
    }
    
    /**
     * 上报消息已接收（本地）
     */
    private UnifiedMessageResponse<?> reportReceivedLocal(UnifiedMessageReportRequest request, String appKey) {
        MessageRecipient recipient = ReceiverUtil.buildRecipient(
            MqMessageEventConstants.ReceiverTypes.USER, 
            request.getClrzjhm(),
            remoteUniqueUserService, 
            remoteUniqueDeptService
        );
        
        MessageDetailVO vo = umpMsgMainService.getMessageByCode(request.getXxbm());
        BroadcastDetailVO broadcastDetailVO = umpMsgBroadcastService.getBroadcastByMsgId(vo.getId());
        
        umpMsgMainService.reportBizReceived(
            vo.getId(), 
            recipient.getReceiverId(),
            recipient.getReceiverType(), 
            broadcastDetailVO.getId(), 
            appKey
        );
        
        return UnifiedMessageResponse.success(null, "");
    }
    
    /**
     * 上报消息已接收（部级）
     */
    private UnifiedMessageResponse<?> reportReceivedToMinistry(UnifiedMessageReportRequest request, String appKey) {
        MessageStatusUpdateRequest updateRequest = buildMessageStatusUpdateRequest(request);
        MessageStatusUpdateResponse response = messageCenterClientService.updateMessageStatus(appKey, updateRequest);
        return buildUnifiedResponse(response);
    }
    
    /**
     * 上报消息已拉取
     */
    @PostMapping("/message/pulled")
    @Operation(summary = "上报消息已拉取")
    public UnifiedMessageResponse<?> reportPulled(@Valid @RequestBody UnifiedMessageReportRequest request,
            Authentication authentication) {
        String appKey = extractAppKey(authentication);
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return reportPulledToMinistry(request, appKey);
        }
        
        // 本地消息中心模式
        return reportPulledLocal(request, appKey);
    }
    
    /**
     * 上报消息已拉取（本地）
     */
    private UnifiedMessageResponse<?> reportPulledLocal(UnifiedMessageReportRequest request, String appKey) {
        MessageRecipient recipient = ReceiverUtil.buildRecipient(
            MqMessageEventConstants.ReceiverTypes.USER, 
            request.getClrzjhm(),
            remoteUniqueUserService, 
            remoteUniqueDeptService
        );
        
        MessageDetailVO vo = umpMsgMainService.getMessageByCode(request.getXxbm());
        BroadcastDetailVO broadcastDetailVO = umpMsgBroadcastService.getBroadcastByMsgId(vo.getId());
        
        umpMsgMainService.reportBizPulled(
            vo.getId(), 
            recipient.getReceiverId(),
            recipient.getReceiverType(), 
            broadcastDetailVO.getId(), 
            appKey
        );
        
        return UnifiedMessageResponse.success(null, "");
    }
    
    /**
     * 上报消息已拉取（部级）
     */
    private UnifiedMessageResponse<?> reportPulledToMinistry(UnifiedMessageReportRequest request, String appKey) {
        MessageStatusUpdateRequest updateRequest = buildMessageStatusUpdateRequest(request);
        MessageStatusUpdateResponse response = messageCenterClientService.updateMessageStatus(appKey, updateRequest);
        return buildUnifiedResponse(response);
    }
    
    /**
     * 上报消息已阅读
     */
    @PostMapping("/message/read")
    @Operation(summary = "上报消息已阅读")
    public UnifiedMessageResponse<?> reportRead(@Valid @RequestBody UnifiedMessageReportRequest request,
            Authentication authentication) {
        String appKey = extractAppKey(authentication);
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return reportReadToMinistry(request, appKey);
        }
        
        // 本地消息中心模式
        return reportReadLocal(request, appKey);
    }
    
    /**
     * 上报消息已阅读（本地）
     */
    private UnifiedMessageResponse<?> reportReadLocal(UnifiedMessageReportRequest request, String appKey) {
        MessageRecipient recipient = ReceiverUtil.buildRecipient(
            MqMessageEventConstants.ReceiverTypes.USER, 
            request.getClrzjhm(),
            remoteUniqueUserService, 
            remoteUniqueDeptService
        );
        if (BeanUtil.isEmpty(recipient)) {
        	return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_MISSING);
        }
        MessageDetailVO vo = umpMsgMainService.getMessageByCode(request.getXxbm());
        if (BeanUtil.isEmpty(vo)) {
        	return UnifiedMessageResponse.fail(BusinessEnum.UMP_RECORD_NOT_EXISTED);
        }
        BroadcastDetailVO broadcastDetailVO = umpMsgBroadcastService.getBroadcastByMsgId(vo.getId());
        String broadcastId = null;
        if (broadcastDetailVO != null) {
        	broadcastId = broadcastDetailVO.getId();
        }        
        umpMsgMainService.reportBizRead(
            vo.getId(), 
            recipient.getReceiverId(),
            recipient.getReceiverType(), 
            broadcastId, 
            appKey
        );
        
        return UnifiedMessageResponse.success(null, "");
    }
    
    /**
     * 上报消息已阅读（部级）
     */
    private UnifiedMessageResponse<?> reportReadToMinistry(UnifiedMessageReportRequest request, String appKey) {
        MessageStatusUpdateRequest updateRequest = buildMessageStatusUpdateRequest(request);
        MessageStatusUpdateResponse response = messageCenterClientService.updateMessageStatus(appKey, updateRequest);
        return buildUnifiedResponse(response);
    }
    
    /**
     * 构建消息状态更新请求
     */
    private MessageStatusUpdateRequest buildMessageStatusUpdateRequest(UnifiedMessageReportRequest request) {
        MessageStatusUpdateRequest updateRequest = new MessageStatusUpdateRequest();
        BeanUtil.copyProperties(request, updateRequest);
        return updateRequest;
    }
    
    /**
     * 申请消息编码
     */
    @PostMapping("/applyCode")
    @Operation(summary = "申请消息编码")
    @SysLog("申请消息编码")
    public UnifiedMessageResponse<?> applyCode(@Valid @RequestBody MsgCodingDTO request,
            Authentication authentication) {
        String appKey = extractAppKey(authentication);
        
        // 部级消息中心模式
        if (ministryMessageCenterProperties.getEnabled()) {
            return applyCodeFromMinistry(request, appKey);
        }
        
        // 本地消息中心模式
        MsgCodingVO data = umpMsgMainService.generateMessageCode(request, appKey);
        return UnifiedMessageResponse.success(Collections.singletonList(data), "");
    }
    
    /**
     * 从部级消息中心申请消息编码
     */
    private UnifiedMessageResponse<?> applyCodeFromMinistry(MsgCodingDTO request, String appKey) {
        CodeApplyRequest codeApplyRequest = buildCodeApplyRequest(request);
        CodeApplyResponse response = messageCenterClientService.applyMessageCode(appKey, codeApplyRequest);
        
        // 构建返回结果
        UnifiedMessageResponse<Map<String, String>> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        
        UnifiedMessageData<Map<String, String>> data = new UnifiedMessageData<>();
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("jzptjcbm", response.getJzptjcbm());
        resultMap.put("ewm", response.getEwm());
        
        data.setXxjl(Collections.singletonList(resultMap));
        data.setCode(response.getCode());
        data.setInfo(response.getInfo());
        ret.setData(data);
        
        return ret;
    }
    
    /**
     * 构建编码申请请求
     */
    private CodeApplyRequest buildCodeApplyRequest(MsgCodingDTO request) {
        CodeApplyRequest codeApplyRequest = new CodeApplyRequest();
        codeApplyRequest.setSqssdm(request.getDivisionCode());
        
        ApplyUnitInfo sqdwxx = new ApplyUnitInfo();
        sqdwxx.setSqdwdm(request.getAgencyCode());
        sqdwxx.setSqdwmc(request.getAgencyName());
        codeApplyRequest.setSqdwxx(sqdwxx);
        
        ApplyPersonInfo sqrxx = new ApplyPersonInfo();
        sqrxx.setSqrdh(request.getApplicantPhone());
        sqrxx.setSqrxm(request.getApplicantName());
        sqrxx.setSqrzjhm(request.getApplicantIdCard());
        codeApplyRequest.setSqrxx(sqrxx);
        
        return codeApplyRequest;
    }
    
    /**
     * 构建统一响应（用于部级消息中心返回）
     */
    private UnifiedMessageResponse<?> buildUnifiedResponse(Object response) {
        if (response instanceof CodeApplyResponse) {
            return buildCodeApplyResponse((CodeApplyResponse) response);
        } else if (response instanceof MessageSendResponse) {
            return buildMessageSendResponse((MessageSendResponse) response);
        } else if (response instanceof MessageReceiveResponse) {
            return buildMessageReceiveResponse((MessageReceiveResponse) response);
        } else if (response instanceof MessageStatusUpdateResponse) {
            return buildMessageStatusUpdateResponse((MessageStatusUpdateResponse) response);
        } else if (response instanceof UnreadMessageResponse) {
            return buildUnreadMessageResponse((UnreadMessageResponse) response);
        }
        
        // 默认响应
        UnifiedMessageResponse<Object> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        return ret;
    }

    /**
     * 构建申请编码响应
     */
    private UnifiedMessageResponse<Map<String, String>> buildCodeApplyResponse(CodeApplyResponse response) {
        UnifiedMessageResponse<Map<String, String>> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        
        UnifiedMessageData<Map<String, String>> data = new UnifiedMessageData<>();
        data.setCode(response.getCode());
        data.setInfo(response.getInfo());
        
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("jzptjcbm", response.getJzptjcbm());
        resultMap.put("ewm", response.getEwm());
        data.setXxjl(Collections.singletonList(resultMap));
        
        ret.setData(data);
        return ret;
    }

    /**
     * 构建发送消息响应
     */
    private UnifiedMessageResponse<Object> buildMessageSendResponse(MessageSendResponse response) {
        UnifiedMessageResponse<Object> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        
        UnifiedMessageData<Object> data = new UnifiedMessageData<>();
        data.setCode(response.getCode());
        data.setInfo(response.getInfo());
        
        ret.setData(data);
        return ret;
    }

    /**
     * 构建接收消息响应
     */
    private UnifiedMessageResponse<MessageRecord> buildMessageReceiveResponse(MessageReceiveResponse response) {
        UnifiedMessageResponse<MessageRecord> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        
        UnifiedMessageData<MessageRecord> data = new UnifiedMessageData<>();
        data.setCode(response.getCode());
        data.setInfo(response.getInfo());
        data.setXxjl(response.getXxjl());
        data.setYbid(response.getYbid());
        
        ret.setData(data);
        return ret;
    }

    /**
     * 构建更新消息状态响应
     */
    private UnifiedMessageResponse<Object> buildMessageStatusUpdateResponse(MessageStatusUpdateResponse response) {
        UnifiedMessageResponse<Object> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        
        UnifiedMessageData<Object> data = new UnifiedMessageData<>();
        data.setCode(response.getCode());
        data.setInfo(response.getInfo());
        
        ret.setData(data);
        return ret;
    }

    /**
     * 构建查询未读消息响应
     */
    private UnifiedMessageResponse<MessageRecord> buildUnreadMessageResponse(UnreadMessageResponse response) {
        UnifiedMessageResponse<MessageRecord> ret = new UnifiedMessageResponse<>();
        ret.setStatus(BusinessEnum.WEB_OK.getCode());
        ret.setMessage(BusinessEnum.WEB_OK.getMsg());
        
        UnifiedMessageData<MessageRecord> data = new UnifiedMessageData<>();
        data.setCode(response.getCode());
        data.setInfo(response.getInfo());
        data.setXxjl(response.getXxjl());
        data.setXxzs(response.getXxzs());
        
        ret.setData(data);
        return ret;
    }
    
    /**
     * 应用认证
     * 此处和AppKeyAuthenticationProvider.authenticate生成令牌方式不一致
     * 因此，若要使用本方法认证应用系统，必须把开放式API全部开放，让OAUTH2不验证JWT令牌，而是在开放式API内部验证JWT令牌
     */
    @PostMapping("/app/authenticate")
    @Operation(summary = "应用认证", 
        description = "基于AppKey和AppSecret的应用认证服务，支持设备管理、执法系统等应用类型",
        responses = {
            @ApiResponse(responseCode = "200", description = "认证成功",
                content = @Content(schema = @Schema(implementation = AppAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "401", description = "认证失败")
        }
    )    
    @SysLog("应用认证")
    public UnifiedMessageResponse<?> authenticate(
            HttpServletRequest request, // 注入 HttpServletRequest
            @Validated @RequestBody AppKeyAuthRequest authRequest) throws IOException {

        // 校验 MD5 是否匹配
        if (StrUtil.isNotBlank(authRequest.getBodyMd5())) {
            // 直接从 request 参数中获取，并尝试转换为包装器
            // 注意：由于配置了过滤器，这里拿到的应该是经过包装的请求
            if (!(request instanceof ContentCachingRequestWrapper)) {
                 // 理论上不应该发生，但做一层防御
                 log.error("Request is not wrapped by ContentCachingRequestWrapper");
                 return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_LENGTH_ERROR.getCode().toString(), "请求处理异常");
            }
            
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            byte[] bodyBytes = wrapper.getContentAsByteArray();
            String computedMd5 = DigestUtil.md5Hex(bodyBytes);
            
	        if (!computedMd5.equals(authRequest.getBodyMd5())) {
	            log.warn("请求体 MD5 不匹配，appKey: {}, expected: {}, actual: {}",
	                    authRequest.getAppKey(), authRequest.getBodyMd5(), computedMd5);
	            return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_LENGTH_ERROR.getCode().toString(), "消息体已被篡改");
	        }
        }

        AppKeyAuthResponse response = appAuthService.authenticateByAppKey(authRequest);
        List<Object> records = new ArrayList<Object>();
        records.add(response);
        return response.isSuccess() ? UnifiedMessageResponse.success(records, "") : 
        	UnifiedMessageResponse.fail(BusinessEnum.UMP_SERVICE_ERROR_13000.getCode().toString(), response.getErrorMsg());
    }
    
    @PutMapping("/refreshSecret")
    @Operation(summary = "刷新应用密钥")
    @SysLog("刷新应用密钥")
    public UnifiedMessageResponse<?> refreshAppSecret(
            @Parameter(description = "应用标识", required = true)
            @RequestParam String appKey,
            Authentication authentication) {

    	String currentAppKey = extractAppKey(authentication);
        if (!currentAppKey.equals(appKey)) {
            return UnifiedMessageResponse.fail(
                    BusinessEnum.WEB_FORBIDDEN.getCode().toString(),
                    "无权操作其他应用的密钥");
        }

        String newSecret = appAuthService.refreshAppSecret(appKey);
        List<Object> records = new ArrayList<>();
        records.add(newSecret);
        return UnifiedMessageResponse.success(records, "");
    }
    
    @GetMapping("/getTimestamp")
    @Operation(summary = "获取服务器时间戳")
    @SysLog("获取服务器时间戳")
    @ApiResponse(responseCode = "200", description = "服务器时间",
            content = @Content(schema = @Schema(implementation = ServerTimestamp.class)))
    public UnifiedMessageResponse<?> getTimestamp() {
        ServerTimestamp sts = new ServerTimestamp();
        
        // 获取当前UTC时间戳（毫秒）
        long timestampInMillis = Instant.now().toEpochMilli();
        sts.setTimestamp(timestampInMillis);
        
        // 格式化UTC时间字符串
        Instant instant = Instant.ofEpochMilli(timestampInMillis);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.UTC);
        String formatted = formatter.format(instant);
        sts.setStrDate(formatted);
        
        List<Object> records = new ArrayList<>();
        records.add(sts);
        return UnifiedMessageResponse.success(records, "");
    }
    
    // Swagger响应模型定义
    private static class AppAuthResponse extends R<AppKeyAuthResponse> {
        private static final long serialVersionUID = 1L;
    }
}
