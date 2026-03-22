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
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MsgCodingDTO;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessagePollRequest;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessageReportRequest;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessageUnreadRequest;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.api.vo.AppKeyAuthResponse;
import ltd.huntinginfo.feng.center.api.vo.MsgCodingVO;
import ltd.huntinginfo.feng.center.api.vo.ServerTimestamp;
import ltd.huntinginfo.feng.center.service.AppAuthService;
import ltd.huntinginfo.feng.center.service.UmpAppCredentialService;
import ltd.huntinginfo.feng.center.service.UmpMsgMainService;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.UnifiedMessageResponse;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * 发送消息
     */
    @PostMapping("/message/send")
    @Operation(summary = "发送消息")
    public UnifiedMessageResponse<?> sendMessage(@Valid @RequestBody MessageSendDTO sendDTO,
    		@AuthenticationPrincipal Jwt jwt) {
    	log.debug("sendMessage sendDTO: {}", sendDTO);
    	
        // 从请求属性中获取认证后的应用信息
    	String appKey = jwt.getSubject();  // 直接获取 appKey
    	AppDetailVO app = umpAppCredentialService.getAppByKey(appKey);
    	if (app == null) {
    		return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), "AppKey不正确");
    	}

        // 调用服务创建消息
        String messageId = umpMsgMainService.createMessage(sendDTO);
        
        List<Object> records = new ArrayList<Object>();
        records.add(messageId);

        return UnifiedMessageResponse.success(records, "");
    }

    /**
     * 拉取消息
     */
    @PostMapping("/message/poll")
    @Operation(summary = "拉取消息")
    public UnifiedMessageResponse<?> pollMessages(@Valid @RequestBody UnifiedMessagePollRequest request,
                                                  @AuthenticationPrincipal Jwt jwt) {
        // 1. 从 JWT 中获取 appKey
        String appKey = jwt.getSubject();
        if (StrUtil.isBlank(appKey)) {
            return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), "应用标识不能为空");
        }

        // 7. 查询未接收消息（根据类型分发）
        UnifiedMessageResponse<?> result = umpMsgMainService.getUnreceivedMessagesByCursor(appKey, request);

        return result;
    }
    
    /**
     * 查询未读消息
     */
    @PostMapping("/message/unread")
    @Operation(summary = "查询未读消息")
    public UnifiedMessageResponse<?> getUnreadMessages(@Valid @RequestBody UnifiedMessageUnreadRequest request,
                                                  @AuthenticationPrincipal Jwt jwt) {
        // 1. 从 JWT 中获取 appKey
        String appKey = jwt.getSubject();
        if (StrUtil.isBlank(appKey)) {
            return UnifiedMessageResponse.fail(BusinessEnum.UMP_PARAM_MISSING.getCode().toString(), "应用标识不能为空");
        }

        // 7. 查询未接收消息（根据类型分发）
        UnifiedMessageResponse<?> result = umpMsgMainService.getUnreadMessagesByApp(appKey, request);

        return result;
    }

    /**
     * 上报消息已接收（回调成功）
     */
    @PostMapping("/message/received")
    @Operation(summary = "上报消息已接收")
    public UnifiedMessageResponse<?> reportReceived(@Valid @RequestBody UnifiedMessageReportRequest request,
                                               HttpServletRequest httpRequest) {
        String appKey = (String) httpRequest.getAttribute("appKey");
        // 业务系统上报已接收，更新消息状态为 BIZ_RECEIVED
        // 需要根据消息ID和接收者信息找到对应的收件箱记录或广播记录
        // 此处简化：调用服务方法处理
        umpMsgMainService.reportBizReceived(request.getMessageId(), request.getReceiverId(),
                request.getReceiverType(), request.getBroadcastId(), appKey);

        return UnifiedMessageResponse.success(null, "");
    }

    /**
     * 上报消息已拉取
     */
    @PostMapping("/message/pulled")
    @Operation(summary = "上报消息已拉取")
    public UnifiedMessageResponse<?> reportPulled(@Valid @RequestBody UnifiedMessageReportRequest request,
                                             HttpServletRequest httpRequest) {
        String appKey = (String) httpRequest.getAttribute("appKey");
        umpMsgMainService.reportBizPulled(request.getMessageId(), request.getReceiverId(),
                request.getReceiverType(), request.getBroadcastId(), appKey);
        return UnifiedMessageResponse.success(null, "");
    }

    /**
     * 上报消息已阅读
     */
    @PostMapping("/message/read")
    @Operation(summary = "上报消息已阅读")
    public UnifiedMessageResponse<?> reportRead(@Valid @RequestBody UnifiedMessageReportRequest request,
                                           HttpServletRequest httpRequest) {
        String appKey = (String) httpRequest.getAttribute("appKey");
        umpMsgMainService.reportBizRead(request.getMessageId(), request.getReceiverId(),
                request.getReceiverType(), request.getBroadcastId(), appKey);
        return UnifiedMessageResponse.success(null, "");
    }
    
    /**
     * 应用认证
     * 此处和AppKeyAuthenticationProvider.authenticate生成令牌方式不一致
     * 因此，若要使用本方法认证应用系统，必须把开放式API全部开放，让OAUTH2不验证JWT令牌，而是在开放式API内部验证JWT令牌
     */
