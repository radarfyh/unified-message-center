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

package ltd.huntinginfo.feng.common.feign;

import org.springframework.cloud.openfeign.FengFeignClientsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ltd.huntinginfo.feng.common.feign.core.FengFeignInnerRequestInterceptor;
import ltd.huntinginfo.feng.common.feign.core.FengFeignRequestCloseInterceptor;

/**
 * Sentinel Feign 自动配置类
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Configuration(proxyBeanMethods = false)
@Import(FengFeignClientsRegistrar.class)
public class FengFeignAutoConfiguration {

	/**
	 * 创建并返回FengFeignRequestCloseInterceptor实例
	 * @return FengFeignRequestCloseInterceptor实例
	 */
	@Bean
	public FengFeignRequestCloseInterceptor fengFeignRequestCloseInterceptor() {
		return new FengFeignRequestCloseInterceptor();
	}

	/**
	 * 创建并返回FengFeignInnerRequestInterceptor实例
	 * @return FengFeignInnerRequestInterceptor 内部请求拦截器实例
	 */
	@Bean
	public FengFeignInnerRequestInterceptor fengFeignInnerRequestInterceptor() {
		return new FengFeignInnerRequestInterceptor();
	}

}
