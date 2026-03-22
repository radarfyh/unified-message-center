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
package ltd.huntinginfo.feng.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.RedisServer;
import ltd.huntinginfo.feng.config.EmbeddedRedisConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * rendis启动、停止、状态查询等操作端点
 * @author radarfyh
 * @date 2024/12/30
 */
@Component
@Endpoint(id = "redis")
public class FengRedisEndpoint {

    private final EmbeddedRedisConfig embeddedRedisConfig;
    private final JedisPool jedisPool;

    public FengRedisEndpoint(EmbeddedRedisConfig embeddedRedisConfig, JedisPool jedisPool) {
        this.embeddedRedisConfig = embeddedRedisConfig;
        this.jedisPool = jedisPool;
    }

    @ReadOperation
    public Map<String, Object> status() {
        RedisServer redisServer = embeddedRedisConfig.getRedisServer();
        boolean active = redisServer != null && redisServer.isActive();
        return Map.of(
                "status", active ? "UP" : "DOWN",
                "details", Map.of(
                    "port", active ? redisServer.ports().get(0) : "N/A",
                    "active", active
                )
            );
    }

    @WriteOperation
    public Map<String, String> execute(@Selector String action) throws IOException {
        RedisServer redisServer = embeddedRedisConfig.getRedisServer();
        Map<String, String> response = new HashMap<>();
        try {
            switch (action.toLowerCase()) {
                case "start":
                    if (redisServer != null && !redisServer.isActive()) {
                        redisServer.start();
                    }
                    response.put("status", "STARTED");
                    break;
                case "stop":
                    if (redisServer != null && redisServer.isActive()) {
                        redisServer.stop();
                    }
                    response.put("status", "STOPPED");
                    break;
                default:
                    throw new IllegalArgumentException("无效指令，请使用 start 或 stop");
            }
            response.put("success", "true");
        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("success", "false");
        }
        return response;
    }

    private Jedis createJedisConnection() {
        return jedisPool.getResource();
    }
}