//    @PostMapping("/app/authenticate")
//    @Operation(summary = "应用认证", 
//        description = "基于AppKey和AppSecret的应用认证服务，支持设备管理、执法系统等应用类型",
//        responses = {
//            @ApiResponse(responseCode = "200", description = "认证成功",
//                content = @Content(schema = @Schema(implementation = AppAuthResponse.class))),
//            @ApiResponse(responseCode = "400", description = "参数校验失败"),
//            @ApiResponse(responseCode = "401", description = "认证失败")
//        }
//    )    
//    @SysLog("应用认证")
//    public UnifiedMessageResponse<?> authenticate(
//            HttpServletRequest request, // 注入 HttpServletRequest
//            @Validated @RequestBody AppKeyAuthRequest authRequest) throws IOException {
//
//        // 校验 MD5 是否匹配
//        if (StrUtil.isNotBlank(authRequest.getBodyMd5())) {
//            // 直接从 request 参数中获取，并尝试转换为包装器
//            // 注意：由于配置了过滤器，这里拿到的应该是经过包装的请求
//            if (!(request instanceof ContentCachingRequestWrapper)) {
//                 // 理论上不应该发生，但做一层防御
//                 log.error("Request is not wrapped by ContentCachingRequestWrapper");
//                 return buildFailedResponse(BusinessEnum.UMP_PARAM_LENGTH_ERROR.getCode().toString(), "请求处理异常");
//            }
//            
//            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
//            byte[] bodyBytes = wrapper.getContentAsByteArray();
//            String computedMd5 = DigestUtil.md5Hex(bodyBytes);
//            
//	        if (!computedMd5.equals(authRequest.getBodyMd5())) {
//	            log.warn("请求体 MD5 不匹配，appKey: {}, expected: {}, actual: {}",
//	                    authRequest.getAppKey(), authRequest.getBodyMd5(), computedMd5);
//	            return buildFailedResponse(BusinessEnum.UMP_PARAM_LENGTH_ERROR.getCode().toString(), "消息体已被篡改");
//	        }
//        }
//
//        AppKeyAuthResponse response = appAuthService.authenticateByAppKey(authRequest);
//        List<Object> records = new ArrayList<Object>();
//        records.add(response);
//        return response.isSuccess() ? UnifiedMessageResponse.success(records, "") : UnifiedMessageResponse.fail(BusinessEnum.UMP_SERVICE_ERROR_13000.getCode().toString(), response.getErrorMsg());
//    }
    
    @PutMapping("/refreshSecret")
    @Operation(summary = "刷新应用密钥")
    @SysLog("刷新应用密钥")
    public UnifiedMessageResponse<?> refreshAppSecret(
            @Parameter(description = "应用标识", required = true)
            @RequestParam String appKey) {
        
        String newSecret = appAuthService.refreshAppSecret(appKey);
        List<Object> records = new ArrayList<Object>();
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
    	
        // 1. 创建LocalDateTime实例，例如当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        
        // 2. 将LocalDateTime转换为ZonedDateTime，指定时区，例如UTC
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));
        
        // 3. 将ZonedDateTime转换为Instant
        Instant instant = zonedDateTime.toInstant();
        
        // 4. 将Instant转换为Unix时间戳（毫秒）
        long timestampInMillis = instant.toEpochMilli();

    	sts.setTimestamp(timestampInMillis);
    	
        // 指定UTC偏移量，例如：+08:00对应中国标准时间
        ZoneOffset offset = ZoneOffset.of("+08:00");
        
        // 将LocalDateTime转换为OffsetDateTime
        OffsetDateTime offsetDateTime = localDateTime.atOffset(offset);
        
        // 格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatted = offsetDateTime.format(formatter);
    	sts.setStrDate(formatted);
    	List<Object> records = new ArrayList<>();
    	records.add(sts);
        return UnifiedMessageResponse.success(records, "");
    }
    
    @PostMapping("/applyCode")
    @Operation(summary = "申请消息编码")
    @SysLog("申请消息编码")
    public UnifiedMessageResponse<?> applyCode(@Valid @RequestBody MsgCodingDTO request,
                                                           HttpServletRequest httpRequest) {
        String appKey = (String) httpRequest.getAttribute("appKey");
        MsgCodingVO data = umpMsgMainService.generateMessageCode(request, appKey);
    	List<Object> records = new ArrayList<>();
    	records.add(data);
        return UnifiedMessageResponse.success(records, "");
    }
    
    // Swagger响应模型定义
    private static class AppAuthResponse extends R<AppKeyAuthResponse> {
        private static final long serialVersionUID = 1L;
    }
}
