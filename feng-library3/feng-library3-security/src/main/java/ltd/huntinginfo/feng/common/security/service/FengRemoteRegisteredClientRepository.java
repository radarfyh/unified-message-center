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

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.admin.api.entity.SysOauthClientDetails;
import ltd.huntinginfo.feng.admin.api.feign.RemoteClientDetailsService;
import ltd.huntinginfo.feng.common.core.constant.CacheConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.util.RetOps;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * 查询客户端相关信息实现类，支持Redis缓存
 *
 * @author lengleng
 * @date 2025/05/31
 */
@RequiredArgsConstructor
public class FengRemoteRegisteredClientRepository implements RegisteredClientRepository {

	/**
	 * 刷新令牌有效期默认 30 天
	 */
	private final static int refreshTokenValiditySeconds = 60 * 60 * 24 * 30;

	/**
	 * 请求令牌有效期默认 12 小时
	 */
	private final static int accessTokenValiditySeconds = 60 * 60 * 12;

	/**
	 * 远程客户端详情服务
	 */
	private final RemoteClientDetailsService clientDetailsService;

	/**
	 * 保存注册的客户端
	 *
	 * <p>
	 * 重要提示：敏感信息应在实现外部进行编码，例如 {@link RegisteredClient#getClientSecret()}
	 * </p>
	 * @param registeredClient 要保存的注册客户端
	 */
	@Override
	public void save(RegisteredClient registeredClient) {
	}

	/**
	 * 根据ID查找已注册的客户端
	 * @param id 注册标识符
	 * @return 找到的{@link RegisteredClient}，未找到则返回{@code null}
	 */
	@Override
	public RegisteredClient findById(String id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 根据客户端ID查询注册客户端信息，支持Redis缓存
	 * @param clientId 客户端ID
	 * @return 注册客户端信息
	 * @throws OAuth2AuthorizationCodeRequestAuthenticationException 客户端查询异常时抛出
	 */
	@Override
	@SneakyThrows
	@Cacheable(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientId", unless = "#result == null")
	public RegisteredClient findByClientId(String clientId) {

		SysOauthClientDetails clientDetails = RetOps.of(clientDetailsService.getClientDetailsById(clientId))
			.getData()
			.orElseThrow(() -> new OAuth2AuthorizationCodeRequestAuthenticationException(
					new OAuth2Error("客户端查询异常，请检查数据库链接"), null));

		RegisteredClient.Builder builder = RegisteredClient.withId(clientDetails.getClientId())
			.clientId(clientDetails.getClientId())
			.clientSecret(SecurityConstants.NOOP + clientDetails.getClientSecret())
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

		for (String authorizedGrantType : clientDetails.getAuthorizedGrantTypes()) {
			builder.authorizationGrantType(new AuthorizationGrantType(authorizedGrantType));

		}
		// 回调地址
		Optional.ofNullable(clientDetails.getWebServerRedirectUri())
			.ifPresent(redirectUri -> Arrays.stream(redirectUri.split(StrUtil.COMMA))
				.filter(StrUtil::isNotBlank)
				.forEach(builder::redirectUri));

		// scope
		Optional.ofNullable(clientDetails.getScope())
			.ifPresent(scope -> Arrays.stream(scope.split(StrUtil.COMMA))
				.filter(StrUtil::isNotBlank)
				.forEach(builder::scope));

		return builder
			.tokenSettings(TokenSettings.builder()
//				.accessTokenFormat(OAuth2TokenFormat.REFERENCE)
				.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // 改为 JWT
				.accessTokenTimeToLive(Duration.ofSeconds(
						Optional.ofNullable(clientDetails.getAccessTokenValidity()).orElse(accessTokenValiditySeconds)))
				.refreshTokenTimeToLive(Duration.ofSeconds(Optional.ofNullable(clientDetails.getRefreshTokenValidity())
					.orElse(refreshTokenValiditySeconds)))
				.build())
			.clientSettings(ClientSettings.builder()
				.requireAuthorizationConsent(!BooleanUtil.toBoolean(clientDetails.getAutoapprove()))
				.build())
			.build();

	}

}
