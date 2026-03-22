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
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.json.CallbackConfig;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigResponse;
import ltd.huntinginfo.feng.center.api.json.MessageContent;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.UnifiedMessageDetail;
import ltd.huntinginfo.feng.center.service.*;
import ltd.huntinginfo.feng.center.service.state.MessageStateMachine;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.MessagePushStatus;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.util.SignatureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * 消息推送处理器
 * 负责将消息推送到业务系统的回调地址
 */
@Slf4j
@Service
public class MessagePushProcessor {

    private static final String WORKER_PREFIX = "push-processor-";
    private static final int DEFAULT_RETRY_TIMES = 3;
    private static final long RETRY_BASE_DELAY = 1000L; // 1秒

    /**
     * 回调请求头常量
     */
    public interface CallbackHeaders {
        String APP_KEY = "X-App-Key";
        String TIMESTAMP = "X-Timestamp";
        String NONCE = "X-Nonce";
        String SIGNATURE = "X-Signature";
        String BODY_MD5 = "X-Body-Md5";
        String CONTENT_TYPE = "Content-Type";
    }

    private final UmpMsgMainService umpMsgMainService;
    private final UmpMsgInboxService umpMsgInboxService;
    private final UmpAppCredentialService umpAppCredentialService;
    private final UmpMsgQueueService umpMsgQueueService;
    private final UmpTopicSubscriptionService umpTopicSubscriptionService;
    private final UmpMsgCallbackService umpMsgCallbackService;
    private final UmpMsgBroadcastService umpMsgBroadcastService;
    private final RestTemplate restTemplate;
    private final MessageStateMachine messageStateMachine;

    @Autowired
    public MessagePushProcessor(
            UmpMsgMainService umpMsgMainService,
            UmpMsgInboxService umpMsgInboxService,
            UmpAppCredentialService umpAppCredentialService,
            UmpMsgQueueService umpMsgQueueService,
            UmpTopicSubscriptionService umpTopicSubscriptionService,
            UmpMsgCallbackService umpMsgCallbackService,
            UmpMsgBroadcastService umpMsgBroadcastService,
            UmpBroadcastReceiveRecordService umpBroadcastReceiveRecordService,
            RemoteUniqueUserService remoteUniqueUserService,
            RemoteUniqueDeptService remoteUniqueDeptService,
            @Qualifier("plainRestTemplate") RestTemplate restTemplate,
            MessageStateMachine messageStateMachine) {
        this.umpMsgMainService = umpMsgMainService;
        this.umpMsgInboxService = umpMsgInboxService;
        this.umpAppCredentialService = umpAppCredentialService;
        this.umpMsgQueueService = umpMsgQueueService;
        this.umpTopicSubscriptionService = umpTopicSubscriptionService;
        this.umpMsgCallbackService = umpMsgCallbackService;
        this.umpMsgBroadcastService = umpMsgBroadcastService;
        this.restTemplate = restTemplate;
        this.messageStateMachine = messageStateMachine;
    }

    // ==================== 公共入口 ====================

    /**
     * 处理推送任务（由 MESSAGE_DISTRIBUTED 事件触发）
     * 任务数据中应包含 inboxIds 或 broadcastIds 以及对应的应用标识 receiverAppKeys（单元素列表）
     */
    public void pushMessageToReceiver(MqMessage<TaskData> message) {
        String messageId = extractMessageId(message);
        TaskData payload = message.getPayload();
        String taskId = payload != null ? payload.getTaskId() : null;
        String workerId = WORKER_PREFIX + Thread.currentThread().getId();

        if (StrUtil.isBlank(messageId)) {
            log.error("消息ID为空，无法处理推送任务");
            return;
        }
        if (StrUtil.isBlank(taskId)) {
            log.error("任务ID为空，消息ID: {}", messageId);
            return;
        }

        try {
            // 标记任务为处理中（独立事务）
            markTaskProcessing(taskId, workerId);

            // 加载消息数据
            PushContext context = loadPushContext(messageId, payload);
            if (context == null) {
                umpMsgQueueService.markAsFailed(taskId, workerId, "加载推送数据失败", null);
                return;
            }

            // 执行推送（不包含事务）
            boolean pushSuccess = doPush(context);

            if (pushSuccess) {
                // 标记任务成功（独立事务）
                markTaskSuccess(taskId, workerId, message);
            } else {
                // 标记失败并触发重试
                umpMsgQueueService.failAndScheduleRetry(taskId, workerId, "推送失败", null, MqMessageEventConstants.RetryDefaults.MAX_INTERVAL);
            }

        } catch (Exception e) {
            log.error("处理推送任务失败，任务ID: {}, 消息ID: {}", taskId, messageId, e);
            
            UmpMsgQueue queueTask = umpMsgQueueService.getById(taskId);
            if (queueTask != null && queueTask.getCurrentRetry() == 0) {
                messageStateMachine.onRetryPush(message);
            }
            
            umpMsgQueueService.failAndScheduleRetry(taskId, workerId, e.getMessage(), e.toString(), MqMessageEventConstants.RetryDefaults.MAX_INTERVAL);
        }
    }

