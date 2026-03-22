package ltd.huntinginfo.feng.common.security.component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.core.endpoint.DefaultOAuth2AccessTokenResponseMapConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
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
import org.springframework.util.StreamUtils;

import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;

import tools.jackson.databind.ObjectMapper;

/**
 * 扩展OAuth2AccessTokenResponseHttpMessageConverter，支持Long类型转String的Token响应转换
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class FengCustomOAuth2AccessTokenResponseHttpMessageConverter
		extends OAuth2AccessTokenResponseHttpMessageConverter {

	/**
	 * 字符串到对象的映射类型引用
	 */
	// private static final ParameterizedTypeReference<Map<String, Object>>
	// STRING_OBJECT_MAP = new ParameterizedTypeReference<Map<String, Object>>() {};

	/**
	 * OAuth2访问令牌响应参数转换器，用于将OAuth2AccessTokenResponse转换为Map<String, Object>
	 */
	private Converter<OAuth2AccessTokenResponse, Map<String, Object>> accessTokenResponseParametersConverter = new DefaultOAuth2AccessTokenResponseMapConverter();

	/**
	 * 将OAuth2访问令牌响应写入HTTP输出消息
	 * @param tokenResponse OAuth2访问令牌响应
	 * @param outputMessage HTTP输出消息
	 * @throws HttpMessageNotWritableException 写入响应时发生错误抛出异常
	 */
	protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage)
			throws HttpMessageNotWritableException {
		try {
			Map<String, Object> tokenResponseParameters = this.accessTokenResponseParametersConverter
				.convert(tokenResponse);

			ObjectMapper objectMapper = SpringContextHolder.getBean(ObjectMapper.class);

			// 直接将 Map 转换为 JSON 字符串
			String jsonResponse = objectMapper.writeValueAsString(tokenResponseParameters);

			// 设置响应头
			outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			outputMessage.getHeaders().setContentLength(jsonResponse.getBytes(StandardCharsets.UTF_8).length);

			// 写入响应体
			StreamUtils.copy(jsonResponse, StandardCharsets.UTF_8, outputMessage.getBody());
		}
		catch (Exception ex) {
			throw new HttpMessageNotWritableException(
					"An error occurred writing the OAuth 2.0 Access Token Response: " + ex.getMessage(), ex);
		}
	}

}
