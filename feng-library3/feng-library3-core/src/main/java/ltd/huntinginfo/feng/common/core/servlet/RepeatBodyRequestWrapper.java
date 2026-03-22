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
 
package ltd.huntinginfo.feng.common.core.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Request包装类：允许body重复读取
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class RepeatBodyRequestWrapper extends HttpServletRequestWrapper {

	private final byte[] bodyByteArray;

	private final Map<String, String[]> parameterMap;

	public RepeatBodyRequestWrapper(HttpServletRequest request) {
		super(request);
		this.bodyByteArray = getByteBody(request);
		// 使用 HashMap 以便后续可以修改
		this.parameterMap = new HashMap<>(request.getParameterMap());
	}

	/**
	 * 获取BufferedReader对象
	 * @return 如果bodyByteArray为空则返回null，否则返回对应的BufferedReader
	 */
	@Override
	public BufferedReader getReader() {
		return ObjectUtils.isEmpty(this.bodyByteArray) ? null
				: new BufferedReader(new InputStreamReader(getInputStream()));
	}

	/**
	 * 获取Servlet输入流
	 * @return ServletInputStream 基于bodyByteArray的输入流
	 */
	@Override
	public ServletInputStream getInputStream() {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.bodyByteArray);
		return new ServletInputStream() {
			@Override
			public boolean isFinished() {
				return byteArrayInputStream.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true; // 可以读取
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// doNothing
			}

			@Override
			public int read() {
				return byteArrayInputStream.read();
			}
		};
	}

	/**
	 * 从HttpServletRequest中获取字节数组形式的请求体
	 * @param request HTTP请求对象
	 * @return 请求体字节数组，解析失败时返回空数组
	 */
	private static byte[] getByteBody(HttpServletRequest request) {
		byte[] body = new byte[0];
		try {
			body = StreamUtils.copyToByteArray(request.getInputStream());
		}
		catch (IOException e) {
			log.error("解析流中数据异常", e);
		}
		return body;
	}

	/**
	 * 获取参数映射表
	 * @return 可变的参数映射表
	 */
	@Override
	public Map<String, String[]> getParameterMap() {
		return this.parameterMap; // 返回可变的 parameterMap
	}

	/**
	 * 设置新的参数映射
	 * @param parameterMap 新的参数映射，将替换现有参数映射
	 */
	public void setParameterMap(Map<String, String[]> parameterMap) {
		this.parameterMap.clear();
		this.parameterMap.putAll(parameterMap);
	}

	/**
	 * 根据参数名获取参数值
	 * @param name 参数名
	 * @return 参数值，如果不存在则返回null
	 */
	@Override
	public String getParameter(String name) {
		String[] values = parameterMap.get(name);
		return (values != null && values.length > 0) ? values[0] : null;
	}

	/**
	 * 根据参数名获取参数值数组
	 * @param name 参数名
	 * @return 参数值数组，如果不存在则返回null
	 */
	@Override
	public String[] getParameterValues(String name) {
		return parameterMap.get(name);
	}

}
