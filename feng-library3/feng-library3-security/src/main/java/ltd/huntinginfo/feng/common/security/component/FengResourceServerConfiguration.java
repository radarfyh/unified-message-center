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
package ltd.huntinginfo.feng.common.security.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.convert.converter.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 资源服务器认证授权配置
 *
 * @author lengleng
 * @date 2025/05/31
 */
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class FengResourceServerConfiguration {

	/**
	 * 资源认证异常处理入口点
	 */
	protected final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

	/**
	 * 允许所有URL的配置属性
	 */
	private final PermitAllUrlProperties permitAllUrl;

	/**
	 * FengBearerToken提取器
	 */
	private final FengBearerTokenExtractor fengBearerTokenExtractor;

	/**
	 * 自定义不透明令牌解析器
	 */
	private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;

	/**
	 * CORS跨域资源共享配置属性
	 */
	private final FengBootCorsProperties FengBootCorsProperties;
	
	@Value("${jwt.uri:http://feng-gateway3/auth/oauth2/jwks}")
	private String jwkSetUri;

	/**
	 * 负载均衡的 RestTemplate，用于通过服务名调用认证服务器
	 */
	@Bean
	@LoadBalanced
	public RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}
	
    /**
     * 配置 JWT 解码器，从授权服务器的 JWK Set 端点获取公钥
     */
    @Bean
    public JwtDecoder jwtDecoder(RestTemplate loadBalancedRestTemplate) {
    	log.info("Creating JwtDecoder with RestTemplate class: {} jwt.uri: {}", loadBalancedRestTemplate.getClass(), jwkSetUri);
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
				.restOperations(loadBalancedRestTemplate)
				.build();
    }
    
    /**
     * 自定义 JWT 认证转换器，将 JWT 转换为包含 FengUser 的 Authentication
     */
    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(UserDetailsService userDetailsService) {
        return new FengJwtAuthenticationConverter(userDetailsService);
    }
    
    /**
     * 自定义 AuthenticationManagerResolver，同时支持 JWT 和不透明令牌
     */
    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver(
            JwtDecoder jwtDecoder,
            OpaqueTokenIntrospector introspector,
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) {
        // 创建 JWT 认证管理器
        JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider(jwtDecoder);
        jwtProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
        AuthenticationManager jwtManager = new ProviderManager(jwtProvider);
        
        // 创建不透明令牌认证管理器
        OpaqueTokenAuthenticationProvider opaqueProvider = new OpaqueTokenAuthenticationProvider(introspector);
        AuthenticationManager opaqueManager = new ProviderManager(opaqueProvider);
        
        return request -> {
            // 从请求中提取 token
            String token = fengBearerTokenExtractor.resolve(request);
            if (token == null) {
                // 无 token 时使用默认，后续会触发认证入口点
                return opaqueManager; // 或 throw
            }
            // 判断是否为 JWT：包含两个点，且每部分都是 base64url 编码
            if (token.contains(".") && token.split("\\.").length == 3) {
                return jwtManager;
            } else {
                return opaqueManager;
            }
        };
    }
    
	/**
	 * 资源服务器安全配置
	 * @param http http
	 * @return {@link SecurityFilterChain }
	 * @throws Exception 异常
	 */
	@Bean
	SecurityFilterChain resourceServer(HttpSecurity http, 
			AuthenticationManagerResolver<HttpServletRequest> tokenAuthenticationManagerResolver) throws Exception {
		/**
		 * AntPathRequestMatcher[] permitMatchers = permitAllUrl.getUrls() .stream()
		 * .map(AntPathRequestMatcher::new) .toList() .toArray(new AntPathRequestMatcher[]
		 * {});
		 **/
		PathPatternRequestMatcher[] permitMatchers = permitAllUrl.getUrls()
			.stream()
			.map(url -> PathPatternRequestMatcher.withDefaults().matcher(url))
			.toList()
			.toArray(new PathPatternRequestMatcher[] {});

	    http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(permitMatchers).permitAll()
                .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
                .authenticationManagerResolver(tokenAuthenticationManagerResolver)
                .authenticationEntryPoint(resourceAuthExceptionEntryPoint)
                .bearerTokenResolver(fengBearerTokenExtractor)
        )
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .csrf(AbstractHttpConfigurer::disable);

		// 配置 CORS 跨域资源共享
		if (Boolean.TRUE.equals(FengBootCorsProperties.getEnabled())) {
			http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		}

		return http.build();
	}

	/**
	 * 配置 CORS 跨域资源共享
	 * @return UrlBasedCorsConfigurationSource CORS配置源
	 */
	private UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		// 从配置文件读取允许的源模式
		FengBootCorsProperties.getAllowedOriginPatterns().forEach(corsConfiguration::addAllowedOriginPattern);
		// 从配置文件读取允许的请求头
		FengBootCorsProperties.getAllowedHeaders().forEach(corsConfiguration::addAllowedHeader);
		// 从配置文件读取允许的HTTP方法
		FengBootCorsProperties.getAllowedMethods().forEach(corsConfiguration::addAllowedMethod);
		// 从配置文件读取是否允许携带凭证
		corsConfiguration.setAllowCredentials(FengBootCorsProperties.getAllowCredentials());

		// 注册CORS配置到指定路径
		source.registerCorsConfiguration(FengBootCorsProperties.getPathPattern(), corsConfiguration);

		return source;
	}

}
