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
package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.dto.MsgQueueQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueuePageVO;
import ltd.huntinginfo.feng.center.api.vo.MsgQueueStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息队列表服务接口
 * 主要业务逻辑如下，涉及多个表，实际上应该指其对应的后台服务实现：
 * 1.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=PUSH，ump_msg_main.receiver_type=USER,
 *     则ump_msg_queue.queue_type=CALLBACK，存收件箱ump_msg_inbox，调用APP回调地址ump_msg_main.callback_url/ump_app_credential.callback_url，记录发送日志ump_system_log
 * 2.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=PUSH，ump_msg_main.receiver_type=DEPT,
 *     则ump_msg_queue.queue_type=CALLBACK，存广播信息筒ump_msg_broadcast，调用APP回调地址ump_msg_main.callback_url/ump_app_credential.callback_url，记录发送日志ump_system_log
 * 3.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=PUSH，ump_msg_main.receiver_type=ORG,
 *     则ump_msg_queue.queue_type=CALLBACK，存广播信息筒ump_msg_broadcast，调用APP回调地址ump_msg_main.callback_url/ump_app_credential.callback_url，记录发送日志ump_system_log
 * 4.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=PUSH，ump_msg_main.receiver_type=AREA,
 *     则ump_msg_queue.queue_type=CALLBACK，存广播信息筒ump_msg_broadcast，调用APP回调地址ump_msg_main.callback_url/ump_app_credential.callback_url，记录发送日志ump_system_log
 * 5.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=PUSH，ump_msg_main.receiver_type=ALL,
 *     则ump_msg_queue.queue_type=CALLBACK，存广播信息筒ump_msg_broadcast，调用APP回调地址ump_msg_main.callback_url/ump_app_credential.callback_url，记录发送日志ump_system_log
 * 6.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=POLL，ump_msg_main.receiver_type=USER,
 *     则ump_msg_queue.queue_type=SEND，存收件箱ump_msg_inbo，记录发送日志ump_system_log，等待APP调用查询接口
 * 7.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=POLL，ump_msg_main.receiver_type=DEPT,
 *     则ump_msg_queue.queue_type=DISTRIBUTE，存收广播信息筒ump_msg_broadcast，记录发送日志ump_system_log，等待APP调用查询接口
 * 8.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=POLL，ump_msg_main.receiver_type=ORG,
 *     则ump_msg_queue.queue_type=DISTRIBUTE，存收广播信息筒ump_msg_broadcast，记录发送日志ump_system_log，等待APP调用查询接口
 * 9.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=POLL，ump_msg_main.receiver_type=AREA,
 *     则ump_msg_queue.queue_type=DISTRIBUTE，存收广播信息筒ump_msg_broadcast，记录发送日志ump_system_log，等待APP调用查询接口
 * 10.若ump_msg_main.push_mode/ump_app_credential.default_push_mode=POLL，ump_msg_main.receiver_type=ALL,
 *     则ump_msg_queue.queue_type=DISTRIBUTE, 存收广播信息筒ump_msg_broadcast，记录发送日志ump_system_log，等待APP调用查询接口
 * 11. APP上报已接收（receive_status）和已读（read_status）后表示信息已完结，要写ump_msg_queue.end_time
 *     如果接收失败（ump_msg_inbox.receive_status=FAILED）、广播送达失败（ump_broadcast_receive_record.receive_status=FAILED）
 *         、执行失败（ump_msg_queue.status=FAILED）、发送失败（ump_msg_callback.status=FAILED)，重试直到达到ump_msg_queue.max_retry次数才写ump_msg_queue.end_time，
 *     同时，要及时更新时间、错误信息、状态信息、统计信息等，例如：ump_msg_inbox.distribute_time/error_message/push_count/last_push_time/push_status
 * 12.已完成的任务不从ump_msg_queue中删除，但要定期归档（超过3个月进入历史表，超过1年进入归档表）
 * 13.已经处理完的消息不从ump_msg_main中删除，但要定期归档（超过3个月进入历史表，超过1年进入归档表）
 * 14.已经完结的点对点消息不从ump_msg_inbox中删除，但要定期归档（超过3个月进入历史表，超过1年进入归档表）
 * 15.已经完结的广播消息不从ump_msg_broadcast中删除，但要定期归档（超过3个月进入历史表，超过1年进入归档表）
 * 16.APP上报已读状态时写记录到ump_broadcast_receive_record，不删除该表，但要定期归档（超过3个月进入历史表，超过1年进入归档表）
 * 17.ump_msg_topic和ump_topic_subscription暂时不处理，使用ump_msg_queue作为任务中心
 * 18.ump_msg_inbox.distribute_mode调整为：发送方式:PUSH-推送 POLL-轮询，ump_msg_inbox.distribute_time调整为：发送时间
 */
public interface UmpMsgQueueService extends IService<UmpMsgQueue> {

    /**
     * 创建队列任务
     *
     * @param queueType 队列类型
     * @param queueName 队列名称
     * @param msgId 消息ID
     * @param taskData 任务数据
     * @param priority 优先级
     * @param executeTime 执行时间
     * @param maxRetry 最大重试次数
     * @return 任务ID
     */
    String createQueueTask(String queueType, String queueName, String msgId,
    		TaskData taskData, Integer priority,
            LocalDateTime executeTime, Integer maxRetry);

