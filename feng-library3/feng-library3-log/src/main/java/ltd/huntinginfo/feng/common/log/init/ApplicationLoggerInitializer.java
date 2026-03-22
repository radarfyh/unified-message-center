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
package ltd.huntinginfo.feng.common.log.init;

import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 应用日志初始化类：通过环境变量注入 logging.file 自动维护 Spring Boot Admin Logger Viewer
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class ApplicationLoggerInitializer implements EnvironmentPostProcessor, Ordered {

	/**
	 * 后处理环境配置，设置日志路径和相关系统属性
	 * @param environment 可配置的环境对象
	 * @param application Spring应用对象
	 */
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		String appName = environment.getProperty("spring.application.name");
		String logBase = environment.getProperty("LOGGING_PATH", "logs");

		// spring boot admin 直接加载日志
		System.setProperty("logging.file.name", String.format("%s/%s/debug.log", logBase, appName));

		// 避免各种依赖的地方组件造成 BeanPostProcessorChecker 警告
		// System.setProperty("logging.level.org.springframework.context.support.PostProcessorRegistrationDelegate","ERROR");

		// 避免 sentinel 1.8.4+ 心跳日志过大
		// System.setProperty("csp.sentinel.log.level", "OFF");

		// 避免 sentinel 健康检查 server
		System.setProperty("management.health.sentinel.enabled", "false");
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
