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
package ltd.huntinginfo.feng.auth.support.password;

import static ltd.huntinginfo.feng.common.core.constant.SecurityConstants.PASSWORD;

import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import ltd.huntinginfo.feng.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationConverter;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.security.util.OAuth2EndpointUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * OAuth2 资源所有者密码认证转换器
 *
 * @author lengleng
 * @date 2025/05/30
 */
public class OAuth2ResourceOwnerPasswordAuthenticationConverter
		extends OAuth2ResourceOwnerBaseAuthenticationConverter<OAuth2ResourceOwnerPasswordAuthenticationToken> {

	/**
	 * 支持密码模式
	 * @param grantType 授权类型
	 */
	@Override
	public boolean support(String grantType) {
		return PASSWORD.equals(grantType);
	}

	/**
	 * 构建OAuth2资源所有者密码认证令牌
	 * @param clientPrincipal 客户端主体认证信息
	 * @param requestedScopes 请求的作用域集合
	 * @param additionalParameters 附加参数映射
	 * @return 构建完成的OAuth2资源所有者密码认证令牌
	 */
	@Override
	public OAuth2ResourceOwnerPasswordAuthenticationToken buildToken(Authentication clientPrincipal,
			Set requestedScopes, Map additionalParameters) {
		return new OAuth2ResourceOwnerPasswordAuthenticationToken(new AuthorizationGrantType(PASSWORD), clientPrincipal,
				requestedScopes, additionalParameters);
	}

	/**
	 * 校验扩展参数 密码模式密码必须不为空
	 * @param request 参数列表
	 */
	@Override
	public void checkParams(HttpServletRequest request) {
		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
		// username (REQUIRED)
		String username = parameters.getFirst(CommonConstants.USERNAME);
		if (!StringUtils.hasText(username) || parameters.get(CommonConstants.USERNAME).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, CommonConstants.USERNAME,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		// password (REQUIRED)
		String password = parameters.getFirst(CommonConstants.PASSWORD);
		if (!StringUtils.hasText(password) || parameters.get(CommonConstants.PASSWORD).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, CommonConstants.PASSWORD,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
	}

}
