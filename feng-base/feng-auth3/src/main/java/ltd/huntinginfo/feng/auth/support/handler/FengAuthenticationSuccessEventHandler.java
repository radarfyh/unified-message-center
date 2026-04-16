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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import ltd.huntinginfo.feng.admin.api.entity.SysLog;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.log.event.SysLogEvent;
import ltd.huntinginfo.feng.common.log.util.SysLogUtils;
import ltd.huntinginfo.feng.common.security.component.FengCustomOAuth2AccessTokenResponseHttpMessageConverter;
import ltd.huntinginfo.feng.common.security.service.FengUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * 处理认证成功事件的处理器
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class FengAuthenticationSuccessEventHandler implements AuthenticationSuccessHandler {

	private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter = new FengCustomOAuth2AccessTokenResponseHttpMessageConverter();

	/**
	 * 用户认证成功时调用
	 * @param request 触发认证成功的请求
	 * @param response 响应对象
	 * @param authentication 认证过程中创建的认证对象
	 */
	@SneakyThrows
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;
		Map<String, Object> map = accessTokenAuthentication.getAdditionalParameters();
		if (MapUtil.isNotEmpty(map)) {
			// 发送异步日志事件
			FengUser userInfo = (FengUser) map.get(SecurityConstants.DETAILS_USER);

			SecurityContextHolder.getContext().setAuthentication(accessTokenAuthentication);
			SysLog logVo = SysLogUtils.getSysLog();
			logVo.setTitle("登录成功");
			String startTimeStr = request.getHeader(CommonConstants.REQUEST_START_TIME);
			if (StrUtil.isNotBlank(startTimeStr)) {
				Long startTime = Long.parseLong(startTimeStr);
				Long endTime = System.currentTimeMillis();
				logVo.setTime(endTime - startTime);
			}
			if (userInfo != null) {
				logVo.setCreateBy(userInfo.getName());
				log.debug("用户：{} 登录成功 用户信息：{}，日志信息：{}", userInfo.getName(), JSONUtil.toJsonPrettyStr(userInfo), JSONUtil.toJsonPrettyStr(logVo));
			}
			SpringContextHolder.publishEvent(new SysLogEvent(logVo));
		}

		// 输出token
		sendAccessTokenResponse(request, response, authentication);
	}

	/**
	 * 发送访问令牌响应
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @param authentication 认证信息
	 * @throws IOException 写入响应时可能抛出IO异常
	 */
	private void sendAccessTokenResponse(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;

		OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
		OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
		Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

		OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
			.tokenType(accessToken.getTokenType())
			.scopes(accessToken.getScopes());
		if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
			builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
		}
		if (refreshToken != null) {
			builder.refreshToken(refreshToken.getTokenValue());
		}
		if (!CollectionUtils.isEmpty(additionalParameters)) {
			builder.additionalParameters(additionalParameters);
		}
		OAuth2AccessTokenResponse accessTokenResponse = builder.build();
		ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

		// 无状态 注意删除 context 上下文的信息
		SecurityContextHolder.clearContext();

		this.accessTokenHttpResponseConverter.write(accessTokenResponse, null, httpResponse);
	}

}
