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
package ltd.huntinginfo.feng.center.service.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.service.processor.MessageQueueTaskProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消息队列定时任务调度器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageQueueSchedule {

    private final MessageQueueTaskProcessor messageQueueTaskProcessor;

    /**
     * 处理分发队列任务（每60秒执行一次）
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    public void processDistributeTasks() {
        try {
        	log.debug("开始处理分发重试任务...");
            messageQueueTaskProcessor.processDistributeRetryTasks(50);
            log.debug("...结束处理分发重试任务");
        } catch (Exception e) {
            log.error("处理分发队列任务失败", e);
        }
    }

    /**
     * 处理推送队列任务（每3分钟执行一次）
     */
    @Scheduled(fixedDelay = 180000, initialDelay = 10000)
    public void processPushTasks() {
        try {
        	log.debug("开始处理回调/推送重试任务...");
            messageQueueTaskProcessor.processPushRetryTasks(100);
            log.debug("...结束处理回调/推送重试任务");
        } catch (Exception e) {
            log.error("处理回调/推送队列任务失败", e);
        }
    }

    /**
     * 清理过期消息（每11分钟执行一次）
     */
    @Scheduled(cron = "0 */11 * * * ?")
    public void cleanExpiredMessages() {
        try {
            log.debug("开始清理过期消息...");
            int cleanedCount = messageQueueTaskProcessor.processExpiredMessages();
            log.debug("已清理{}条过期消息", cleanedCount);
        } catch (Exception e) {
            log.error("清理过期消息任务失败", e);
        }
    }
    
    /**
     * 处理统计任务（每天凌晨3点执行）
     */
    @Scheduled(cron = "0 */12 * * * ?")
    public void processStatisticsTasks() {
        try {
            log.debug("开始处理统计任务...");
            Boolean success = messageQueueTaskProcessor.processStatisticsMessages();
            log.debug("已完成统计任务", success);
        } catch (Exception e) {
            log.error("统计任务失败", e);
        }
    }
}