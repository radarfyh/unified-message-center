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
package ltd.huntinginfo.feng.auth.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ltd.huntinginfo.feng.auth.support.CustomeOAuth2AccessTokenGenerator;
import ltd.huntinginfo.feng.auth.support.base.app.AppKeyAuthenticationConverter;
import ltd.huntinginfo.feng.auth.support.base.app.AppKeyAuthenticationProvider;
import ltd.huntinginfo.feng.auth.support.core.CustomeOAuth2TokenCustomizer;
import ltd.huntinginfo.feng.auth.support.core.FormIdentityLoginConfigurer;
import ltd.huntinginfo.feng.auth.support.core.FengDaoAuthenticationProvider;
import ltd.huntinginfo.feng.auth.support.filter.PasswordDecoderFilter;
import ltd.huntinginfo.feng.auth.support.filter.ValidateCodeFilter;
import ltd.huntinginfo.feng.auth.support.handler.FengAuthenticationFailureEventHandler;
import ltd.huntinginfo.feng.auth.support.handler.FengAuthenticationSuccessEventHandler;
import ltd.huntinginfo.feng.auth.support.password.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import ltd.huntinginfo.feng.auth.support.password.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import ltd.huntinginfo.feng.auth.support.sms.OAuth2ResourceOwnerSmsAuthenticationConverter;
import ltd.huntinginfo.feng.auth.support.sms.OAuth2ResourceOwnerSmsAuthenticationProvider;
import ltd.huntinginfo.feng.center.api.feign.RemoteAppCredentialService;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.security.component.FengBootCorsProperties;

import lombok.RequiredArgsConstructor;

