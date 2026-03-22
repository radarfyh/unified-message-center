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

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 连接池
 * @author radarfyh
 * @date 2024/12/30
 */
@Configuration
public class JedisConfig {
    
    @Value("${redis.embedded.port:6379}")
    private int port;
    
    @Value("${redis.embedded.password:}")
    private String password;
    
    @Bean
    @ConditionalOnProperty(name = "redis.embedded.enabled", havingValue = "true")
    @DependsOn("embeddedRedisConfig")
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setJmxEnabled(false); // 禁用 JMX 注册，避免冲突
        
        // 禁用空闲连接验证，避免关闭时验证已断开的连接
        poolConfig.setTestWhileIdle(false);
        
        // 彻底关闭 evictor 线程
        Integer timeBetweenEvictionRunsMillis = -1;
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(timeBetweenEvictionRunsMillis)); 
        
        if (password != null && !password.isEmpty()) {
            return new JedisPool(poolConfig, "localhost", port, 3000, password);
        }
        return new JedisPool(poolConfig, "localhost", port, 3000);
    }
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}