    /**
     * 批量创建队列任务
     *
     * @param tasks 任务列表
     * @return 成功创建数量
     */
    int batchCreateQueueTasks(List<UmpMsgQueue> tasks);

    /**
     * 根据消息ID查询队列任务
     *
     * @param msgId 消息ID
     * @return 队列任务列表
     */
    List<MsgQueueDetailVO> getQueueTasksByMsgId(String msgId);

    /**
     * 分页查询队列任务
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<MsgQueuePageVO> queryQueuePage(MsgQueueQueryDTO queryDTO);

    /**
     * 获取待执行的任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param limit 限制数量
     * @return 待执行任务列表
     */
    List<MsgQueueDetailVO> getPendingTasks(String queueType, String queueName, int limit);
    /**
     * 根据队列类型获取待执行的任务（状态 PENDING，执行时间 ≤ 当前时间）
     * @param queueType 队列类型（DISTRIBUTE / PUSH）
     * @param limit 最大获取数量
     * @return 任务列表
     */
    List<MsgQueueDetailVO> getPendingTasksByType(String queueType, int limit);

    /**
     * 根据队列类型获取待执行的重试任务（状态 PENDING，当前重试次数 > 0，执行时间 ≤ 当前时间）
     * @param queueType 队列类型（DISTRIBUTE / PUSH）
     * @param limit 最大获取数量
     * @return 任务列表
     */
    List<MsgQueueDetailVO> getRetryPendingTasksByType(String queueType, int limit);
    
    /**
     * 获取可重试的任务（状态 FAILED，当前重试次数 < 最大重试次数，执行时间 ≤ 当前时间）
     * @param limit 最大获取数量
     * @return 任务列表
     */
    List<MsgQueueDetailVO> getRetryableTasks(int limit);
    
    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return 任务详情VO
     */
    MsgQueueDetailVO getQueueTaskDetail(String taskId);

    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param status 状态
     * @param workerId 工作者ID
     * @param resultCode 结果代码（可选）
     * @param resultMessage 结果消息（可选）
     * @param errorStack 错误堆栈（可选）
     * @return 是否成功
     */
    boolean updateTaskStatus(String taskId, String status, String workerId,
                            String resultCode, String resultMessage, String errorStack);

    /**
     * 标记任务为处理中
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @return 是否成功
     */
    boolean markAsProcessing(String taskId, String workerId);
    
    /**
     * 标记任务为重试中
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @return 是否成功
     */
    boolean markAsRetrying(String taskId, String workerId);

    /**
     * 标记任务为成功
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param resultMessage 结果消息（可选）
     * @return 是否成功
     */
    boolean markAsSuccess(String taskId, String workerId, String resultMessage);

    /**
     * 标记任务为重试成功
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param resultMessage 结果消息（可选）
     * @return 是否成功
     */
    boolean markAsRetrySuccess(String taskId, String workerId, String resultMessage);
    
    /**
     * 标记任务为失败
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param errorMessage 错误消息
     * @param errorStack 错误堆栈（可选）
     * @return 是否成功
     */
    boolean markAsFailed(String taskId, String workerId, String errorMessage, String errorStack);
    
    /**
     * 标记任务为重试失败
     *
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param errorMessage 错误消息
     * @param errorStack 错误堆栈（可选）
     * @return 是否成功
     */
    boolean markAsRetryFailed(String taskId, String workerId, String errorMessage, String errorStack);

    /**
     * 重试失败任务
     *
     * @param taskId 任务ID
     * @param retryDelayMinutes 重试延迟分钟数
     * @return 是否成功
     */
    boolean retryFailedTask(String taskId, int retryDelayMinutes);

    /**
     * 批量重试失败任务
     *
     * @param taskIds 任务ID列表
     * @param retryDelayMinutes 重试延迟分钟数
     * @return 成功重试数量
     */
    int batchRetryFailedTasks(List<String> taskIds, int retryDelayMinutes);

    /**
     * 获取队列统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param queueType 队列类型（可选）
     * @return 统计信息VO
     */
    MsgQueueStatisticsVO getQueueStatistics(LocalDateTime startTime,
                                           LocalDateTime endTime,
                                           String queueType);

    /**
     * 删除队列任务
     *
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean deleteQueueTask(String taskId);

    /**
     * 根据消息ID删除队列任务
     *
     * @param msgId 消息ID
     * @return 删除的任务数量
     */
    long deleteByMsgId(String msgId);
    
    /**
     * 将任务重置为待处理状态（清除执行现场，状态改为 PENDING）
     * @param taskId 任务ID
     * @return 是否成功
     */
    boolean resetToPending(String taskId);
    
    /**
     * 标记任务失败，并根据重试策略设置下次执行时间
     * @param taskId 任务ID
     * @param workerId 工作者ID
     * @param errorMessage 错误信息
     * @param errorStack 错误堆栈
     * @param delaySeconds 重试延迟秒数（仅当未达最大重试次数时生效）
     * @return 是否成功
     */
    boolean failAndScheduleRetry(String taskId, String workerId, String errorMessage, 
                                 String errorStack, Long delaySeconds);
}