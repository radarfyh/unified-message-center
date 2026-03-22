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
package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgQueue;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息队列表Mapper接口
 */
@Mapper
public interface UmpMsgQueueMapper extends BaseMapper<UmpMsgQueue> {

    /**
     * 根据消息ID查询队列任务
     *
     * @param msgId 消息ID
     * @return 队列任务列表
     */
    @Select("SELECT * FROM ump_msg_queue WHERE msg_id = #{msgId} AND del_flag = '0' ORDER BY create_time DESC")
    List<UmpMsgQueue> selectByMsgId(@Param("msgId") String msgId);

    /**
     * 分页查询队列任务
     *
     * @param page      分页参数
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param status    状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime   结束时间（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_queue WHERE del_flag = '0' " +
            "<if test='queueType != null and queueType != \"\"'> AND queue_type = #{queueType} </if>" +
            "<if test='queueName != null and queueName != \"\"'> AND queue_name = #{queueName} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            " ORDER BY priority ASC, execute_time ASC" +
            "</script>")
    IPage<UmpMsgQueue> selectQueuePage(IPage<UmpMsgQueue> page,
                                      @Param("queueType") String queueType,
                                      @Param("queueName") String queueName,
                                      @Param("status") String status,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 查询待执行的任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param limit     限制数量
     * @return 待执行任务列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_queue WHERE status = 'PENDING' AND execute_time &lt;= NOW() " +
            "<if test='queueType != null and queueType != \"\"'> AND queue_type = #{queueType} </if>" +
            "<if test='queueName != null and queueName != \"\"'> AND queue_name = #{queueName} </if> AND del_flag = '0'" +
            " ORDER BY priority ASC, execute_time ASC LIMIT #{limit}" +
            "</script>")
    List<UmpMsgQueue> selectPendingTasks(@Param("queueType") String queueType,
                                        @Param("queueName") String queueName,
                                        @Param("limit") int limit);

    /**
     * 更新任务状态
     *
     * @param id            任务ID
     * @param status        状态
     * @param workerId      工作者ID（可选）
     * @param startTime     开始时间（可选）
     * @param endTime       结束时间（可选）
     * @param resultCode    结果代码（可选）
     * @param resultMessage 结果消息（可选）
     * @param errorStack    错误堆栈（可选）
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_queue " +
            "<set>" +
            "status = #{status}, " +
            "update_time = NOW(), " +
            "<if test='workerId != null'>worker_id = #{workerId}, </if>" +
            "<if test='startTime != null'>start_time = #{startTime}, </if>" +
            "<if test='endTime != null'>end_time = #{endTime}, </if>" +
            "<if test='resultCode != null'>result_code = #{resultCode}, </if>" +
            "<if test='resultMessage != null'>result_message = #{resultMessage}, </if>" +
            "<if test='errorStack != null'>error_stack = #{errorStack}, </if>" +
            "</set>" +
            "WHERE id = #{id} AND del_flag = '0'" +
            "</script>")
    int updateTaskStatus(@Param("id") String id,
                        @Param("status") String status,
                        @Param("workerId") String workerId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime,
                        @Param("resultCode") String resultCode,
                        @Param("resultMessage") String resultMessage,
                        @Param("errorStack") String errorStack);

    /**
     * 更新任务重试次数
     *
     * @param id           任务ID
     * @param currentRetry 当前重试次数
     * @param executeTime  下次执行时间
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_queue SET current_retry = #{currentRetry}, execute_time = #{executeTime}, update_time = NOW() WHERE id = #{id} AND del_flag = '0'")
    int updateRetryCount(@Param("id") String id,
                        @Param("currentRetry") Integer currentRetry,
                        @Param("executeTime") LocalDateTime executeTime);

    /**
     * 批量更新任务状态
     *
     * @param ids        任务ID列表
     * @param status     目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_queue SET status = #{status} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") String status);

    /**
     * 获取队列统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param queueType 队列类型（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count, " +
            "SUM(CASE WHEN status = 'PROCESSING' THEN 1 ELSE 0 END) AS processing_count, " +
            "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_count, " +
            "SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count, " +
            "AVG(priority) AS avg_priority, " +
            "AVG(current_retry) AS avg_retry_count " +
            "FROM ump_msg_queue " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "<if test='queueType != null and queueType != \"\"'> AND queue_type = #{queueType} </if> AND del_flag = '0'" +
            "</script>")
    Map<String, Object> selectQueueStatistics(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("queueType") String queueType);

    /**
     * 查询超时任务
     *
     * @param timeoutMinutes 超时时间（分钟）
     * @param limit          限制数量
     * @return 超时任务列表
     */
    @Select("SELECT * FROM ump_msg_queue WHERE status = 'PROCESSING' AND start_time &lt;= DATE_SUB(NOW(), INTERVAL #{timeoutMinutes} MINUTE) AND del_flag = '0' ORDER BY start_time ASC LIMIT #{limit}")
    List<UmpMsgQueue> selectTimeoutTasks(@Param("timeoutMinutes") int timeoutMinutes,
                                        @Param("limit") int limit);

    /**
     * 查询失败任务
     *
     * @param queueType 队列类型（可选）
     * @param queueName 队列名称（可选）
     * @param limit     限制数量
     * @return 失败任务列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_queue WHERE status = 'FAILED' " +
            "<if test='queueType != null and queueType != \"\"'> AND queue_type = #{queueType} </if>" +
            "<if test='queueName != null and queueName != \"\"'> AND queue_name = #{queueName} </if> AND del_flag = '0'" +
            " ORDER BY execute_time ASC LIMIT #{limit}" +
            "</script>")
    List<UmpMsgQueue> selectFailedTasks(@Param("queueType") String queueType,
                                       @Param("queueName") String queueName,
                                       @Param("limit") int limit);
}