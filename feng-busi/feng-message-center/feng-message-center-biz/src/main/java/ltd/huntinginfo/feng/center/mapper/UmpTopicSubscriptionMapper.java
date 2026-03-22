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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.entity.UmpTopicSubscription;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 主题订阅表Mapper接口
 */
@Mapper
public interface UmpTopicSubscriptionMapper extends BaseMapper<UmpTopicSubscription> {

    /**
     * 根据主题代码和应用标识查询订阅
     *
     * @param topicCode 主题代码
     * @param appKey    应用标识
     * @return 订阅实体
     */
    @Select("SELECT * FROM ump_topic_subscription WHERE topic_code = #{topicCode} AND app_key = #{appKey} AND del_flag = '0'")
    UmpTopicSubscription selectByTopicAndApp(@Param("topicCode") String topicCode,
                                             @Param("appKey") String appKey);

    /**
     * 分页查询订阅列表
     *
     * @param page      分页参数
     * @param topicCode 主题代码（可选）
     * @param appKey    应用标识（可选）
     * @param status    状态（可选）
     * @param pushMode  推送方式（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_topic_subscription WHERE del_flag = '0' " +
            "<if test='topicCode != null and topicCode != \"\"'> AND topic_code = #{topicCode} </if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='pushMode != null and pushMode != \"\"'> AND push_mode = #{pushMode} </if>" +
            " ORDER BY subscribe_time DESC" +
            "</script>")
    IPage<UmpTopicSubscription> selectSubscriptionPage(Page<UmpTopicSubscription> page,
                                                       @Param("topicCode") String topicCode,
                                                       @Param("appKey") String appKey,
                                                       @Param("status") Integer status,
                                                       @Param("pushMode") String pushMode);

    /**
     * 根据主题代码查询订阅列表
     *
     * @param topicCode 主题代码
     * @param status    状态（可选）
     * @return 订阅列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_topic_subscription WHERE topic_code = #{topicCode} AND del_flag = '0' " +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY subscribe_time DESC" +
            "</script>")
    List<UmpTopicSubscription> selectByTopicCode(@Param("topicCode") String topicCode,
                                                 @Param("status") Integer status);

    /**
     * 根据应用标识查询订阅列表
     *
     * @param appKey 应用标识
     * @param status 状态（可选）
     * @return 订阅列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_topic_subscription WHERE app_key = #{appKey} AND del_flag = '0' " +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY subscribe_time DESC" +
            "</script>")
    List<UmpTopicSubscription> selectByAppKey(@Param("appKey") String appKey,
                                              @Param("status") Integer status);

    /**
     * 更新订阅状态
     *
     * @param id               订阅ID
     * @param status           状态
     * @param unsubscribeTime  取消订阅时间
     * @return 更新条数
     */
    @Update("UPDATE ump_topic_subscription SET status = #{status}, unsubscribe_time = #{unsubscribeTime} WHERE id = #{id} AND del_flag = '0'")
    Integer updateSubscriptionStatus(@Param("id") String id,
                                 @Param("status") Integer status,
                                 @Param("unsubscribeTime") LocalDateTime unsubscribeTime);

    /**
     * 更新订阅统计信息
     *
     * @param id               订阅ID
     * @param messageCount     消息数量
     * @param lastMessageTime  最后消息时间
     * @return 更新条数
     */
    @Update("UPDATE ump_topic_subscription SET message_count = #{messageCount}, last_message_time = #{lastMessageTime} WHERE id = #{id} AND del_flag = '0'")
    Integer updateSubscriptionStats(@Param("id") String id,
                                @Param("messageCount") Integer messageCount,
                                @Param("lastMessageTime") LocalDateTime lastMessageTime);

    /**
     * 批量更新订阅状态
     *
     * @param ids              订阅ID列表
     * @param status           目标状态
     * @param unsubscribeTime  取消订阅时间
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_topic_subscription SET status = #{status}, unsubscribe_time = #{unsubscribeTime} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND del_flag = '0'" +
            "</script>")
    Integer batchUpdateStatus(@Param("ids") List<String> ids,
                          @Param("status") Integer status,
                          @Param("unsubscribeTime") LocalDateTime unsubscribeTime);

    /**
     * 获取订阅统计信息
     *
     * @param topicCode 主题代码（可选）
     * @param appKey    应用标识（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS active_count, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS inactive_count, " +
            "SUM(CASE WHEN push_mode = 'PUSH' THEN 1 ELSE 0 END) AS push_mode_count, " +
            "SUM(CASE WHEN push_mode = 'POLL' THEN 1 ELSE 0 END) AS poll_mode_count, " +
            "SUM(message_count) AS total_messages, " +
            "AVG(message_count) AS avg_messages " +
            "FROM ump_topic_subscription " +
            "WHERE del_flag = '0' " +
            "<if test='topicCode != null and topicCode != \"\"'> AND topic_code = #{topicCode} </if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "</script>")
    Map<String, Object> selectSubscriptionStatistics(@Param("topicCode") String topicCode,
                                                     @Param("appKey") String appKey);

    /**
     * 检查订阅是否存在
     *
     * @param topicCode 主题代码
     * @param appKey    应用标识
     * @param status    状态（可选）
     * @return 是否存在
     */
    @Select("<script>" +
            "SELECT COUNT(1) > 0 FROM ump_topic_subscription WHERE topic_code = #{topicCode} AND app_key = #{appKey} AND del_flag = '0' " +
            "<if test='status != null'> AND status = #{status} </if>" +
            "</script>")
    Boolean existsSubscription(@Param("topicCode") String topicCode,
                               @Param("appKey") String appKey,
                               @Param("status") Integer status);

    /**
     * 获取活跃订阅数量
     *
     * @param topicCode 主题代码
     * @return 活跃订阅数量
     */
    @Select("SELECT COUNT(*) FROM ump_topic_subscription WHERE topic_code = #{topicCode} AND status = 1 AND del_flag = '0'")
    Integer countActiveSubscriptions(@Param("topicCode") String topicCode);
}