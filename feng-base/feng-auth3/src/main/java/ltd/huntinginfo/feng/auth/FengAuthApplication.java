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
package ltd.huntinginfo.feng.auth;

import ltd.huntinginfo.feng.common.feign.annotation.EnableFengFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权中心应用启动类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@EnableFengFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class FengAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(FengAuthApplication.class, args);
//		System.out.println("Default Charset: " + java.nio.charset.Charset.defaultCharset());
//		System.out.println("file.encoding: " + System.getProperty("file.encoding"));
	}

}
