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
package ltd.huntinginfo.feng.common.security.service;

import ltd.huntinginfo.feng.common.core.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis实现的OAuth2授权同意服务
 *
 * @author lengleng
 * @date 2025/05/31
 */
@RequiredArgsConstructor
public class FengRedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

	private final static Long TIMEOUT = 10L;

	/**
	 * 保存OAuth2授权同意信息
	 * @param authorizationConsent 授权同意信息，不能为null
	 * @throws IllegalArgumentException 当authorizationConsent为null时抛出
	 */
	@Override
	public void save(OAuth2AuthorizationConsent authorizationConsent) {
		Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");

		RedisUtils.set(buildKey(authorizationConsent), authorizationConsent, TIMEOUT, TimeUnit.MINUTES);
	}

	/**
	 * 移除OAuth2授权同意信息
	 * @param authorizationConsent 授权同意信息，不能为null
	 * @throws IllegalArgumentException 当authorizationConsent为null时抛出
	 */
	@Override
	public void remove(OAuth2AuthorizationConsent authorizationConsent) {
		Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
		RedisUtils.delete(buildKey(authorizationConsent));
	}

	/**
	 * 根据注册客户端ID和主体名称查找OAuth2授权同意信息
	 * @param registeredClientId 注册客户端ID，不能为空
	 * @param principalName 主体名称，不能为空
	 * @return 查找到的OAuth2授权同意信息，可能为null
	 */
	@Override
	public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
		Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
		Assert.hasText(principalName, "principalName cannot be empty");
		return RedisUtils.get(buildKey(registeredClientId, principalName));
	}

	/**
	 * 构建授权确认信息的key
	 * @param registeredClientId 注册客户端ID
	 * @param principalName 主体名称
	 * @return 拼接后的key字符串
	 */
	private static String buildKey(String registeredClientId, String principalName) {
		return "token:consent:" + registeredClientId + ":" + principalName;
	}

	/**
	 * 构建授权同意的键值
	 * @param authorizationConsent 授权同意对象
	 * @return 构建的键值字符串
	 */
	private static String buildKey(OAuth2AuthorizationConsent authorizationConsent) {
		return buildKey(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
	}

}
