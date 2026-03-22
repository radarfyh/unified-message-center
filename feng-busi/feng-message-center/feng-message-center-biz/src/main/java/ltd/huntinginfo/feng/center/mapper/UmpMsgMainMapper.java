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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 消息主表Mapper接口
 */
@Mapper
public interface UmpMsgMainMapper extends BaseMapper<UmpMsgMain> {

    /**
     * 根据消息编码查询消息
     *
     * @param msgCode 消息编码
     * @return 消息实体
     */
    @Select("SELECT * FROM ump_msg_main WHERE msg_code = #{msgCode} AND del_flag = '0'")
    UmpMsgMain selectByMsgCode(@Param("msgCode") String msgCode);

    /**
     * 根据发送应用标识分页查询消息
     *
     * @param page         分页参数
     * @param senderAppKey 发送应用标识
     * @return 分页结果
     */
    @Select("SELECT * FROM ump_msg_main WHERE sender_app_key = #{senderAppKey} AND del_flag = '0' ORDER BY create_time DESC")
    IPage<UmpMsgMain> selectPageBySender(IPage<UmpMsgMain> page, @Param("senderAppKey") String senderAppKey);

    /**
     * 根据状态查询消息列表
     *
     * @param status 消息状态
     * @param limit  限制数量
     * @return 消息列表
     */
    @Select("SELECT * FROM ump_msg_main WHERE status = #{status} AND del_flag = '0' ORDER BY create_time ASC LIMIT #{limit}")
    List<UmpMsgMain> selectByStatus(@Param("status") String status, @Param("limit") int limit);

    /**
     * 查询过期但未标记为过期的消息
     *
     * @param expireTime 过期时间
     * @return 过期消息列表
     */
    @Select("SELECT * FROM ump_msg_main WHERE expire_time < #{expireTime} AND status NOT IN ('EXPIRED', 'FAILED') AND del_flag = '0'")
    List<UmpMsgMain> selectExpiredMessages(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 批量更新消息状态
     *
     * @param ids        消息ID列表
     * @param status     目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_main SET status = #{status} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") String status);

    /**
     * 更新消息的已读统计
     *
     * @param msgId     消息ID
     * @param readCount 已读人数
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_main SET read_count = #{readCount}, update_time = #{updateTime} WHERE id = #{msgId} AND del_flag = '0'")
    int updateReadCount(@Param("msgId") String msgId, @Param("readCount") Integer readCount);

    /**
     * 查询消息统计信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param appKey    应用标识（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT status, " +
            "COUNT(*) AS count, " +
            "SUM(total_receivers) AS receivers, " +
            "SUM(read_count) AS read_count " +
            "FROM ump_msg_main " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "AND del_flag = '0' " +
            "<if test='appKey != null and appKey != \"\"'> AND sender_app_key = #{appKey} </if> " +
            "GROUP BY status" +
            "</script>")
    List<Map<String, Object>> selectMessageStatistics(@Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime,
                                                      @Param("appKey") String appKey);
    
    /**
     * 按应用和消息类型分组统计消息数据
     * 修复：字段名 receiver_count → received_count，状态值适配当前枚举
     */
    @Select("SELECT " +
            "  m.sender_app_key AS app_key, " +
            "  m.msg_type, " +
            "  COUNT(*) AS send_count, " +
            "  SUM(CASE WHEN m.status IN ('BIZ_RECEIVED', 'BIZ_PULLED', 'READ') THEN 1 ELSE 0 END) AS send_success_count, " +
            "  SUM(CASE WHEN m.status IN ('DIST_FAILED', 'PUSH_FAILED', 'POLL_FAILED') THEN 1 ELSE 0 END) AS send_failed_count, " +
            "  SUM(m.received_count) AS receive_count, " +
            "  SUM(m.read_count) AS read_count, " +
            "  COALESCE(SUM(q.error_count), 0) AS error_count, " +
            "  COALESCE(SUM(q.retry_count), 0) AS retry_count " +
            "FROM ump_msg_main m " +
            "LEFT JOIN ( " +
            "  SELECT " +
            "    msg_id, " +
            "    COUNT(CASE WHEN status = 'FAILED' THEN 1 END) AS error_count, " +
            "    SUM(current_retry) AS retry_count " +
            "  FROM ump_msg_queue " +
            "  GROUP BY msg_id " +
            ") q ON m.id = q.msg_id " +
            "WHERE m.create_time BETWEEN #{startTime} AND #{endTime} " +
            "  AND m.del_flag = 0 " +
            "GROUP BY m.sender_app_key, m.msg_type")
    List<Map<String, Object>> selectMessageStatisticsGroupByAppAndType(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}