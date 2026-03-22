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
import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import ltd.huntinginfo.feng.center.api.vo.UnreadReceiverVO;

import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 广播消息接收记录表Mapper接口
 */
@Mapper
public interface UmpBroadcastReceiveRecordMapper extends BaseMapper<UmpBroadcastReceiveRecord> {

    /**
     * 根据复合主键查询接收记录
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 接收记录
     */
    @Select("SELECT * FROM ump_broadcast_receive_record WHERE broadcast_id = #{broadcastId} AND receiver_id = #{receiverId} AND receiver_type = #{receiverType} AND del_flag = '0'")
    UmpBroadcastReceiveRecord selectByPrimaryKey(@Param("broadcastId") String broadcastId,
                                                @Param("receiverId") String receiverId,
                                                @Param("receiverType") String receiverType);

    /**
     * 分页查询广播接收记录
     *
     * @param page 分页参数
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID（可选）
     * @param receiverType 接收者类型（可选）
     * @param receiveStatus 接收状态（可选）
     * @param readStatus 阅读状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_broadcast_receive_record WHERE del_flag = '0' " +
            "<if test='broadcastId != null and broadcastId != \"\"'> AND broadcast_id = #{broadcastId} </if>" +
            "<if test='receiverId != null and receiverId != \"\"'> AND receiver_id = #{receiverId} </if>" +
            "<if test='receiverType != null and receiverType != \"\"'> AND receiver_type = #{receiverType} </if>" +
            "<if test='receiveStatus != null and receiveStatus != \"\"'> AND receive_status = #{receiveStatus} </if>" +
            "<if test='readStatus != null'> AND read_status = #{readStatus} </if>" +
            " ORDER BY update_time DESC" +
            "</script>")
    IPage<UmpBroadcastReceiveRecord> selectReceiveRecordPage(IPage<UmpBroadcastReceiveRecord> page,
                                                            @Param("broadcastId") String broadcastId,
                                                            @Param("receiverId") String receiverId,
                                                            @Param("receiverType") String receiverType,
                                                            @Param("receiveStatus") String receiveStatus,
                                                            @Param("readStatus") Integer readStatus);

    /**
     * 根据广播ID查询接收记录列表
     *
     * @param broadcastId 广播ID
     * @return 接收记录列表
     */
    @Select("SELECT * FROM ump_broadcast_receive_record WHERE broadcast_id = #{broadcastId} AND del_flag = '0' ORDER BY update_time DESC")
    List<UmpBroadcastReceiveRecord> selectByBroadcastId(@Param("broadcastId") String broadcastId);

    /**
     * 根据接收者查询广播接收记录
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态（可选）
     * @param limit 限制数量
     * @return 接收记录列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_broadcast_receive_record WHERE receiver_id = #{receiverId} AND receiver_type = #{receiverType} " +
            "<if test='readStatus != null'> AND read_status = #{readStatus} </if>" +
            " AND del_flag = '0' ORDER BY update_time DESC LIMIT #{limit}" +
            "</script>")
    List<UmpBroadcastReceiveRecord> selectByReceiver(@Param("receiverId") String receiverId,
                                                    @Param("receiverType") String receiverType,
                                                    @Param("readStatus") Integer readStatus,
                                                    @Param("limit") int limit);

    /**
     * 批量插入或更新接收记录
     *
     * @param records 接收记录列表
     * @return 插入/更新条数
     */
    @Insert("<script>" +
            "INSERT INTO ump_broadcast_receive_record " +
            "(broadcast_id, receiver_id, receiver_type, receive_status, receive_time, read_status, read_time, create_by, update_by) " +
            "VALUES " +
            "<foreach collection='records' item='item' separator=','>(" +
            "#{item.broadcastId}, #{item.receiverId}, #{item.receiverType}, #{item.receiveStatus}, #{item.receiveTime}, " +
            "#{item.readStatus}, #{item.readTime}, 'system', 'system')" +
            "</foreach> " +
            "ON DUPLICATE KEY UPDATE receive_status = VALUES(receive_status)," + 
            "receive_time = VALUES(receive_time)," + 
            "read_status = VALUES(read_status)," + 
            "read_time = VALUES(read_time)," + 
            "del_flag = '0'" +
            "</script>")
    int batchUpsert(@Param("records") List<UmpBroadcastReceiveRecord> records);

