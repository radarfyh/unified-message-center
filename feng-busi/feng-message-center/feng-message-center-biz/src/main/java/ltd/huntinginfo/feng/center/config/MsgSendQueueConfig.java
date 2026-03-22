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
@ConfigurationProperties(prefix = "msg.queue")
public class MsgSendQueueConfig {
    
    /**
     * 是否启用队列服务
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
     * 并发线程数
     */
    private int concurrentThreads = 3;
    
    /**
     * 批量处理大小
     */
    private int batchSize = 20;
    
    /**
     * 处理间隔（秒）
     */
    private int processInterval = 30;
    
    /**
     * 最大处理时间（秒）
     */
    private int maxProcessTime = 300;
    
    /**
     * 初始延迟（秒）
     */
    private int initialDelay = 0;
    
    /**
     * 线程池核心大小
     */
    private int corePoolSize = 3;
    
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
     * 任务最大重试次数
     */
    private int maxRetry = 3;
    
    /**
     * 任务重试基础延迟（秒）
     */
    private int retryBaseDelay = 5;
    
    /**
     * 任务重试最大延迟（秒）
     */
    private int retryMaxDelay = 300;
    
    /**
     * 是否启用健康检查
     */
    private boolean healthCheckEnabled = true;
    
    /**
     * 健康检查间隔（分钟）
     */
    private int healthCheckInterval = 5;
    
    /**
     * 是否记录详细日志
     */
    private boolean detailedLogging = false;
    
    /**
     * 监控统计间隔（秒）
     */
    private int monitorInterval = 60;
    
    /**
     * 是否自动清理已完成任务
     */
    private boolean autoCleanCompleted = true;
    
    /**
     * 已完成任务保留天数
     */
    private int completedRetentionDays = 7;
    
    /**
     * 是否启用异步处理
     */
    private boolean asyncProcessing = true;
    
    /**
     * 是否启用任务优先级
     */
    private boolean enablePriority = true;
    
    /**
     * 高优先级任务优先处理阈值
     */
    private int highPriorityThreshold = 3;
    
    /**
     * 是否启用任务去重
     */
    private boolean enableDeduplication = true;
    
    /**
     * 去重缓存时间（分钟）
     */
    private int deduplicationCacheMinutes = 30;
}