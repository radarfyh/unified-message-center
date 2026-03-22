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
package ltd.huntinginfo.feng.gateway.filter;

import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * 全局拦截器，作用于所有微服务
 * <p>
 * 1. 清洗请求头中的from参数 2. 重写StripPrefix = 1，支持全局路由
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class FengRequestGlobalFilter implements GlobalFilter, Ordered {

	/**
	 * 处理Web请求并（可选地）通过给定的网关过滤器链委托给下一个过滤器
	 * @param exchange 当前服务器交换对象
	 * @param chain 提供委托给下一个过滤器的方式
	 * @return {@code Mono<Void>} 表示请求处理完成
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 记录原始请求信息
        ServerHttpRequest originalRequest = exchange.getRequest();
        String originalPath = originalRequest.getURI().getRawPath();
        log.debug("Original request URI: {}, path: {}", originalRequest.getURI(), originalPath);

		// 1. 清洗请求头中from 参数
		ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
			httpHeaders.remove(SecurityConstants.FROM);
			// 设置请求时间
			httpHeaders.put(CommonConstants.REQUEST_START_TIME,
					Collections.singletonList(String.valueOf(System.currentTimeMillis())));
		}).build();

		// 2. 重写StripPrefix
		addOriginalRequestUrl(exchange, request.getURI());
		String rawPath = request.getURI().getRawPath();
		String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath, "/"))
			.skip(1L)
			.collect(Collectors.joining("/"));
		
		log.debug("Rewritten path: {} -> {}", rawPath, newPath);

		ServerHttpRequest newRequest = request.mutate().path(newPath).build();
		exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());

		return chain.filter(exchange.mutate().request(newRequest.mutate().build()).build());
	}

	@Override
	public int getOrder() {
		return 10;
	}

}
