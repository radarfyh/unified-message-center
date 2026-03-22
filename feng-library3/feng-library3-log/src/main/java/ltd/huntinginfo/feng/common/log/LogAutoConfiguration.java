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
package ltd.huntinginfo.feng.common.log;

import ltd.huntinginfo.feng.admin.api.feign.RemoteLogService;
import ltd.huntinginfo.feng.common.log.aspect.SysLogAspect;
import ltd.huntinginfo.feng.common.log.config.FengLogProperties;
import ltd.huntinginfo.feng.common.log.event.SysLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 日志自动配置类，用于配置系统日志相关功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FengLogProperties.class)
@ConditionalOnProperty(value = "security.log.enabled", matchIfMissing = true)
public class LogAutoConfiguration {

	/**
	 * 创建并返回SysLogListener的Bean实例
	 * @param logProperties 日志属性配置
	 * @param remoteLogService 远程日志服务
	 * @return SysLogListener实例
	 */
	@Bean
	public SysLogListener sysLogListener(FengLogProperties logProperties, RemoteLogService remoteLogService) {
		return new SysLogListener(remoteLogService, logProperties);
	}

	/**
	 * 创建并返回SysLogAspect的Bean实例
	 * @return SysLogAspect实例
	 */
	@Bean
	public SysLogAspect sysLogAspect() {
		return new SysLogAspect();
	}

}
