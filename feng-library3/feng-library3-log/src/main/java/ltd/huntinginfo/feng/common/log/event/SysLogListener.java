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
package ltd.huntinginfo.feng.common.log.event;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.annotation.JsonFilter;
import ltd.huntinginfo.feng.admin.api.entity.SysLog;
import ltd.huntinginfo.feng.admin.api.feign.RemoteLogService;
import ltd.huntinginfo.feng.common.log.config.FengLogProperties;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 系统日志监听器：异步处理系统日志事件
 *
 * @author lengleng
 * @date 2025/05/31
 */
@RequiredArgsConstructor
public class SysLogListener implements InitializingBean {

	// new 一个 避免日志脱敏策略影响全局ObjectMapper
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final RemoteLogService remoteLogService;

	private final FengLogProperties logProperties;

	/**
	 * 异步保存系统日志
	 * @param event 系统日志事件
	 */
	@SneakyThrows
	@Async
	@Order
	@EventListener(SysLogEvent.class)
	public void saveSysLog(SysLogEvent event) {
		SysLogEventSource source = (SysLogEventSource) event.getSource();
		SysLog sysLog = new SysLog();
		BeanUtils.copyProperties(source, sysLog);

		// json 格式刷参数放在异步中处理，提升性能
		if (Objects.nonNull(source.getBody())) {
			String params = objectMapper.writeValueAsString(source.getBody());
			sysLog.setParams(StrUtil.subPre(params, logProperties.getMaxLength()));
		}

		remoteLogService.saveLog(sysLog);
	}

	@Override
	public void afterPropertiesSet() {
		// 为Object类添加PropertyFilterMixIn
		objectMapper.rebuild().addMixIn(Object.class, PropertyFilterMixIn.class);
		// objectMapper.addMixIn(Object.class, PropertyFilterMixIn.class);

		// 获取可忽略的字段名
		String[] ignorableFieldNames = logProperties.getExcludeFields().toArray(new String[0]);

		// 创建并配置FilterProvider - 注意包名变化
		FilterProvider filters = new SimpleFilterProvider().addFilter("filter properties by name",
				SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));

		objectMapper.rebuild().filterProvider(filters);
		// 注册自定义的时间模块
		// objectMapper.registerModule(createCustomTimeModule());
		objectMapper.rebuild().addModule(createCustomTimeModule());
		// 注册自定义模块（包括Long转String等）
		registerCustomSerializers();
	}

	public SimpleModule createCustomTimeModule() {
		SimpleModule module = new SimpleModule("CustomTimeModule");

		// 定义日期时间格式
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		// 注册序列化器
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
		module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
		module.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));

		// 注册反序列化器
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
		module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
		module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

		return module;
	}

	private void registerCustomSerializers() {
		SimpleModule module = new SimpleModule("CustomSerializersModule");

		// Long类型转为String，防止前端JavaScript精度丢失
		module.addSerializer(Long.class, ToStringSerializer.instance);
		module.addSerializer(Long.TYPE, ToStringSerializer.instance);
		module.addSerializer(BigInteger.class, ToStringSerializer.instance);

		// 可以添加其他自定义序列化器
		// module.addSerializer(YourClass.class, new YourCustomSerializer());

		// objectMapper.registerModule(module);
		objectMapper.rebuild().addModule(module);
		// 注册标准的JavaTimeModule（如果不需要自定义格式，可以直接使用这个）
		// objectMapper.registerModule(new JavaTimeModule());
	}

	/**
	 * 属性过滤混合类：用于通过名称过滤属性
	 *
	 * @author lengleng
	 * @date 2025/05/31
	 */
	@JsonFilter("filter properties by name")
	class PropertyFilterMixIn {

	}

}
