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

import ltd.huntinginfo.feng.common.swagger.annotation.EnableFengDoc;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;
import java.util.Objects;

/**
 * OpenAPI 配置类，用于动态注册 OpenAPI 相关 Bean 定义
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class OpenAPIDefinitionImportSelector implements ImportBeanDefinitionRegistrar {

	/**
	 * 注册Bean定义，根据注解元数据配置OpenAPI相关Bean
	 * @param metadata 注解元数据
	 * @param registry Bean定义注册器
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableFengDoc.class.getName(), true);
		Object value = annotationAttributes.get("value");
		if (Objects.isNull(value)) {
			return;
		}

		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(OpenAPIDefinition.class);
		definition.addPropertyValue("path", value);
		definition.setPrimary(true);

		registry.registerBeanDefinition("openAPIDefinition", definition.getBeanDefinition());

		// 如果是微服务架构则，引入了服务发现声明相关的元数据配置
		Object isMicro = annotationAttributes.getOrDefault("isMicro", true);
		if (isMicro.equals(false)) {
			return;
		}

		BeanDefinitionBuilder openAPIMetadata = BeanDefinitionBuilder
			.genericBeanDefinition(OpenAPIMetadataConfiguration.class);
		openAPIMetadata.addPropertyValue("path", value);
		registry.registerBeanDefinition("openAPIMetadata", openAPIMetadata.getBeanDefinition());
	}

}
