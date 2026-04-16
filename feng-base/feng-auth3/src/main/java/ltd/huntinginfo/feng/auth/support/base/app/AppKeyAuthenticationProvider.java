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
package ltd.huntinginfo.feng.auth.support.base.app;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.api.feign.RemoteAppCredentialService;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.util.SignatureUtils;
import ltd.huntinginfo.feng.common.security.app.AppKeyAuthenticationToken;
import ltd.huntinginfo.feng.common.security.constants.GrantTypeConstants;

/**
 * APPKEY认证模式认证提供者
 * @author radarfyh
 * @date 2026/01/30
 */
@Slf4j
public class AppKeyAuthenticationProvider implements AuthenticationProvider {

    private static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    private final OAuth2AuthorizationService authorizationService;
//    private final OAuth2TokenGenerator<?> tokenGenerator;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
//    private final RemoteClientDetailsService clientDetailsService;
    private final StringRedisTemplate redisTemplate;
    private final RemoteAppCredentialService appCredentialService;    
//	private final AuthenticationManager authenticationManager;
//	private final MessageSourceAccessor messages;
	
	@Deprecated
	private Supplier<String> refreshTokenGenerator;
	
	public AppKeyAuthenticationProvider(
//			AuthenticationManager authenticationManager,
			OAuth2AuthorizationService authorizationService,
			OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator,
			StringRedisTemplate redisTemplate,
			RemoteAppCredentialService appCredentialService) {
		Assert.notNull(authorizationService, "authorizationService cannot be null");
		Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
//		this.authenticationManager = authenticationManager;
		this.authorizationService = authorizationService;
		this.tokenGenerator = tokenGenerator;
		this.redisTemplate = redisTemplate;
		this.appCredentialService = appCredentialService;

		// 国际化配置
//		this.messages = new MessageSourceAccessor(SpringUtil.getBean("securityMessageSource"), Locale.CHINA);
	}
	
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AppKeyAuthenticationToken appKeyAuth = (AppKeyAuthenticationToken) authentication;

        // 获取客户端信息
        OAuth2ClientAuthenticationToken clientPrincipal = (OAuth2ClientAuthenticationToken) appKeyAuth.getPrincipal();
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 获取附加参数
        Map<String, Object> params = appKeyAuth.getAdditionalParameters();
        long timestamp = Long.parseLong(params.get("timestamp").toString());
        String nonce = params.get("nonce").toString();
        String signature = params.get("signature").toString();
        String bodyMd5 = (String) params.getOrDefault("bodyMd5", "");

        // 1. 时间窗口校验
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - timestamp) > CommonConstants.NONCE_TIME_WINDOW) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, "时间戳超时", ERROR_URI));
        }

        // 2. Nonce 防重放
        String nonceKey = CommonConstants.NONCE_CACHE_PREFIX + nonce;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(nonceKey))) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT, "重复的Nonce", ERROR_URI));
        }

        // 3. 查询应用凭证，验证状态
//        SysOauthClientDetails clientDetails = RetOps.of(clientDetailsService.getClientDetailsById(registeredClient.getClientId()))
//    			.getData()
//    			.orElseThrow(() -> new OAuthClientException("clientId 不合法"));
//        Set<String> authorizedScopes = StringUtils.commaDelimitedListToSet(clientDetails.getScope());
        AppDetailVO credential = appCredentialService.getAppCredentialByAppKey(registeredClient.getClientId());

        log.debug("app credential: {}", JSONUtil.toJsonPrettyStr(credential));
    	
        if (credential == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT, "应用标识无效", ERROR_URI));
        }
        if (credential.getStatus() != 1) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE, "应用已被禁用", ERROR_URI));
        }
        if (credential.getSecretExpireTime() != null && credential.getSecretExpireTime().isBefore(LocalDateTime.now())) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INSUFFICIENT_SCOPE, "应用凭证已过期", ERROR_URI));
        }

        // 4. 验证签名
        String appSecret = appCredentialService.getAppSecretByAppKey(registeredClient.getClientId()); // 或从 registeredClient.getClientSecret() 获取
        if (!SignatureUtils.verifySignature(registeredClient.getClientId(), appSecret,
                timestamp, nonce, bodyMd5, signature)) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED, "签名验证失败", ERROR_URI));
        }

        // 5. 缓存 Nonce
        redisTemplate.opsForValue().set(nonceKey, "1", CommonConstants.NONCE_TIME_WINDOW, TimeUnit.MILLISECONDS);

        // 6. 获取授权范围
        Set<String> authorizedScopes = appKeyAuth.getScopes();
        if (authorizedScopes == null) {
            authorizedScopes = registeredClient.getScopes();
        }

        // 7. 构建 OAuth2Authorization 并生成令牌
        // 参考 OAuth2ClientCredentialsAuthenticationProvider 的实现
        // 使用 tokenGenerator 生成 access_token 和 refresh_token
        // 构建 OAuth2Authorization 并保存
        
		// @formatter:off
		DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
				.registeredClient(registeredClient)
				.principal(appKeyAuth)
				.authorizationServerContext(AuthorizationServerContextHolder.getContext())
				.authorizedScopes(authorizedScopes)
				.authorizationGrantType(GrantTypeConstants.APP_KEY)
				.authorizationGrant(appKeyAuth);
		// @formatter:on
		
		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
				.withRegisteredClient(registeredClient)
				.principalName(appKeyAuth.getName())
				.authorizationGrantType(GrantTypeConstants.APP_KEY)
				.authorizedScopes(authorizedScopes);
		
		// ----- Access token -----
		OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
		OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
		if (generatedAccessToken == null) {
			OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
					"The token generator failed to generate the access token.", ERROR_URI);
			throw new OAuth2AuthenticationException(error);
		}
		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
				generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
		if (generatedAccessToken instanceof ClaimAccessor) {
			authorizationBuilder.id(accessToken.getTokenValue())
				.token(accessToken,
						(metadata) -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME,
								((ClaimAccessor) generatedAccessToken).getClaims()))
				// 0.4.0 新增的方法
				.authorizedScopes(authorizedScopes)
				.attribute(Principal.class.getName(), appKeyAuth);
		}
		else {
			authorizationBuilder.id(accessToken.getTokenValue()).accessToken(accessToken);
		}

		// ----- Refresh token -----
		OAuth2RefreshToken refreshToken = null;
		if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
		// Do not issue refresh token to public client
				!clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {

			if (this.refreshTokenGenerator != null) {
				Instant issuedAt = Instant.now();
				Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getRefreshTokenTimeToLive());
				refreshToken = new OAuth2RefreshToken(this.refreshTokenGenerator.get(), issuedAt, expiresAt);
			}
			else {
				tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
				OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
				if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
					OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
							"The token generator failed to generate the refresh token.", ERROR_URI);
					throw new OAuth2AuthenticationException(error);
				}
				refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
			}
			authorizationBuilder.refreshToken(refreshToken);
		}

		OAuth2Authorization authorization = authorizationBuilder.build();

		this.authorizationService.save(authorization);

        return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AppKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
	/**
	 * 设置刷新令牌生成器
	 * @param refreshTokenGenerator 刷新令牌生成器，不能为null
	 * @deprecated 该方法已废弃
	 */
	@Deprecated
	public void setRefreshTokenGenerator(Supplier<String> refreshTokenGenerator) {
		Assert.notNull(refreshTokenGenerator, "refreshTokenGenerator cannot be null");
		this.refreshTokenGenerator = refreshTokenGenerator;
	}
}