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
package ltd.huntinginfo.feng.common.swagger.config;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * OpenAPI 元数据配置类，用于配置并注册OpenAPI相关元数据
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class OpenAPIMetadataConfiguration implements InitializingBean, ApplicationContextAware {

	/**
	 * 应用上下文
	 */
	private ApplicationContext applicationContext;

	@Setter
	private String path;

	/**
	 * 在属性设置完成后执行，将spring-doc路径信息注册到ServiceInstance的元数据中
	 * @throws Exception 如果执行过程中发生错误
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		String[] beanNamesForType = applicationContext.getBeanNamesForType(ServiceInstance.class);

		if (beanNamesForType.length == 0) {
			return;
		}

		ServiceInstance serviceInstance = applicationContext.getBean(ServiceInstance.class);
		serviceInstance.getMetadata().put("spring-doc", path);
	}

	/**
	 * 设置应用上下文
	 * @param applicationContext 应用上下文对象
	 * @throws BeansException 如果设置上下文时发生错误
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
