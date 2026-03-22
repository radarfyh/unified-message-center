/*
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
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
 *  Author: lengleng
 *
 *  Modified by radarfyh(Edison.Feng) on 2025-12-30.
 *  Copyright (c) 2026 radarfyh(Edison.Feng). All rights reserved.
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */

package ltd.huntinginfo.feng.auth.support.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import ltd.huntinginfo.feng.admin.api.entity.SysLog;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.core.util.WebUtils;
import ltd.huntinginfo.feng.common.log.event.SysLogEvent;
import ltd.huntinginfo.feng.common.log.util.LogTypeEnum;
import ltd.huntinginfo.feng.common.log.util.SysLogUtils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * 认证失败处理器：处理用户认证失败事件并记录日志
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class FengAuthenticationFailureEventHandler implements AuthenticationFailureHandler {

	/**
	 * 当认证失败时调用
	 * @param request 认证请求
	 * @param response 认证响应
	 * @param exception 认证失败的异常
	 */
	@Override
	@SneakyThrows
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) {
		String username = request.getParameter(CommonConstants.USERNAME);

//		log.info("用户：{} 登录失败，异常：", username, exception.getLocalizedMessage());	
		
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
	    String clientId = WebUtils.getClientId();
		log.error("用户：{} 应用Key：{}  授权模式：{}  登录失败，异常：", username, clientId, grantType, exception);
		
		SysLog logVo = SysLogUtils.getSysLog();
		logVo.setTitle("登录失败");
		logVo.setLogType(LogTypeEnum.ERROR.getType());
		logVo.setException(exception.getLocalizedMessage());
		// 发送异步日志事件
		String startTimeStr = request.getHeader(CommonConstants.REQUEST_START_TIME);
		if (StrUtil.isNotBlank(startTimeStr)) {
			Long startTime = Long.parseLong(startTimeStr);
			Long endTime = System.currentTimeMillis();
			logVo.setTime(endTime - startTime);
		}
		logVo.setCreateBy(username);
		SpringContextHolder.publishEvent(new SysLogEvent(logVo));
		// 写出错误信息
		sendErrorResponse(request, response, exception);
	}

	/**
	 * 发送错误响应
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @param exception 认证异常
	 * @throws IOException 写入响应时发生IO异常
	 */
	private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		// 直接设置响应状态码
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		OAuth2AuthenticationException authorizationException = (OAuth2AuthenticationException) exception;
		String errorMessage;
		if (exception instanceof OAuth2AuthenticationException) {
			errorMessage = StrUtil.isBlank(authorizationException.getError().getDescription())
					? authorizationException.getError().getErrorCode()
					: authorizationException.getError().getDescription();
		}
		else {
			errorMessage = exception.getLocalizedMessage();
		}

		// 构建JSON响应
		R<?> result = (authorizationException.getError().getErrorCode() != null)
				? R.failed(authorizationException.getError().getErrorCode(), errorMessage) : R.failed(errorMessage);

		// 设置响应内容类型
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		String jsonResponse = new ObjectMapper().writeValueAsString(result);
		response.getWriter().write(jsonResponse);
	}

}
