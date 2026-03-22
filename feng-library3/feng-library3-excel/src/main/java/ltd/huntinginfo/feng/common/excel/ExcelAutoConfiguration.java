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
package ltd.huntinginfo.feng.common.excel;

import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.excel.provider.RemoteDictApiService;
import ltd.huntinginfo.feng.common.excel.provider.RemoteDictDataProvider;
import com.pig4cloud.plugin.excel.handler.DictDataProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

/**
 * Excel 自动装配类
 *
 * @author lengleng
 * @date 2025/05/31
 */
@AutoConfiguration
public class ExcelAutoConfiguration {

	/**
	 * 创建远程字典API服务实例
	 * @param restClientBuilderOptional RestClient构建器的可选对象
	 * @return {@link RemoteDictApiService} 远程字典API服务实例
	 */
	@Bean
	@ConditionalOnMissingBean
	public RemoteDictApiService remoteDictApiService(Optional<RestClient.Builder> restClientBuilderOptional) {
		RestClient client = restClientBuilderOptional.orElseGet(RestClient::builder)
			.baseUrl(getBaseUrl())
			.defaultHeader(SecurityConstants.FROM, SecurityConstants.FROM_IN)
			.build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build();
		return factory.createClient(RemoteDictApiService.class);
	}

	/**
	 * 创建字典数据提供程序
	 * @param remoteDictApiService 远程字典API服务
	 * @return 字典数据提供程序实例
	 */
	@Bean
	@ConditionalOnMissingBean
	public DictDataProvider dictDataProvider(RemoteDictApiService remoteDictApiService) {
		return new RemoteDictDataProvider(remoteDictApiService);
	}

	/**
	 * 获取基础URL
	 * @return 根据当前架构模式组装的基础URL字符串
	 */
	private String getBaseUrl() {
		// 根据当前架构模式，组装URL
		if (SpringContextHolder.isMicro()) {
			return String.format("http://%s", ServiceNameConstants.USER3_SERVICE);
		}
		else {
			return String.format("http://%s", SpringContextHolder.getEnvironment()
				.resolvePlaceholders("127.0.0.1:${server.port}${server.servlet.context-path}"));
		}
	}

}