    // ==================== 独立事务方法 ====================

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTaskProcessing(String taskId, String workerId) {
        umpMsgQueueService.markAsProcessing(taskId, workerId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markTaskSuccess(String taskId, String workerId, MqMessage<TaskData> message) {
        umpMsgQueueService.markAsSuccess(taskId, workerId, "推送成功");
        messageStateMachine.onPushed(message);
    }

    // ==================== 数据加载 ====================

    /**
     * 推送上下文
     */
    private static class PushContext {
        UmpMsgMain message;
        String appKey;
        String callbackUrl;
        AppDetailVO app;
        String appSecret;
        CallbackConfig callbackConfig;
        List<String> inboxIds;
        List<String> broadcastIds;

        boolean isInboxPush() {
            return !CollectionUtils.isEmpty(inboxIds);
        }

        boolean isBroadcastPush() {
            return !CollectionUtils.isEmpty(broadcastIds);
        }
    }

    private PushContext loadPushContext(String messageId, TaskData payload) {
        if (payload == null) {
            log.error("推送任务数据为空，消息ID: {}", messageId);
            return null;
        }

        UmpMsgMain message = umpMsgMainService.getById(messageId);
        if (message == null) {
            log.error("消息不存在，消息ID: {}", messageId);
            return null;
        }

        List<String> receiverAppKeys = payload.getReceiverAppKeys();
        if (CollectionUtils.isEmpty(receiverAppKeys)) {
            log.error("推送任务缺少接收者应用标识，消息ID: {}", messageId);
            return null;
        }
        String appKey = receiverAppKeys.get(0);

        String callbackUrl = getCallbackUrl(messageId, appKey);
        if (StrUtil.isBlank(callbackUrl)) {
            log.error("无法获取回调地址，消息ID: {}, appKey: {}", messageId, appKey);
            return null;
        }

        AppDetailVO app = umpAppCredentialService.getAppByKey(appKey);
        if (app == null) {
            log.error("应用不存在，appKey: {}", appKey);
            return null;
        }

        String appSecret = umpAppCredentialService.getAppSecret(app.getId());
        CallbackConfig callbackConfig = resolveCallbackConfig(message, appKey);

        PushContext context = new PushContext();
        context.message = message;
        context.appKey = appKey;
        context.callbackUrl = callbackUrl;
        context.app = app;
        context.appSecret = appSecret;
        context.callbackConfig = callbackConfig;
        context.inboxIds = payload.getInboxIds();
        context.broadcastIds = payload.getBroadcastIds();

        return context;
    }

    /**
     * 解析回调配置
     */
    private CallbackConfig resolveCallbackConfig(UmpMsgMain message, String appKey) {
        // 优先使用主题订阅的配置
        if (StrUtil.isNotBlank(message.getTopicCode())) {
            SubscriptionDetailVO subscription = umpTopicSubscriptionService.getSubscription(
                message.getTopicCode(), appKey);
            if (subscription != null && subscription.getCallbackConfig() != null) {
                return BeanUtil.toBean(subscription.getCallbackConfig(), CallbackConfig.class);
            }
        }

        // 其次使用应用配置
        AppDetailVO app = umpAppCredentialService.getAppByKey(appKey);
        if (app != null && app.getCallbackConfig() != null) {
            return BeanUtil.toBean(app.getCallbackConfig(), CallbackConfig.class);
        }

        return null;
    }

    // ==================== 推送执行 ====================

    /**
     * 执行推送
     */
    private boolean doPush(PushContext context) {
        if (context.isInboxPush()) {
            return processInboxPush(context);
        } else if (context.isBroadcastPush()) {
            return processBroadcastPush(context);
        } else {
            log.error("推送任务缺少 inboxIds 或 broadcastIds，消息ID: {}", context.message.getId());
            return false;
        }
    }

    /**
     * 处理收件箱推送
     */
    private boolean processInboxPush(PushContext context) {
        List<UmpMsgInbox> inboxList = umpMsgInboxService.listByIds(context.inboxIds);
        if (inboxList.isEmpty()) {
            log.warn("未找到对应的收件箱记录，消息ID: {}, inboxIds: {}", 
                    context.message.getId(), context.inboxIds);
            return true; // 没有需要推送的记录，视为成功
        }

        // 批量发送回调
        CallbackResult result = sendBatchCallbacks(
            context.message,
            context.app,
            context.appSecret,
            context.callbackUrl,
            inboxList,
            context.callbackConfig,
            this::buildInboxCallbackRequest,
            this::getInboxId
        );

        // 批量更新推送状态
        updateInboxPushStatus(result.successIds, MessagePushStatus.SUCCESS.getCode());
        if (!result.failedIds.isEmpty()) {
            updateInboxPushStatus(result.failedIds, MessagePushStatus.FAILED.getCode());
            log.warn("收件箱推送部分失败，成功: {}, 失败: {}", 
                    result.successIds.size(), result.failedIds.size());
            return result.successIds.size() > 0; // 至少部分成功
        }

        return true;
    }

    /**
     * 处理广播推送
     */
    private boolean processBroadcastPush(PushContext context) {
        List<UmpMsgBroadcast> broadcastList = umpMsgBroadcastService.listByIds(context.broadcastIds);
        if (broadcastList.isEmpty()) {
            log.warn("未找到对应的广播记录，消息ID: {}, broadcastIds: {}", 
                    context.message.getId(), context.broadcastIds);
            return true;
        }

        // 批量发送回调
        CallbackResult result = sendBatchCallbacks(
            context.message,
            context.app,
            context.appSecret,
            context.callbackUrl,
            broadcastList,
            context.callbackConfig,
            this::buildBroadcastCallbackRequest,
            this::getBroadcastId
        );

        // 批量更新推送状态
        updateBroadcastPushStatus(result.successIds, MessagePushStatus.SUCCESS.getCode());
        if (!result.failedIds.isEmpty()) {
            updateBroadcastPushStatus(result.failedIds, MessagePushStatus.FAILED.getCode());
            log.warn("广播推送部分失败，成功: {}, 失败: {}", 
                    result.successIds.size(), result.failedIds.size());
            return result.successIds.size() > 0;
        }

        return true;
    }

    // ==================== 统一回调发送器 ====================

    /**
     * 回调结果
     */
    private static class CallbackResult {
        List<String> successIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
    }

    /**
     * 批量发送回调请求
     */
    private <T> CallbackResult sendBatchCallbacks(
            UmpMsgMain msg,
            AppDetailVO app,
            String appSecret,
            String callbackUrl,
            List<T> targetList,
            CallbackConfig callbackConfig,
            RequestBuilder<T> requestBuilder,
            Function<T, String> idExtractor) {

        CallbackResult result = new CallbackResult();
        
        for (T target : targetList) {
            String targetId = idExtractor.apply(target);
            String callbackId = null;
            
            try {
                // 构建请求
                HttpEntity<?> requestEntity = requestBuilder.build(
                    msg, app, appSecret, callbackUrl, target, callbackConfig);
                
                Map<String, Object> callbackData = null;
                if (callbackConfig == null || callbackConfig.getRequest() == null) {
                	callbackData = BeanUtil.beanToMap(requestEntity);
                } else {
                	callbackData = BeanUtil.beanToMap(callbackConfig.getRequest());
                }

                // 创建回调记录（发送前创建，状态为PENDING）
                callbackId = umpMsgCallbackService.createCallback(
                    msg.getId(),
                    callbackUrl,
                    callbackData,
                    extractSignature(requestEntity)
                );

                // 发送请求（带重试）
                ResponseEntity<Map> response = sendWithRetry(callbackUrl, requestEntity);

                // 处理响应
                if (isCallbackSuccessful(response)) {
                    //umpMsgCallbackService.markAsSuccess(callbackId, buildCallbackResponse(response));
                	umpMsgCallbackService.markAsSuccess(callbackId, BeanUtil.beanToMap(response));
                    result.successIds.add(targetId);
                    log.debug("回调成功，targetId: {}, callbackId: {}", targetId, callbackId);
                } else {
                    String errorMsg = buildFailureMessage(response);
                    umpMsgCallbackService.markAsFailed(callbackId, errorMsg);
                    result.failedIds.add(targetId);
                    log.warn("回调失败，targetId: {}, error: {}", targetId, errorMsg);
                }

            } catch (Exception e) {
                log.error("回调异常，targetId: {}", targetId, e);
                result.failedIds.add(targetId);
                if (callbackId != null) {
                    umpMsgCallbackService.markAsFailed(callbackId, e.getMessage());
                }
            }
        }

        return result;
    }
    
    private CallbackConfigResponse buildCallbackResponse(ResponseEntity<Map> response) {
        CallbackConfigResponse ccResponse = new CallbackConfigResponse();
        if (response.getBody() != null) {
            ccResponse.setCode(getStringValue(response.getBody(), "code", CommonConstants.FAIL.toString()));
            ccResponse.setMsg(getStringValue(response.getBody(), "msg", ""));
            ccResponse.setHttpStatus(response.getStatusCode().toString());
        }
        return ccResponse;
    }

    /**
     * 安全获取Map中的字符串值
     */
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

	/**
     * 带重试的发送
     */
    private ResponseEntity<Map> sendWithRetry(String url, HttpEntity<?> requestEntity) {
        Exception lastException = null;
        
        for (int i = 0; i < DEFAULT_RETRY_TIMES; i++) {
            try {
                if (i > 0) {
                    Thread.sleep(RETRY_BASE_DELAY * i);
                    log.info("第{}次重试，url: {}", i + 1, url);
                }
                return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            } catch (Exception e) {
                lastException = e;
                log.warn("发送失败，第{}次尝试，url: {}, error: {}", i + 1, url, e.getMessage());
            }
        }
        
        throw new RuntimeException("发送失败，已重试" + DEFAULT_RETRY_TIMES + "次", lastException);
    }

    /**
     * 判断回调是否成功
     */
    private boolean isCallbackSuccessful(ResponseEntity<Map> response) {
        if (response == null || response.getStatusCode() == null) {
            return false;
        }
        
        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            return false;
        }
        
        if (response.getBody() == null) {
            return false;
        }
        
        Object code = response.getBody().get("code");
        return code != null && CommonConstants.SUCCESS.toString().equals(code.toString());
    }

    /**
     * 构建失败消息
     */
    private String buildFailureMessage(ResponseEntity<Map> response) {
        int httpStatus = response.getStatusCode().value();
        String businessCode = extractBusinessCode(response);
        return String.format("HTTP状态: %d, 业务码: %s", httpStatus, businessCode);
    }

    /**
     * 提取业务码
     */
    private String extractBusinessCode(ResponseEntity<Map> response) {
        if (response.getBody() == null) {
            return CommonConstants.FAIL.toString();
        }
        Object code = response.getBody().get("code");
        return code != null ? code.toString() : CommonConstants.FAIL.toString();
    }

    /**
     * 提取签名
     */
    private String extractSignature(HttpEntity<?> requestEntity) {
        if (requestEntity.getHeaders() == null) {
            return "";
        }
        List<String> signatures = requestEntity.getHeaders().get(CallbackHeaders.SIGNATURE);
        return signatures != null && !signatures.isEmpty() ? signatures.get(0) : "";
    }

    // ==================== 请求构建器 ====================

    @FunctionalInterface
    private interface RequestBuilder<T> {
        HttpEntity<?> build(UmpMsgMain msg, AppDetailVO app, String appSecret,
                           String callbackUrl, T target, CallbackConfig callbackConfig);
    }

    /**
     * 构建收件箱回调请求
     */
    private HttpEntity<?> buildInboxCallbackRequest(
            UmpMsgMain msg, AppDetailVO app, String appSecret,
            String callbackUrl, UmpMsgInbox inbox, CallbackConfig callbackConfig) {
        
        UnifiedMessageDetail body = buildBaseMessageDetail(msg, callbackUrl);
        
        // 设置接收者信息
        if (inbox != null) {
            body.setReceiverIdNumber(inbox.getReceiverIdNumber());
            body.setReceiverName(inbox.getReceiverName());
            body.setReceivingUnitCode(inbox.getReceivingUnitCode());
            body.setReceivingUnitName(inbox.getReceivingUnitName());
            body.setSendTargetType(inbox.getReceiverType());
        }

        // 设置状态信息
        if (inbox != null && inbox.getReadStatus() != null) {
            body.setProcessStatus(inbox.getReadStatus().toString());
        }

        return buildHttpEntity(body, app, appSecret, callbackConfig);
    }

    /**
     * 构建广播回调请求
     */
    private HttpEntity<?> buildBroadcastCallbackRequest(
            UmpMsgMain msg, AppDetailVO app, String appSecret,
            String callbackUrl, UmpMsgBroadcast broadcast, CallbackConfig callbackConfig) {
        
        UnifiedMessageDetail body = buildBaseMessageDetail(msg, callbackUrl);
        
        // 设置广播信息
        if (broadcast != null) {
            body.setReceivingUnitCode(broadcast.getReceivingUnitCode());
            body.setReceivingUnitName(broadcast.getReceivingUnitName());
            body.setSendTargetType(msg.getSendTargetType());
        }

        // 设置状态信息
        if (broadcast != null && broadcast.getStatus() != null) {
            body.setProcessStatus(broadcast.getStatus());
        }

        return buildHttpEntity(body, app, appSecret, callbackConfig);
    }

    /**
     * 构建基础消息详情
     */
    private UnifiedMessageDetail buildBaseMessageDetail(UmpMsgMain msg, String callbackUrl) {
        UnifiedMessageDetail detail = new UnifiedMessageDetail();
        
        detail.setMessageId(msg.getId());
        detail.setMessageCode(msg.getMsgCode());
        detail.setMessageTitle(msg.getTitle());
        detail.setMessageContent(msg.getContent() != null ? msg.getContent() : new MessageContent());
        detail.setMessageType(msg.getMsgType());
        
        if (msg.getPriority() != null) {
            detail.setPriority(msg.getPriority().toString());
        }
        
        detail.setProcessUrl(callbackUrl);
        detail.setBusinessParam(msg.getExtParams());
        detail.setIcon(null);
        
        // 发送方信息
        detail.setSenderIdNumber(msg.getSenderIdNumber());
        detail.setSenderName(msg.getSenderName());
        detail.setSenderPhone(msg.getSenderPhone());
        detail.setSendUnitCode(msg.getSenderUnitCode());
        detail.setSendUnitName(msg.getSenderUnitName());
        detail.setSendTargetType(msg.getSendTargetType());
        
        // 时间信息
        DateTimeFormatter formatter = DatePattern.NORM_DATETIME_MS_FORMATTER;
        if (msg.getCreateTime() != null) {
            detail.setSendTime(msg.getCreateTime().format(formatter));
        }
        if (msg.getDistributeTime() != null) {
            detail.setDistributeTime(msg.getDistributeTime().format(formatter));
        }
        
        return detail;
    }

    /**
     * 构建HTTP实体
     */
    private HttpEntity<?> buildHttpEntity(UnifiedMessageDetail body, AppDetailVO app, 
                                          String appSecret, CallbackConfig callbackConfig) {
        
        // 生成签名相关数据
        Long timestamp = Instant.now().toEpochMilli();
        String nonce = RandomUtil.randomString(16);
        String signature = SignatureUtils.generateSignature(
            app.getAppKey(), appSecret, timestamp, nonce, null);
        String bodyMd5 = SignatureUtils.calculateBodyMd5(JSONUtil.toJsonPrettyStr(body));

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(CallbackHeaders.APP_KEY, app.getAppKey());
        headers.set(CallbackHeaders.TIMESTAMP, timestamp.toString());
        headers.set(CallbackHeaders.NONCE, nonce);
        headers.set(CallbackHeaders.SIGNATURE, signature);
        headers.set(CallbackHeaders.BODY_MD5, bodyMd5);

        // 合并自定义头（如果存在）
        if (callbackConfig != null && callbackConfig.getRequest() != null 
                && callbackConfig.getRequest().getHeaders() != null) {
            mergeCustomHeaders(headers, BeanUtil.beanToMap(callbackConfig.getRequest().getHeaders()), buildContext(app));
        }

        return new HttpEntity<>(body, headers);
    }

    /**
     * 构建上下文（用于占位符替换）
     */
    private Map<String, Object> buildContext(AppDetailVO app) {
        Map<String, Object> context = new HashMap<>();
        context.put("appKey", app.getAppKey());
        context.put("timestamp", Instant.now().toEpochMilli());
        context.put("nonce", RandomUtil.randomString(16));
        return context;
    }

    /**
     * 合并自定义头
     */
    private void mergeCustomHeaders(HttpHeaders headers, Map<String, Object> customHeaders, 
                                    Map<String, Object> context) {
        if (customHeaders == null) return;
        
        customHeaders.forEach((key, value) -> {
            if (StrUtil.isNotBlank(key) && StrUtil.isNotBlank(value.toString())) {
                String resolved = replacePlaceholder(value.toString(), context);
                headers.set(key, resolved);
            }
        });
    }

    /**
     * 递归替换占位符 ${key}
     */
    private String replacePlaceholder(String str, Map<String, Object> context) {
        if (StrUtil.isBlank(str) || context == null) return str;
        
        String result = str;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        return result;
    }

    // ==================== ID提取器 ====================

    private String getInboxId(UmpMsgInbox inbox) {
        return inbox != null ? inbox.getId() : null;
    }

    private String getBroadcastId(UmpMsgBroadcast broadcast) {
        return broadcast != null ? broadcast.getId() : null;
    }

    // ==================== 批量状态更新 ====================

    /**
     * 批量更新收件箱推送状态
     */
    private void updateInboxPushStatus(List<String> inboxIds, String status) {
        if (CollectionUtils.isEmpty(inboxIds)) return;
        
        umpMsgInboxService.lambdaUpdate()
                .in(UmpMsgInbox::getId, inboxIds)
                .set(UmpMsgInbox::getPushStatus, status)
                .update();
        
        log.debug("批量更新收件箱推送状态完成，数量: {}, 状态: {}", inboxIds.size(), status);
    }

    /**
     * 批量更新广播推送状态
     */
    private void updateBroadcastPushStatus(List<String> broadcastIds, String status) {
        if (CollectionUtils.isEmpty(broadcastIds)) return;
        
        umpMsgBroadcastService.lambdaUpdate()
                .in(UmpMsgBroadcast::getId, broadcastIds)
                .set(UmpMsgBroadcast::getPushStatus, status)
                .update();
        
        log.debug("批量更新广播推送状态完成，数量: {}, 状态: {}", broadcastIds.size(), status);
    }

    // ==================== 辅助方法 ====================

    /**
     * 获取回调地址，优先使用消息主表的 callbackUrl，若无则通过主题订阅获取
     */
    private String getCallbackUrl(String msgId, String appKey) {
        UmpMsgMain umpMsgMain = umpMsgMainService.getById(msgId);
        if (umpMsgMain == null) {
            return "";
        }
        
        String callbackUrl = umpMsgMain.getCallbackUrl();
        if (StrUtil.isNotBlank(callbackUrl)) {
            return callbackUrl;
        }
        
        if (StrUtil.isBlank(appKey) || StrUtil.isBlank(umpMsgMain.getTopicCode())) {
            return "";
        }
        
        SubscriptionDetailVO subscription = umpTopicSubscriptionService.getSubscription(
            umpMsgMain.getTopicCode(), appKey);
        return subscription != null ? subscription.getCallbackUrl() : "";
    }

    /**
     * 提取消息ID
     */
    private String extractMessageId(MqMessage<TaskData> message) {
        if (message == null) return null;
        
//        TaskData payload = message.getPayload();
//        if (payload != null && StrUtil.isNotBlank(payload.getMessageId())) {
//            return payload.getMessageId();
//        }
        return message.getMessageId();
    }
}