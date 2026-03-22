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
package ltd.huntinginfo.feng.gateway.config;

import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.alibaba.nacos.common.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * SpringDoc配置类，实现InitializingBean接口，用于Swagger 3.0文档展示
 *
 * @author lengleng
 * @date 2025/05/30
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "springdoc.api-docs.enabled", matchIfMissing = true)
public class SpringDocConfiguration implements InitializingBean {

	private final SwaggerUiConfigProperties swaggerUiConfigProperties;

	private final DiscoveryClient discoveryClient;

	/**
	 * 在初始化后调用的方法，用于注册SwaggerDocRegister订阅器
	 */
	@Override
	public void afterPropertiesSet() {
		NotifyCenter.registerSubscriber(new SwaggerDocRegister(swaggerUiConfigProperties, discoveryClient));
	}

}

/**
 * Swagger文档注册器，用于处理服务实例变更事件并更新Swagger UI配置
 *
 * @author lengleng
 * @date 2025/05/30
 */
@RequiredArgsConstructor
class SwaggerDocRegister extends Subscriber<InstancesChangeEvent> {

	private final SwaggerUiConfigProperties swaggerUiConfigProperties;

	private final DiscoveryClient discoveryClient;

	/**
	 * 处理服务实例变更事件
	 * @param event 服务实例变更事件对象
	 */
	@Override
	public void onEvent(InstancesChangeEvent event) {
		Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrlSet = discoveryClient.getServices()
			.stream()
			.flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
			.filter(instance -> StringUtils.isNotBlank(instance.getMetadata().get("spring-doc")))
			.map(instance -> {
				AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
				swaggerUrl.setName(instance.getServiceId());
				swaggerUrl.setUrl(String.format("/%s/v3/api-docs", instance.getMetadata().get("spring-doc")));
				return swaggerUrl;
			})
			.collect(Collectors.toSet());

		swaggerUiConfigProperties.setUrls(swaggerUrlSet);
	}

	/**
	 * 订阅类型方法，返回订阅的事件类型
	 * @return 订阅的事件类型
	 */
	@Override
	public Class<? extends Event> subscribeType() {
		return InstancesChangeEvent.class;
	}

}
