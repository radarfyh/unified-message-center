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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgCallback;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigResponse;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 回调记录表Mapper接口
 */
@Mapper
public interface UmpMsgCallbackMapper extends BaseMapper<UmpMsgCallback> {

    /**
     * 根据消息ID和接收者ID查询回调记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @return 回调记录列表
     */
    @Select("SELECT * FROM ump_msg_callback WHERE msg_id = #{msgId} AND receiver_id = #{receiverId} AND del_flag = '0' ORDER BY create_time DESC")
    List<UmpMsgCallback> selectByMsgAndReceiver(@Param("msgId") String msgId, 
                                               @Param("receiverId") String receiverId);

    /**
     * 分页查询回调记录
     *
     * @param page 分页参数
     * @param msgId 消息ID（可选）
     * @param receiverId 接收者ID（可选）
     * @param status 状态（可选）
     * @param callbackUrl 回调地址（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_callback WHERE del_flag = '0' " +
            "<if test='msgId != null and msgId != \"\"'> AND msg_id = #{msgId} </if>" +
            "<if test='receiverId != null and receiverId != \"\"'> AND receiver_id = #{receiverId} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='callbackUrl != null and callbackUrl != \"\"'> AND callback_url LIKE CONCAT('%', #{callbackUrl}, '%') </if>" +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpMsgCallback> selectCallbackPage(IPage<UmpMsgCallback> page,
                                            @Param("msgId") String msgId,
                                            @Param("receiverId") String receiverId,
                                            @Param("status") String status,
                                            @Param("callbackUrl") String callbackUrl,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 查询待重试的回调记录
     *
     * @param maxRetryCount 最大重试次数
     * @param limit 限制数量
     * @return 待重试的回调记录列表
     */
//    @Select("SELECT * FROM ump_msg_callback " +
//            "WHERE status = 'FAILED' AND retry_count < #{maxRetryCount} " +
//            "AND (next_retry_time IS NULL OR next_retry_time <= NOW()) AND del_flag = '0' " +
//            "ORDER BY next_retry_time ASC LIMIT #{limit}")
//    List<UmpMsgCallback> selectPendingRetry(@Param("maxRetryCount") Integer maxRetryCount, 
//                                           @Param("limit") int limit);

    /**
     * 查询待发送的回调记录
     *
     * @param limit 限制数量
     * @return 待发送的回调记录列表
     */
    @Select("SELECT * FROM ump_msg_callback WHERE status = 'PENDING' AND del_flag = '0' ORDER BY create_time ASC LIMIT #{limit}")
    List<UmpMsgCallback> selectPendingSend(@Param("limit") int limit);

    /**
     * 更新回调状态
     *
     * @param id 回调记录ID
     * @param status 状态
     * @param httpStatus HTTP状态码（可选）
     * @param responseBody 响应内容（可选）
     * @param errorMessage 错误信息（可选）
     * @param sendTime 发送时间（可选）
     * @param responseTime 响应时间（可选）
     * @param costTime 耗时（可选）
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_callback " +
            "<set>" +
            "status = #{status}, " +
            "<if test='responseBody != null'>response_body = #{responseBody, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, </if>" +
            "<if test='errorMessage != null'>error_message = #{errorMessage}, </if>" +
            "<if test='sendTime != null'>send_time = #{sendTime}, </if>" +
            "<if test='responseTime != null'>response_time = #{responseTime}, </if>" +
            "<if test='costTime != null'>cost_time = #{costTime}, </if>" +
            "</set>" +
            "WHERE id = #{id} AND del_flag = '0'" +
            "</script>")
    int updateCallbackStatus(@Param("id") String id,
                             @Param("status") String status,
                             @Param("responseBody") Map<String, Object> responseBody,
                             @Param("errorMessage") String errorMessage,
                             @Param("sendTime") LocalDateTime sendTime,
                             @Param("responseTime") LocalDateTime responseTime,
                             @Param("costTime") Integer costTime);
    /**
     * 更新重试信息
     *
     * @param id 回调记录ID
     * @param retryCount 重试次数
     * @param nextRetryTime 下次重试时间
     * @param status 状态
     * @return 更新条数
     */
//    @Update("UPDATE ump_msg_callback SET retry_count = #{retryCount}, next_retry_time = #{nextRetryTime}, " +
//            "status = #{status} WHERE id = #{id} AND del_flag = '0'")
//    int updateRetryInfo(@Param("id") String id,
//                       @Param("retryCount") Integer retryCount,
//                       @Param("nextRetryTime") LocalDateTime nextRetryTime,
//                       @Param("status") String status);

    /**
     * 批量更新回调状态
     *
     * @param ids 回调记录ID列表
     * @param status 目标状态
     * @return 更新条数
     */
//    @Update("<script>" +
//            "UPDATE ump_msg_callback SET status = #{status} " +
//            "WHERE id IN " +
//            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
//            "</script>")
//    int batchUpdateStatus(@Param("ids") List<String> ids,
//                         @Param("status") String status);

    /**
     * 获取回调统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param msgId 消息ID（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count, " +
            "SUM(CASE WHEN status = 'PROCESSING' THEN 1 ELSE 0 END) AS processing_count, " +
            "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_count, " +
            "SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count, " +
            "AVG(cost_time) AS avg_cost_time, " +
            "AVG(retry_count) AS avg_retry_count " +
            "FROM ump_msg_callback " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "<if test='msgId != null and msgId != \"\"'> AND msg_id = #{msgId} </if> AND del_flag = '0'" +
            "</script>")
    Map<String, Object> selectCallbackStatistics(@Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("msgId") String msgId);

    /**
     * 查询失败的回调记录
     *
     * @param retryCount 重试次数阈值
     * @param limit 限制数量
     * @return 失败的回调记录列表
     */
//    @Select("SELECT * FROM ump_msg_callback WHERE status = 'FAILED' AND retry_count >= #{retryCount} AND del_flag = '0' " +
//            "ORDER BY create_time DESC LIMIT #{limit}")
//    List<UmpMsgCallback> selectFailedCallbacks(@Param("retryCount") Integer retryCount,
//                                              @Param("limit") int limit);
}