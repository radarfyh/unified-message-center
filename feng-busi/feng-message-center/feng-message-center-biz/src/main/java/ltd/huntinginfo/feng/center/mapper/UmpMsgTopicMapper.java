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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTopic;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 消息主题表Mapper接口
 */
@Mapper
public interface UmpMsgTopicMapper extends BaseMapper<UmpMsgTopic> {

    /**
     * 根据主题代码查询主题
     *
     * @param topicCode 主题代码
     * @return 主题实体
     */
    @Select("SELECT * FROM ump_msg_topic WHERE topic_code = #{topicCode} AND del_flag = '0'")
    UmpMsgTopic selectByTopicCode(@Param("topicCode") String topicCode);

    /**
     * 分页查询主题列表
     *
     * @param page      分页参数
     * @param topicName 主题名称（可选）
     * @param topicType 主题类型（可选）
     * @param status    状态（可选）
     * @param topicCode 主题代码（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_topic WHERE del_flag = '0' " +
            "<if test='topicName != null and topicName != \"\"'> AND topic_name LIKE CONCAT('%', #{topicName}, '%') </if>" +
            "<if test='topicType != null and topicType != \"\"'> AND topic_type = #{topicType} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='topicCode != null and topicCode != \"\"'> AND topic_code = #{topicCode} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpMsgTopic> selectTopicPage(IPage<UmpMsgTopic> page,
                                      @Param("topicName") String topicName,
                                      @Param("topicType") String topicType,
                                      @Param("status") Integer status,
                                      @Param("topicCode") String topicCode);

    /**
     * 根据状态查询主题列表
     *
     * @param status 状态
     * @return 主题列表
     */
    @Select("SELECT * FROM ump_msg_topic WHERE status = #{status} AND del_flag = '0' ORDER BY create_time DESC")
    List<UmpMsgTopic> selectByStatus(@Param("status") Integer status);

    /**
     * 查询可用的主题列表
     *
     * @return 可用的主题列表
     */
    @Select("SELECT * FROM ump_msg_topic WHERE status = 1 AND del_flag = '0' ORDER BY create_time DESC")
    List<UmpMsgTopic> selectAvailableTopics();

    /**
     * 更新主题的订阅者数量
     *
     * @param topicId          主题ID
     * @param subscriberCount 订阅者数量
     * @return 更新条数
     */
    @Update("UPDATE ump_msg_topic SET subscriber_count = #{subscriberCount}, update_time = NOW() WHERE id = #{topicId} AND del_flag = '0'")
    int updateSubscriberCount(@Param("topicId") String topicId,
                             @Param("subscriberCount") Integer subscriberCount);

    /**
     * 批量更新主题状态
     *
     * @param ids    主题ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_topic SET status = #{status}, update_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 获取主题统计信息
     *
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN topic_type = 'SYSTEM' THEN 1 ELSE 0 END) AS system_count, " +
            "SUM(CASE WHEN topic_type = 'CUSTOM' THEN 1 ELSE 0 END) AS custom_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS enabled_count, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS disabled_count, " +
            "SUM(subscriber_count) AS total_subscribers, " +
            "AVG(subscriber_count) AS avg_subscribers " +
            "FROM ump_msg_topic WHERE del_flag = '0'")
    Map<String, Object> selectTopicStatistics();
}