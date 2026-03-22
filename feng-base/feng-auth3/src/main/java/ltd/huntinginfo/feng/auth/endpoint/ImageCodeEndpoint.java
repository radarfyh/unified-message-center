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
package ltd.huntinginfo.feng.auth.endpoint;

import cn.hutool.core.lang.Validator;
import com.pig4cloud.captcha.ArithmeticCaptcha;
import ltd.huntinginfo.feng.common.core.constant.CacheConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.util.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 验证码相关的接口
 *
 * @author lengleng
 * @date 2022/6/27
 */
@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
@Tag(description = "code", name = "验证码控制器管理模块")
public class ImageCodeEndpoint {

	private static final Integer DEFAULT_IMAGE_WIDTH = 100;

	private static final Integer DEFAULT_IMAGE_HEIGHT = 40;

	/**
	 * 创建图形验证码并输出到响应流
	 * @param randomStr 随机字符串，用于缓存验证码
	 * @param response HTTP响应对象，用于输出验证码图片
	 */
	@SneakyThrows
	@GetMapping("/image")
	@Operation(summary = "创建图形验证码并输出到响应流", description = "创建图形验证码并输出到响应流")
	public void image(String randomStr, HttpServletResponse response) {
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);

		if (Validator.isMobile(randomStr)) {
			return;
		}

		String result = captcha.text();
		RedisUtils.set(CacheConstants.DEFAULT_CODE_KEY + randomStr, result, SecurityConstants.CODE_TIME,
				TimeUnit.SECONDS);
		// 转换流信息写出
		captcha.out(response.getOutputStream());
	}

}
