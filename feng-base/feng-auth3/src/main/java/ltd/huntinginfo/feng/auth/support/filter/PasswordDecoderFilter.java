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
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.servlet.RepeatBodyRequestWrapper;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 密码解密过滤器：用于处理登录请求中的密码解密
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordDecoderFilter extends OncePerRequestFilter {

	private final AuthSecurityConfigProperties authSecurityConfigProperties;

	private static final String PASSWORD = "password";

	private static final String KEY_ALGORITHM = "AES";

	static {
		// 关闭hutool 强制关闭Bouncy Castle库的依赖
		SecureUtil.disableBouncyCastle();
	}

	/**
	 * 过滤器内部处理逻辑，用于处理登录请求中的密码解密
	 * @param request HTTP请求对象
	 * @param response HTTP响应对象
	 * @param chain 过滤器链
	 * @throws ServletException 如果发生servlet相关异常
	 * @throws IOException 如果发生I/O异常
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		// 不是登录请求，直接向下执行
		if (!StrUtil.containsAnyIgnoreCase(request.getRequestURI(), SecurityConstants.OAUTH_TOKEN_URL)) {
			chain.doFilter(request, response);
			return;
		}

		// 将请求流转换为可多次读取的请求流
		RepeatBodyRequestWrapper requestWrapper = new RepeatBodyRequestWrapper(request);
		Map<String, String[]> parameterMap = requestWrapper.getParameterMap();
		
		//log.info("authSecurityConfigProperties: {}", authSecurityConfigProperties.toString());
		
		// 构建前端对应解密AES 因子
		AES aes = new AES(Mode.CFB, Padding.NoPadding,
				new SecretKeySpec(authSecurityConfigProperties.getEncodeKey().getBytes(), KEY_ALGORITHM),
				new IvParameterSpec(authSecurityConfigProperties.getEncodeKey().getBytes()));

		parameterMap.forEach((k, v) -> {
			String[] values = parameterMap.get(k);
			if (!PASSWORD.equals(k) || ArrayUtil.isEmpty(values)) {
				return;
			}

			// 解密密码
			String decryptPassword = aes.decryptStr(values[0]);
			parameterMap.put(k, new String[] { decryptPassword });
		});
		chain.doFilter(requestWrapper, response);
	}

}
