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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 收件箱表Mapper接口
 */
@Mapper
public interface UmpMsgInboxMapper extends BaseMapper<UmpMsgInbox> {

    /**
     * 根据消息ID和接收者查询收件箱记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 收件箱记录
     */
    @Select("SELECT * FROM ump_msg_inbox WHERE msg_id = #{msgId} AND receiver_id = #{receiverId} AND receiver_type = #{receiverType} AND del_flag = '0'")
    UmpMsgInbox selectByMsgAndReceiver(@Param("msgId") String msgId, 
                                       @Param("receiverId") String receiverId, 
                                       @Param("receiverType") String receiverType);

    /**
     * 分页查询接收者的收件箱
     *
     * @param page 分页参数
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态（可选）
     * @param receiveStatus 接收状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_inbox " +
            "WHERE receiver_id = #{receiverId} AND receiver_type = #{receiverType} " +
            "<if test='readStatus != null'> AND read_status = #{readStatus} </if>" +
            "<if test='receiveStatus != null and receiveStatus != \"\"'> AND receive_status = #{receiveStatus} </if> AND del_flag = '0'" +
            " ORDER BY distribute_time DESC" +
            "</script>")
    IPage<UmpMsgInbox> selectPageByReceiver(IPage<UmpMsgInbox> page, 
                                            @Param("receiverId") String receiverId, 
                                            @Param("receiverType") String receiverType,
                                            @Param("readStatus") Integer readStatus,
                                            @Param("receiveStatus") String receiveStatus);

    /**
     * 根据消息ID查询收件箱记录列表
     *
     * @param msgId 消息ID
     * @return 收件箱记录列表
     */
    @Select("SELECT * FROM ump_msg_inbox WHERE msg_id = #{msgId} AND del_flag = '0'")
    List<UmpMsgInbox> selectByMsgId(@Param("msgId") String msgId);

    /**
     * 批量更新收件箱记录的接收状态
     *
     * @param ids 收件箱记录ID列表
     * @param receiveStatus 接收状态
     * @param receiveTime 接收时间
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_inbox SET receive_status = #{receiveStatus}, receive_time = #{receiveTime} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
            "</script>")
    int batchUpdateReceiveStatus(@Param("ids") List<String> ids, 
                                @Param("receiveStatus") String receiveStatus, 
                                @Param("receiveTime") LocalDateTime receiveTime);

    /**
     * 批量更新收件箱记录的阅读状态
     *
     * @param ids 收件箱记录ID列表
     * @param readStatus 阅读状态
     * @param readTime 阅读时间
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_inbox SET read_status = #{readStatus}, read_time = #{readTime} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
            "</script>")
    int batchUpdateReadStatus(@Param("ids") List<String> ids, 
                             @Param("readStatus") Integer readStatus, 
                             @Param("readTime") LocalDateTime readTime);

    /**
     * 更新推送状态和次数
     *
     * @param id 收件箱记录ID
     * @param pushStatus 推送状态
     * @param pushCount 推送次数
     * @param lastPushTime 最后推送时间
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_inbox SET push_status = #{pushStatus}, push_count = #{pushCount}, last_push_time = #{lastPushTime}, error_message = #{errorMessage} WHERE id = #{id} AND del_flag = '0'")
    int updatePushStatus(@Param("id") String id, 
                        @Param("pushStatus") String pushStatus, 
                        @Param("pushCount") Integer pushCount, 
                        @Param("lastPushTime") LocalDateTime lastPushTime,
                        @Param("errorMessage") String errorMessage);

    /**
     * 查询待推送的收件箱记录
     *
     * @param limit 限制数量
     * @return 待推送的收件箱记录列表
     */
    @Select("SELECT * FROM ump_msg_inbox WHERE push_status = 'PENDING' AND del_flag = '0' ORDER BY distribute_time ASC LIMIT #{limit}")
    List<UmpMsgInbox> selectPendingPush(@Param("limit") int limit);

    /**
     * 统计接收者的未读消息数量
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    @Select("SELECT COUNT(*) FROM ump_msg_inbox WHERE receiver_id = #{receiverId} AND receiver_type = #{receiverType} AND read_status = 0 AND del_flag = '0'")
    Integer countUnreadByReceiver(@Param("receiverId") String receiverId, 
                                  @Param("receiverType") String receiverType);

    /**
     * 获取接收者的消息统计
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param startTime 开始分发时间（可选）
     * @param endTime 结束分发时间（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN read_status = 0 THEN 1 ELSE 0 END) AS unread_count, " +
            "SUM(CASE WHEN read_status = 1 THEN 1 ELSE 0 END) AS read_count, " +
            "SUM(CASE WHEN receive_status = 'SUCCESS' THEN 1 ELSE 0 END) AS received_count, " +
            "SUM(CASE WHEN receive_status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count " +
            "FROM ump_msg_inbox " +
            "WHERE receiver_id = #{receiverId} AND receiver_type = #{receiverType} " +
            "<if test='startTime != null'> AND distribute_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND distribute_time &lt;= #{endTime} </if> AND del_flag = '0'" +
            "</script>")
    Map<String, Object> selectReceiverStatistics(@Param("receiverId") String receiverId, 
                                                @Param("receiverType") String receiverType,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);
}