    /**
     * 更新接收状态
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param receiveStatus 接收状态
     * @param receiveTime 接收时间
     * @return 更新条数
     */
    @Update("UPDATE ump_broadcast_receive_record SET receive_status = #{receiveStatus}, receive_time = #{receiveTime} " +
            "WHERE broadcast_id = #{broadcastId} AND receiver_id = #{receiverId} AND receiver_type = #{receiverType} AND del_flag = '0'")
    int updateReceiveStatus(@Param("broadcastId") String broadcastId,
                           @Param("receiverId") String receiverId,
                           @Param("receiverType") String receiverType,
                           @Param("receiveStatus") String receiveStatus,
                           @Param("receiveTime") LocalDateTime receiveTime);
    /**
     * 批量更新接收状态
     *
     * @param broadcastId 广播ID
     * @param receiverIds 接收者ID列表
     * @param receiverType 接收者类型
     * @param receiveStatus 接收状态
     * @param receiveTime 接收时间
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_broadcast_receive_record SET receive_status = #{receiveStatus}, receive_time = #{receiveTime} " +
            "WHERE broadcast_id = #{broadcastId} AND receiver_type = #{receiverType} AND del_flag = '0' " +
            "AND receiver_id IN <foreach collection='receiverIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateReceiveStatus(@Param("broadcastId") String broadcastId,
                             @Param("receiverIds") List<String> receiverIds,
                             @Param("receiverType") String receiverType,
                             @Param("receiveStatus") String receiveStatus,
                             @Param("receiveTime") LocalDateTime receiveTime);

    /**
     * 更新阅读状态
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态
     * @param readTime 阅读时间
     * @return 更新条数
     */
    @Update("UPDATE ump_broadcast_receive_record SET read_status = #{readStatus}, read_time = #{readTime} " +
            "WHERE broadcast_id = #{broadcastId} AND receiver_id = #{receiverId} AND receiver_type = #{receiverType} AND del_flag = '0'")
    int updateReadStatus(@Param("broadcastId") String broadcastId,
                        @Param("receiverId") String receiverId,
                        @Param("receiverType") String receiverType,
                        @Param("readStatus") Integer readStatus,
                        @Param("readTime") LocalDateTime readTime);

    /**
     * 批量更新阅读状态
     *
     * @param broadcastId 广播ID
     * @param receiverIds 接收者ID列表
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态
     * @param readTime 阅读时间
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_broadcast_receive_record SET read_status = #{readStatus}, read_time = #{readTime} " +
            "WHERE broadcast_id = #{broadcastId} AND receiver_type = #{receiverType} AND del_flag = '0' AND receiver_id IN " +
            "<foreach collection='receiverIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateReadStatus(@Param("broadcastId") String broadcastId,
                             @Param("receiverIds") List<String> receiverIds,
                             @Param("receiverType") String receiverType,
                             @Param("readStatus") Integer readStatus,
                             @Param("readTime") LocalDateTime readTime);

    /**
     * 统计广播接收记录
     *
     * @param broadcastId 广播ID
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN receive_status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count, " +
            "SUM(CASE WHEN receive_status = 'SUCCESS' THEN 1 ELSE 0 END) AS delivered_count, " +
            "SUM(CASE WHEN receive_status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count, " +
            "SUM(CASE WHEN read_status = 0 THEN 1 ELSE 0 END) AS unread_count, " +
            "SUM(CASE WHEN read_status = 1 THEN 1 ELSE 0 END) AS read_count " +
            "FROM ump_broadcast_receive_record WHERE broadcast_id = #{broadcastId} AND del_flag = '0'")
    Map<String, Object> countByBroadcast(@Param("broadcastId") String broadcastId);

    /**
     * 查询广播未读接收者
     *
     * @param broadcastId 广播ID
     * @param limit 限制数量
     * @return 未读接收者列表
     */
    @Select("SELECT broadcast_id, receiver_id, receiver_type, receive_status, read_status " +
            "FROM ump_broadcast_receive_record " +
            "WHERE broadcast_id = #{broadcastId} AND read_status = 0 AND del_flag = '0' " +
            "ORDER BY update_time DESC LIMIT #{limit}")
    List<UnreadReceiverVO> selectUnreadReceivers(@Param("broadcastId") String broadcastId,
                                                   @Param("limit") int limit);
}