/**
 * 认证服务器配置类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Configuration
@RequiredArgsConstructor
public class AuthorizationServerConfiguration {

	private final OAuth2AuthorizationService authorizationService;

	private final PasswordDecoderFilter passwordDecoderFilter;

	private final ValidateCodeFilter validateCodeFilter;

	private final FengBootCorsProperties fengBootCorsProperties;
	
	private final RemoteAppCredentialService remoteAppCredentialService;
	
	private final StringRedisTemplate redisTemplate;
	
	private final JwtEncoder jwtEncoder;

	/**
	 * Authorization Server 配置，仅对 /oauth2/** 的请求有效
	 * @param http http
	 * @return {@link SecurityFilterChain }
	 * @throws Exception 异常
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServer(HttpSecurity http) throws Exception {
		// 配置授权服务器的安全策略，只有/oauth2/**的请求才会走如下的配置
		http.securityMatcher("/oauth2/**");
		OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

		// 增加验证码过滤器
		http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class);
		// 增加密码解密过滤器
		http.addFilterBefore(passwordDecoderFilter, UsernamePasswordAuthenticationFilter.class);

		http.with(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> {// 个性化认证授权端点
				tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter()) // 注入自定义的授权认证Converter
					.accessTokenResponseHandler(new FengAuthenticationSuccessEventHandler()) // 登录成功处理器
					.errorResponseHandler(new FengAuthenticationFailureEventHandler());// 登录失败处理器
				})
				.clientAuthentication(oAuth2ClientAuthenticationConfigurer -> // 个性化客户端认证
					oAuth2ClientAuthenticationConfigurer.errorResponseHandler(new FengAuthenticationFailureEventHandler()))// 处理客户端认证异常
						.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint// 授权码端点个性化confirm页面
							.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI)), Customizer.withDefaults())
			.authorizeHttpRequests(authorizeRequests -> authorizeRequests
					.requestMatchers("/oauth2/jwks").permitAll()  // 放行 jwks 端点
					.anyRequest().authenticated());

		// 设置 Token 存储的策略
		http.with(authorizationServerConfigurer.authorizationService(authorizationService)// redis存储token的实现
			.authorizationServerSettings(
					AuthorizationServerSettings.builder().issuer(SecurityConstants.PROJECT_LICENSE).build()),
				Customizer.withDefaults());

		// 设置授权码模式登录页面
		http.with(new FormIdentityLoginConfigurer(), Customizer.withDefaults());

		// 配置 CORS 跨域资源共享
		if (Boolean.TRUE.equals(fengBootCorsProperties.getEnabled())) {
			http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		}

		DefaultSecurityFilterChain securityFilterChain = http.build();

		// 注入自定义授权模式实现
		addCustomOAuth2GrantAuthenticationProvider(http);

		return securityFilterChain;
	}

	/**
	 * 令牌生成规则实现 </br>
	 * client:username:uuid
	 * @return OAuth2TokenGenerator
	 */
	@Bean
	public OAuth2TokenGenerator oAuth2TokenGenerator() {
		// 支持 JWT 格式
		JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder); 
		
		CustomeOAuth2AccessTokenGenerator accessTokenGenerator = new CustomeOAuth2AccessTokenGenerator();
		// 注入Token 增加关联用户信息
		accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());
		return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, new OAuth2RefreshTokenGenerator());
	}

	/**
	 * request -> xToken 注入请求转换器
	 * @return DelegatingAuthenticationConverter
	 */
	@Bean
	public AuthenticationConverter accessTokenRequestConverter() {
		return new DelegatingAuthenticationConverter(Arrays.asList(
				new OAuth2ResourceOwnerPasswordAuthenticationConverter(),
				new OAuth2ResourceOwnerSmsAuthenticationConverter(), new OAuth2RefreshTokenAuthenticationConverter(),
				new OAuth2ClientCredentialsAuthenticationConverter(),
				new OAuth2AuthorizationCodeAuthenticationConverter(),
				new AppKeyAuthenticationConverter(), // 新增
				new OAuth2AuthorizationCodeRequestAuthenticationConverter()));
	}
	
	/**
	 * 注入授权模式实现提供方
	 * <p>
	 * 1. 密码模式 </br>
	 * 2. 短信登录 </br>
	 */
	private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http) {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);

		OAuth2ResourceOwnerPasswordAuthenticationProvider resourceOwnerPasswordAuthenticationProvider = new OAuth2ResourceOwnerPasswordAuthenticationProvider(
				authenticationManager, authorizationService, oAuth2TokenGenerator());

		OAuth2ResourceOwnerSmsAuthenticationProvider resourceOwnerSmsAuthenticationProvider = new OAuth2ResourceOwnerSmsAuthenticationProvider(
				authenticationManager, authorizationService, oAuth2TokenGenerator());
		
		AppKeyAuthenticationProvider appKeyAuthenticationProvider = new AppKeyAuthenticationProvider(
//				authenticationManager, 
				authorizationService, 
				oAuth2TokenGenerator(), 
	            redisTemplate, 
	            remoteAppCredentialService);

		// 处理 UsernamePasswordAuthenticationToken
		http.authenticationProvider(new FengDaoAuthenticationProvider());
		// 处理 OAuth2ResourceOwnerPasswordAuthenticationToken
		http.authenticationProvider(resourceOwnerPasswordAuthenticationProvider);
		// 处理 OAuth2ResourceOwnerSmsAuthenticationToken
		http.authenticationProvider(resourceOwnerSmsAuthenticationProvider);
		// 新增：处理AppKeyAuthenticationToken
	    http.authenticationProvider(appKeyAuthenticationProvider); 
	}

	/**
	 * 配置 CORS 跨域资源共享
	 * @return UrlBasedCorsConfigurationSource CORS配置源
	 */
	private UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		// 从配置文件读取允许的源模式
		fengBootCorsProperties.getAllowedOriginPatterns().forEach(corsConfiguration::addAllowedOriginPattern);
		// 从配置文件读取允许的请求头
		fengBootCorsProperties.getAllowedHeaders().forEach(corsConfiguration::addAllowedHeader);
		// 从配置文件读取允许的HTTP方法
		fengBootCorsProperties.getAllowedMethods().forEach(corsConfiguration::addAllowedMethod);
		// 从配置文件读取是否允许携带凭证
		corsConfiguration.setAllowCredentials(fengBootCorsProperties.getAllowCredentials());

		// 注册CORS配置到指定路径
		source.registerCorsConfiguration(fengBootCorsProperties.getPathPattern(), corsConfiguration);

		return source;
	}

}
