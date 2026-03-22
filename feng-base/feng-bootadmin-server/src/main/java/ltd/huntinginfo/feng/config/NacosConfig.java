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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * nacos配置属性打印
 * @author radarfyh
 * @date 2024/12/30
 */
@Component
public class NacosConfig {

    @Autowired
    private ConfigurableEnvironment environment;

    @PostConstruct
    public void validateNacosConnection() {
        printPropertySources();
        
        // 打印配置信息（含解密后的密码）
        getNacosConfigDetails();
        
        getRedisConfigDetails();
        
        // 检测Nacos服务端连通性
        checkNacosServerConnectivity();
    }

    private void getNacosConfigDetails() {
        System.out.println("\n============= Nacos 配置详情 =============");
        System.out.printf("Server Address: %s%n", getServerAddr());
        System.out.printf("Username: %s%n", getUsername());
        System.out.printf("Password: %s%n", getPassword()); 
        System.out.printf("Namespace: %s%n", getNamespace());
        System.out.println("========================================\n");
        

    }
    
    private void getRedisConfigDetails() {
        System.out.println("\n============= Redis 配置详情 =============");
        System.out.printf("Redis Host: %s%n", getRedisHost());
        System.out.printf("Redis Port: %s%n", getRedisPort());
        System.out.printf("Redis Database: %s%n", getRedisDatabase()); 
        System.out.printf("Redis Password: %s%n", getRedisPassword());
        System.out.println("========================================\n");
        
    }

    private void checkNacosServerConnectivity() {
        String serverAddr = getServerAddr();
        if (serverAddr == null || serverAddr.isEmpty()) {
            System.err.println("Nacos 服务端地址未配置!");
            return;
        }

        String[] hosts = serverAddr.split(",");
        for (String host : hosts) {
            String[] parts = host.trim().split(":");
            String ip = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 8848; // 默认8848端口

            System.out.printf("正在检测 Nacos 服务端连通性 [%s:%d] ...%n", ip, port);
            
            // 检测方式1: 基础TCP端口连通性
            boolean isPortReachable = checkTcpPort(ip, port, 5);
            System.out.printf("  - TCP 端口检测: %s%n", isPortReachable ? "✔ 成功" : "✖ 失败");

            // 检测方式2: HTTP API健康检查（更准确）
            if (isPortReachable) {
                boolean isApiHealthy = checkHttpHealth(host, getUsername(), getPassword(), 5);
                System.out.printf("  - HTTP 健康检查: %s%n", isApiHealthy ? "✔ 成功" : "✖ 失败");
            }
        }
    }
    
    private void printPropertySources() {
        System.out.println("\n===== 完整的 PropertySource 列表 =====");
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            String sourceName = propertySource.getName();
            // 打印配置源
            //if (sourceName.contains("nacos") || sourceName.contains("bootstrap")) {
                System.out.printf("### 配置源名称: %s,内容：%s%n", sourceName, propertySource.getSource());
                System.out.println("----------------------------------------");
            //}
        }
    }

    // 获取配置属性（自动处理解密）
    private String getProperty(String key) {
        return environment.getProperty(key);
    }

    private String getServerAddr() {
    	String property = getProperty("spring.cloud.nacos.config.server-addr");
        return property;
    }
    
    private String getUsername() {
    	String property = getProperty("spring.cloud.nacos.username");
        return property;
    }
    
    private String getPassword() {
    	String property = getProperty("spring.cloud.nacos.password");
        return property;
    }
    
    private String getNamespace() {
    	String property = getProperty("spring.cloud.nacos.config.namespace");
        return property;
    }
    
    private String getRedisHost() {
    	String property = getProperty("spring.data.redis.host");
        return property;
    }
    
    private String getRedisPort() {
    	String property = getProperty("spring.data.redis.port");
        return property;
    }
    
    private String getRedisDatabase() {
    	String property = getProperty("spring.data.redis.database");
        return property;
    }
    
    private String getRedisPassword() {
    	String property = getProperty("spring.data.redis.password");
        return property;
    }

    // -------------------- 连通性检测工具方法 --------------------
    private boolean checkTcpPort(String host, int port, int timeoutSeconds) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), (int) TimeUnit.SECONDS.toMillis(timeoutSeconds));
            return true;
        } catch (Exception e) {
            System.err.printf("TCP 连接失败: %s%n", e.getMessage());
            return false;
        }
    }

    private boolean checkHttpHealth(String addr, String name, String password, int timeoutSeconds) {
        String healthUrl = String.format("http://%s/nacos/v1/ns/service/list?pageNo=1&pageSize=2&username=%s&password=%s", addr, name, password);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(healthUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(timeoutSeconds));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(timeoutSeconds));
            
            int responseCode = connection.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            System.err.printf("HTTP 健康检查失败: %s%n", e.getMessage());
            return false;
        }
    }
}
