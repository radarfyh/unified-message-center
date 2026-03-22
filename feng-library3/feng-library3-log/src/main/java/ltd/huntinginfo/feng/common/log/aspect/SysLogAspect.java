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
package ltd.huntinginfo.feng.common.log.aspect;

import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.common.core.util.SpringContextHolder;
import ltd.huntinginfo.feng.common.log.event.SysLogEvent;
import ltd.huntinginfo.feng.common.log.event.SysLogEventSource;
import ltd.huntinginfo.feng.common.log.util.LogTypeEnum;
import ltd.huntinginfo.feng.common.log.util.SysLogUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;

/**
 * 系统日志切面类，通过Spring AOP实现操作日志的异步记录
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class SysLogAspect {

	/**
	 * 环绕通知方法，用于处理系统日志记录
	 * @param point 连接点对象
	 * @param sysLog 系统日志注解
	 * @return 目标方法执行结果
	 * @throws Throwable 目标方法执行可能抛出的异常
	 */
	@Around("@annotation(sysLog)")
	@SneakyThrows
	public Object around(ProceedingJoinPoint point, ltd.huntinginfo.feng.common.log.annotation.SysLog sysLog) {
		String strClassName = point.getTarget().getClass().getName();
		String strMethodName = point.getSignature().getName();
		log.debug("[类名]:{},[方法]:{}", strClassName, strMethodName);

		String value = sysLog.value();
		String expression = sysLog.expression();
		// 当前表达式存在 SPEL，会覆盖 value 的值
		if (StrUtil.isNotBlank(expression)) {
			// 解析SPEL
			MethodSignature signature = (MethodSignature) point.getSignature();
			EvaluationContext context = SysLogUtils.getContext(point.getArgs(), signature.getMethod());
			try {
				value = SysLogUtils.getValue(context, expression, String.class);
			}
			catch (Exception e) {
				// SPEL 表达式异常，获取 value 的值
				log.error("@SysLog 解析SPEL {} 异常", expression);
			}
		}

		SysLogEventSource logVo = SysLogUtils.getSysLog();
		logVo.setTitle(value);
		// 获取请求body参数 下句可能会报错：It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
//		if (StrUtil.isBlank(logVo.getParams())) {
//			logVo.setBody(point.getArgs());
//		}
//		if (StrUtil.isBlank(logVo.getParams())) {
//		    // 过滤掉不可序列化的参数（例如 HttpServletRequest）
//		    Object[] args = point.getArgs();
//		    if (args != null) {
//		        // 只保留可序列化的参数（或转换为字符串）
//		        List<Object> serializableArgs = Arrays.stream(args)
//		                .filter(arg -> !(arg instanceof HttpServletRequest) 
//		                        && !(arg instanceof HttpServletResponse)
//		                        && !(arg instanceof MultipartFile))
//		                .collect(Collectors.toList());
//		        if (!serializableArgs.isEmpty()) {
//		            logVo.setBody(serializableArgs.toArray());
//		        } else {
//		            logVo.setBody(null); // 或无参数可记录
//		        }
//		    }
//		}
		
		// 发送异步日志事件
		Long startTime = System.currentTimeMillis();
		Object obj;

		try {
			obj = point.proceed();
		}
		catch (Exception e) {
			logVo.setLogType(LogTypeEnum.ERROR.getType());
			logVo.setException(e.getMessage());
			throw e;
		}
		finally {
			Long endTime = System.currentTimeMillis();
			logVo.setTime(endTime - startTime);
			SpringContextHolder.publishEvent(new SysLogEvent(logVo));
		}

		return obj;
	}

}
