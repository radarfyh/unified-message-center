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
package ltd.huntinginfo.feng.indicator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import ltd.huntinginfo.feng.config.EmbeddedRedisConfig;

/**
 * redis健康检查
 * @author radarfyh
 * @date 2024/12/30
 */
@Component
@ConditionalOnProperty(name = "redis.embedded.enabled", havingValue = "true")
public class RedisHealthIndicator implements HealthIndicator {
    
    private final EmbeddedRedisConfig embeddedRedisConfig;
    private final JedisPool jedisPool;

    public RedisHealthIndicator(EmbeddedRedisConfig embeddedRedisConfig, JedisPool jedisPool) {
        this.embeddedRedisConfig = embeddedRedisConfig;
        this.jedisPool = jedisPool;
    }

    @Override
    public Health health() {
        // 每次从 EmbeddedRedisConfig 获取最新 RedisServer 实例（可能为 null）
        var redisServer = embeddedRedisConfig.getRedisServer();
        if (redisServer == null || !redisServer.isActive()) {
            return Health.down()
                .withDetail("reason", "Redis process not running")
                .build();
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            String ping = jedis.ping();
            if ("PONG".equals(ping)) {
                return Health.up()
                    .withDetail("response", ping)
                    .withDetail("port", redisServer.ports().get(0))
                    .build();
            } else {
                return Health.down()
                    .withDetail("response", ping)
                    .withDetail("port", redisServer.ports().get(0))
                    .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                .withDetail("port", redisServer.ports().get(0))
                .build();
        }
    }
}