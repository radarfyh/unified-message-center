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
package ltd.huntinginfo.feng.center.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "msg.polling")
public class MessagePollingConfig {
    
    /**
     * 是否启用轮询服务
     */
    private boolean enabled = true;
    
    /**
     * 轮询主题
     */
    private String topic = "fz_001800";
    
    /**
     * 消息类型
     */
    private String msgType = "001800";
    
    /**
     * 游标键
     */
    private String cursorKey = "DEFAULT";
    
    /**
     * 轮询间隔（秒）
     */
    private int intervalSeconds = 10;
    
    /**
     * 批量处理大小
     */
    private int batchSize = 20;
    
    /**
     * 最大重试次数
     */
    private int maxRetry = 3;
    
    /**
     * 最大并发轮询数
     */
    private int maxConcurrentPolling = 5;
    
    /**
     * 并发线程数
     */
    private int concurrentThreads = 5;
    
    /**
     * 最大错误次数（超过此数暂停轮询）
     */
    private int maxErrorCount = 10;
    
    /**
     * 是否启用健康检查
     */
    private boolean healthCheckEnabled = true;
    
    /**
     * 健康检查间隔（分钟）
     */
    private int healthCheckInterval = 5;
    
    /**
     * 初始延迟（秒）
     */
    private int initialDelay = 0;
    
    /**
     * 线程池核心大小
     */
    private int corePoolSize = 5;
    
    /**
     * 线程池最大大小
     */
    private int maxPoolSize = 10;
    
    /**
     * 线程池队列容量
     */
    private int queueCapacity = 100;
    
    /**
     * 线程池空闲线程存活时间（秒）
     */
    private int keepAliveSeconds = 60;
    
    /**
     * 回调超时时间（毫秒）
     */
    private int callbackTimeout = 5000;
    
    /**
     * 回调最大重试次数
     */
    private int callbackMaxRetry = 3;
    
    /**
     * 回调重试间隔（毫秒）
     */
    private int callbackRetryInterval = 1000;
    
    /**
     * 是否启用批量回调
     */
    private boolean batchCallbackEnabled = true;
    
    /**
     * 批量回调大小
     */
    private int batchCallbackSize = 10;
    
    /**
     * 是否记录详细日志
     */
    private boolean detailedLogging = false;
    
    /**
     * 监控统计间隔（秒）
     */
    private int monitorInterval = 60;
    
    /**
     * 是否自动重置错误过多的游标
     */
    private boolean autoResetCursor = true;
    
    /**
     * 自动重置错误阈值
     */
    private int autoResetErrorThreshold = 20;
    
    /**
     * 是否启用异步处理
     */
    private boolean asyncProcessing = true;
    
    /**
     * 异步处理超时时间（秒）
     */
    private int asyncProcessingTimeout = 30;
}