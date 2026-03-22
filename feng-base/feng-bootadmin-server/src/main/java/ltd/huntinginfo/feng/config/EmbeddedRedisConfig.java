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
package ltd.huntinginfo.feng.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;
import redis.embedded.core.RedisServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

/**
 * redis启动和停止
 * @author radarfyh
 * @date 2024/12/30
 */
@Configuration
@ConditionalOnProperty(name = "redis.embedded.enabled", havingValue = "true")
@Slf4j
public class EmbeddedRedisConfig {

    private final EmbeddedRedisProperties properties;
    private RedisServer redisServer;
    private final Object lock = new Object();

    public EmbeddedRedisConfig(EmbeddedRedisProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void startRedis() throws IOException {
        if (!properties.getEnabled()) {
            return;
        }
        synchronized (lock) {
            if (redisServer != null && redisServer.isActive()) {
                log.info("Embedded Redis already running on port {}", properties.getPort());
                return;
            }

            int maxAttempts = 3;
            int attempt = 0;
            while (attempt < maxAttempts) {
                try {
                    if (redisServer != null) {
                        try {
                            redisServer.stop();
                        } catch (Exception ignored) {}
                    }
                    createAndStartRedisServer();
                    log.info("Embedded Redis server started successfully on port {}", properties.getPort());
                    return;
                } catch (RuntimeException e) {
                    attempt++;
                    if (attempt >= maxAttempts) {
                        throw new IllegalStateException(
                            "Failed to start embedded Redis server after " + maxAttempts + " attempts", e);
                    }
                    log.warn("Failed to start Redis on port {}, attempt {}/{}, retrying...",
                            properties.getPort(), attempt, maxAttempts);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Interrupted while retrying", ie);
                    }
                }
            }
        }
    }

    private void createAndStartRedisServer() throws IOException {
    	// 注意： 不支持远程连接，只能绑定到127.0.0.1
        RedisServerBuilder builder = RedisServer.newRedisServer()
                .port(properties.getPort())
                .setting("maxmemory " + properties.getMaxMemory());  
        
        log.info("maxmemory: {}", properties.getMaxMemory());

        String os = System.getProperty("os.name").toLowerCase();
        String bindIp = StrUtil.isNotBlank(properties.getIp()) ? properties.getIp() : "127.0.0.1";
        
        if (os.contains("win")) {
            builder.setting("bind 127.0.0.1");
            log.info("Windows OS detected, using bind 127.0.0.1");
        } else {
            builder.setting("bind " + bindIp);
            log.info("bind " + bindIp);
        }

        if (properties.getRequirepass() && properties.getPassword() != null) {
            builder.setting("requirepass " + properties.getPassword());
            log.info("requirepass " + properties.getPassword());
        }
        
        if (properties.getProtectedMode() != null && !properties.getProtectedMode() ) {
        	builder.setting("protected-mode no");
        	log.info("protected-mode no");
        } else {
        	builder.setting("protected-mode yes");
        	log.info("protected-mode yes");
        }
        
//        if (StrUtil.isNotBlank(properties.getConfigFile())) {
//        	builder.configFile(properties.getConfigFile());
//        	log.info("configFile: {}", properties.getConfigFile());
//        }

        this.redisServer = builder.build();
        this.redisServer.start();        
    }

    @PreDestroy
    public void stopRedis() {
        synchronized (lock) {
            if (redisServer != null && redisServer.isActive()) {
                try {
                    redisServer.stop();
                    log.info("Embedded Redis server stopped");
                } catch (Exception e) {
                    log.error("Failed to stop embedded Redis server", e);
                }
            }
        }
    }

    public RedisServer getRedisServer() {
        return redisServer;
    }
}