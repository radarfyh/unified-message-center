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
package ltd.huntinginfo.feng.auth.support.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ltd.huntinginfo.feng.common.core.constant.CacheConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.exception.ValidateCodeException;
import ltd.huntinginfo.feng.common.core.util.RedisUtils;
import ltd.huntinginfo.feng.common.core.util.WebUtils;
import ltd.huntinginfo.feng.common.security.constants.GrantTypeConstants;

/**
 * 登录前处理器
 *
 * @author lengleng
 * @date 2024/4/3
 */

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证码过滤器：用于处理登录请求中的验证码校验
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateCodeFilter extends OncePerRequestFilter {

	private final AuthSecurityConfigProperties authSecurityConfigProperties;

	/**
	 * 过滤器内部处理逻辑，用于验证码校验
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @param filterChain 过滤器链
	 * @throws ServletException Servlet异常
	 * @throws IOException IO异常
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String requestUrl = request.getServletPath();

		// 不是登录URL 请求直接跳过
		if (!SecurityConstants.OAUTH_TOKEN_URL.equals(requestUrl)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 如果登录URL 但是刷新token的请求，直接向下执行
		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		//log.info("authSecurityConfigProperties: {}", authSecurityConfigProperties.toString());
		
		List<String> ignoreClients = authSecurityConfigProperties.getIgnoreClients();
		log.debug("Client ID from request: {}, Grant Type: {}, ignoreClients: {}", WebUtils.getClientId(), grantType, ignoreClients);		
		
		boolean isIgnoreClient = false;
		if (ignoreClients != null) {
			isIgnoreClient = ignoreClients.contains(WebUtils.getClientId());
		} else {
			if (StrUtil.equalsAnyIgnoreCase(grantType, GrantTypeConstants.APP_KEY.getValue())) {
				isIgnoreClient = true;
			}
		}
		if (StrUtil.equalsAnyIgnoreCase(grantType, 
					SecurityConstants.PASSWORD, 
					SecurityConstants.CLIENT_CREDENTIALS,
					SecurityConstants.AUTHORIZATION_CODE,
					GrantTypeConstants.APP_KEY.getValue()
				) && isIgnoreClient) {
			filterChain.doFilter(request, response);
			return;
		}

		// 校验验证码 1. 客户端开启验证码 2. 短信模式
		try {
			checkCode();
			filterChain.doFilter(request, response);
		}
		catch (ValidateCodeException validateCodeException) {
			throw new OAuth2AuthenticationException(validateCodeException.getMessage());
		}
	}

	/**
	 * 校验验证码
	 */
	private void checkCode() throws ValidateCodeException {
		Optional<HttpServletRequest> request = WebUtils.getRequest();
		String code = request.get().getParameter("code");

		if (StrUtil.isBlank(code)) {
			throw new ValidateCodeException("验证码不能为空");
		}

		String randomStr = request.get().getParameter("randomStr");

		String mobile = request.get().getParameter("mobile");
		if (StrUtil.isNotBlank(mobile)) {
			randomStr = mobile;
		}

		String key = CacheConstants.DEFAULT_CODE_KEY + randomStr;
		if (!RedisUtils.hasKey(key)) {
			throw new ValidateCodeException("验证码不合法");
		}

		String saveCode = RedisUtils.get(key);

		if (StrUtil.isBlank(saveCode)) {
			RedisUtils.delete(key);
			throw new ValidateCodeException("验证码不合法");
		}

		if (!StrUtil.equals(saveCode, code)) {
			RedisUtils.delete(key);
			throw new ValidateCodeException("验证码不合法");
		}
	}

}
