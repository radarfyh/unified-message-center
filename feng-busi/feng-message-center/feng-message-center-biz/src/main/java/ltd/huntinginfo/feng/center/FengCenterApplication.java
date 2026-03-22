/*
 *      Copyright (c) 2018-2025, radarfyh(Edison.Feng) All rights reserved.
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
 *  Author: radarfyh(Edison.Feng)
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */

package ltd.huntinginfo.feng.center;

import ltd.huntinginfo.feng.common.feign.annotation.EnableFengFeignClients;
import ltd.huntinginfo.feng.common.security.annotation.EnableFengResourceServer;
import ltd.huntinginfo.feng.common.swagger.annotation.EnableFengDoc;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 用户统一管理系统
 *
 * @author edison
 * @date 2026/01/30
 */
@EnableFengDoc(value = "center")
@EnableFengFeignClients
@EnableFengResourceServer
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {
		"ltd.huntinginfo.feng.center", 
		"ltd.huntinginfo.feng.common.rabbitmq.service", 
		"ltd.huntinginfo.feng.common.core.util",
		"ltd.huntinginfo.feng.common.kafka.service"})
public class FengCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(FengCenterApplication.class, args);
	}
	
}
