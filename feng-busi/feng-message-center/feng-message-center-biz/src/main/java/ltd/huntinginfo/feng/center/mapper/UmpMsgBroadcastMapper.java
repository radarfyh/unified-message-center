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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广播信息筒表Mapper接口
 */
@Mapper
public interface UmpMsgBroadcastMapper extends BaseMapper<UmpMsgBroadcast> {

    /**
     * 根据消息ID查询广播记录
     *
     * @param msgId 消息ID
     * @return 广播记录
     */
    @Select("SELECT * FROM ump_msg_broadcast WHERE msg_id = #{msgId} AND del_flag = '0' LIMIT 1")
    UmpMsgBroadcast selectByMsgId(@Param("msgId") String msgId);

    /**
     * 分页查询广播记录
     *
     * @param page 分页参数
     * @param broadcastType 广播类型（可选）
     * @param status 状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_broadcast WHERE del_flag = '0' " +
            "<if test='broadcastType != null and broadcastType != \"\"'> AND broadcast_type = #{broadcastType} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpMsgBroadcast> selectBroadcastPage(IPage<UmpMsgBroadcast> page,
                                                @Param("broadcastType") String broadcastType,
                                                @Param("status") String status,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 根据状态查询广播记录
     *
     * @param status 状态
     * @param limit 限制数量
     * @return 广播记录列表
     */
    @Select("SELECT * FROM ump_msg_broadcast WHERE status = #{status} AND del_flag = '0' ORDER BY create_time ASC LIMIT #{limit}")
    List<UmpMsgBroadcast> selectByStatus(@Param("status") String status, @Param("limit") int limit);
    
    /**
     * 查询自定义接收者的广播记录
     *
     * @return 广播记录列表
     */
    @Select("SELECT *\r\n"
    		+ "FROM ump_msg_broadcast\r\n"
    		+ "WHERE (receiving_unit_code IS NULL OR TRIM(receiving_unit_code) = '')\r\n"
    		+ "  AND receiving_scope IS NOT NULL\r\n"
    		+ "  AND JSON_TYPE(receiving_scope) = 'OBJECT'\r\n"
    		+ "  AND JSON_LENGTH(receiving_scope) > 0\r\n"
    		+ "  AND del_flag = '0'\r\n"
    		+ "ORDER BY create_time ASC LIMIT #{limit};")
    List<UmpMsgBroadcast> selectByCustomReceiver(@Param("limit") int limit);

    /**
     * 更新广播统计信息
     *
     * @param broadcastId 广播ID
     * @param distributedCount 已分发数量
     * @param receivedCount 已接收数量
     * @param readCount 已读人数
     * @param status 状态
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_broadcast SET " +
            "distributed_count = #{distributedCount}, " +
            "received_count = #{receivedCount}, " +
            "read_count = #{readCount}, " +
            "status = #{status} " +
            "WHERE id = #{broadcastId} AND del_flag = '0' ")
    int updateBroadcastStatistics(@Param("broadcastId") String broadcastId,
                                   @Param("distributedCount") Integer distributedCount,
                                   @Param("receivedCount") Integer receivedCount,
                                   @Param("readCount") Integer readCount,
                                   @Param("status") String status);

    /**
     * 批量更新广播状态
     *
     * @param ids 广播ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_broadcast SET status = #{status} " +
            "WHERE del_flag = '0' AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                           @Param("status") String status);

    /**
     * 获取广播统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param broadcastType 广播类型（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 'DISTRIBUTING' THEN 1 ELSE 0 END) AS distributing_count, " +
            "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count, " +
            "SUM(CASE WHEN status = 'PARTIAL' THEN 1 ELSE 0 END) AS partial_count, " +
            "SUM(total_receivers) AS total_receivers, " +
            "SUM(distributed_count) AS distributed_receivers, " +
            "SUM(received_count) AS received_receivers, " +
            "SUM(read_count) AS read_receivers " +
            "FROM ump_msg_broadcast " +
            "WHERE del_flag = '0' AND create_time BETWEEN #{startTime} AND #{endTime} " +
            "<if test='broadcastType != null and broadcastType != \"\"'> AND broadcast_type = #{broadcastType} </if>" +
            "</script>")
    Map<String, Object> selectBroadcastStatistics(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   @Param("broadcastType") String broadcastType);

    /**
     * 查询待分发的广播记录
     *
     * @param limit 限制数量
     * @return 待分发的广播记录列表
     */
    @Select("SELECT * FROM ump_msg_broadcast " +
            "WHERE del_flag = '0' AND status = 'DISTRIBUTING' AND distributed_count &lt; total_receivers " +
            "ORDER BY create_time ASC LIMIT #{limit}")
    List<UmpMsgBroadcast> selectPendingDistribute(@Param("limit") int limit);

    /**
     * 已读数量加1
     * @param broadcastId
     */
    @Update("UPDATE ump_msg_broadcast SET read_count = read_count + 1 WHERE id = #{broadcastId} AND del_flag = '0'")
    void incrementReadCount(String broadcastId);

    /**
     * 更新已接收数量
     *
     * @param broadcastId 广播ID
     * @param receivedCount 已接收数量
     * @param status 状态
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_broadcast SET received_count = #{receivedCount}, status = #{status} WHERE id = #{broadcastId} AND del_flag = '0'")
    int updateReceivedCount(@Param("broadcastId") String broadcastId,
                             @Param("receivedCount") Integer receivedCount,
                             @Param("status") String status);

    /**
     * 更新已读数量
     *
     * @param broadcastId 广播ID
     * @param readCount 已读数量
     * @param status 状态
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_broadcast SET read_count = #{readCount}, status = #{status} WHERE id = #{broadcastId} AND del_flag = '0'")
    int updateReadCount(@Param("broadcastId") String broadcastId,
                         @Param("readCount") Integer readCount,
                         @Param("status") String status);

    /**
     * 更新已分发数量
     *
     * @param broadcastId 广播ID
     * @param distributedCount 已分发数量
     * @param status 状态
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_broadcast SET distributed_count = #{distributedCount}, status = #{status} WHERE id = #{broadcastId} AND del_flag = '0'")
    int updateDistributedCount(@Param("broadcastId") String broadcastId,
                                @Param("distributedCount") Integer distributedCount,
                                @Param("status") String status);
}