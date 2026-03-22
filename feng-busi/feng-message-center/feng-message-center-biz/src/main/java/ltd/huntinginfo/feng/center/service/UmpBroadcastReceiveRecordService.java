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
import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import ltd.huntinginfo.feng.center.api.dto.BroadcastReceiveRecordQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.UnreadReceiverVO;

import java.util.List;
import java.util.Map;

/**
 * 广播消息接收记录表服务接口
 */
public interface UmpBroadcastReceiveRecordService extends IService<UmpBroadcastReceiveRecord> {

    /**
     * 创建或更新接收记录
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 是否成功
     */
    boolean upsertReceiveRecord(String broadcastId, String receiverId, String receiverType);

    /**
     * 批量创建或更新接收记录
     *
     * @param broadcastId 广播ID
     * @param receivers 接收者列表
     * @return 成功处理数量
     */
    int batchUpsertReceiveRecords(String broadcastId, List<Map<String, Object>> receivers);

    /**
     * 根据复合主键查询接收记录
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 接收记录详情VO
     */
    BroadcastReceiveRecordDetailVO getReceiveRecord(String broadcastId, String receiverId, String receiverType);

    /**
     * 分页查询接收记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<BroadcastReceiveRecordPageVO> queryReceiveRecordPage(BroadcastReceiveRecordQueryDTO queryDTO);
    
    /**
     * 列表查询接收记录
     *
     * @param queryDTO 查询条件
     * @return 列表结果
     */
    List<BroadcastReceiveRecordDetailVO> queryReceiveRecordList(BroadcastReceiveRecordQueryDTO queryDTO);

    /**
     * 更新接收状态
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param receiveStatus 接收状态
     * @return 是否成功
     */
    boolean updateReceiveStatus(String broadcastId, String receiverId, String receiverType, String receiveStatus);

    /**
     * 标记为已送达
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 是否成功
     */
    boolean markAsDelivered(String broadcastId, String receiverId, String receiverType);

    /**
     * 批量标记为已送达
     *
     * @param broadcastId 广播ID
     * @param receiverIds 接收者ID列表
     * @param receiverType 接收者类型
     * @return 成功标记数量
     */
    int batchMarkAsDelivered(String broadcastId, List<String> receiverIds, String receiverType);

    /**
     * 更新阅读状态
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态
     * @return 是否成功
     */
    boolean updateReadStatus(String broadcastId, String receiverId, String receiverType, Integer readStatus);

    /**
     * 标记为已读
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 是否成功
     */
    boolean markAsRead(String broadcastId, String receiverId, String receiverType);

    /**
     * 批量标记为已读
     *
     * @param broadcastId 广播ID
     * @param receiverIds 接收者ID列表
     * @param receiverType 接收者类型
     * @return 成功标记数量
     */
    int batchMarkAsRead(String broadcastId, List<String> receiverIds, String receiverType);

    /**
     * 根据接收者标记广播为已读
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param broadcastIds 广播ID列表（可选，为空则标记所有未读广播）
     * @return 成功标记数量
     */
    Boolean markAsReadByReceiver(String receiverId, String receiverType, List<String> broadcastIds);

    /**
     * 获取广播接收统计
     *
     * @param broadcastId 广播ID
     * @return 统计信息VO
     */
    BroadcastReceiveRecordStatisticsVO getBroadcastReceiveStatistics(String broadcastId);

    /**
     * 查询广播未读接收者
     *
     * @param broadcastId 广播ID
     * @param limit 限制数量
     * @return 未读接收者列表
     */
    List<UnreadReceiverVO> getUnreadReceivers(String broadcastId, int limit);

    /**
     * 查询接收者的广播记录
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param readStatus 阅读状态（可选）
     * @param limit 限制数量
     * @return 接收记录列表
     */
    List<BroadcastReceiveRecordDetailVO> getReceiverBroadcasts(String receiverId, String receiverType,
                                                              Integer readStatus, int limit);

    /**
     * 删除接收记录
     *
     * @param broadcastId 广播ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 是否成功
     */
    boolean deleteReceiveRecord(String broadcastId, String receiverId, String receiverType);

    /**
     * 根据广播ID删除接收记录
     *
     * @param broadcastId 广播ID
     * @return 删除的记录数量
     */
    long deleteByBroadcastId(String broadcastId);
}