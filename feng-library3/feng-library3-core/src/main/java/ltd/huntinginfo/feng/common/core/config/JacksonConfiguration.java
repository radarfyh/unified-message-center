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

package ltd.huntinginfo.feng.common.core.config;

import ltd.huntinginfo.feng.common.core.jackson.LongToStringModule;
import ltd.huntinginfo.feng.common.core.jackson.FengJavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Jackson配置类，用于自定义Jackson的ObjectMapper配置
 *
 * @author lengleng
 * @author L.cm
 * @author lishangbu
 * @date 2025/05/30
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class JacksonConfiguration {

	/**
	 * 自定义JsonMapperBuilder配置 (Jackson 3)
	 * @return JsonMapperBuilderCustomizer实例，包含以下配置： 1. 设置地区为中国 2. 设置系统默认时区 3. 配置Long类型序列化为字符串
	 * 4. 注册自定义时间模块
	 */
	@Bean
	@ConditionalOnMissingBean
	public JsonMapperBuilderCustomizer customizer() {
		return builder -> builder.defaultLocale(Locale.CHINA)
			.defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
			.addModule(new FengJavaTimeModule())
			.addModule(new LongToStringModule());
	}

}
