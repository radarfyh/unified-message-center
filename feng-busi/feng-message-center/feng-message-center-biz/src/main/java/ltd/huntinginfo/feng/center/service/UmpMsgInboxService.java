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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.dto.InboxQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxPageVO;
import ltd.huntinginfo.feng.center.api.vo.ReceiverStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收件箱表服务接口
 */
public interface UmpMsgInboxService extends IService<UmpMsgInbox> {

    /**
     * 分页查询收件箱
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<InboxPageVO> queryInboxPage(InboxQueryDTO queryDTO);
    
    /**
     * 列表查询收件箱
     *
     * @param queryDTO 查询条件
     * @return 列表结果
     */
    List<InboxDetailVO> queryInboxList(InboxQueryDTO queryDTO);

    /**
     * 查询收件箱详情
     *
     * @param inboxId 收件箱记录ID
     * @return 收件箱详情VO
     */
    InboxDetailVO getInboxDetail(String inboxId);

    /**
     * 根据消息和接收者查询收件箱记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 收件箱记录
     */
    InboxDetailVO getByMsgAndReceiver(String msgId, String receiverId, String receiverType);

    /**
     * 创建收件箱记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param receiverName 接收者名称
     * @param distributeMode 分发方式
     * @return 收件箱记录ID
     */
    String createInboxRecord(String msgId, MessageRecipient recipient, String distributeMode);

    /**
     * 批量创建收件箱记录
     *
     * @param msgId 消息ID
     * @param receiverList 接收者列表
     * @param distributeMode 分发方式
     * @return 成功创建的记录数量
     */
    int batchCreateInboxRecords(String msgId, List<MessageRecipient> receivers, String distributeMode);

    /**
     * 标记消息为已接收
     *
     * @param inboxId 收件箱记录ID
     * @return 是否成功
     */
    boolean markAsReceived(String inboxId);

    /**
     * 标记消息为已读
     *
     * @param inboxId 收件箱记录ID
     * @return 是否成功
     */
    boolean markAsRead(String inboxId);

    /**
     * 批量标记消息为已读
     *
     * @param inboxIds 收件箱记录ID列表
     * @return 成功标记数量
     */
    int batchMarkAsRead(List<String> inboxIds);

    /**
     * 根据接收者标记消息为已读
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param msgIds 消息ID列表（可选，为空则标记所有未读消息）
     * @return 成功标记数量
     */
    int markAsReadByReceiver(String receiverId, String receiverType, List<String> msgIds);

    /**
     * 更新推送状态
     *
     * @param inboxId 收件箱记录ID
     * @param pushStatus 推送状态
     * @param errorMessage 错误信息（可选）
     * @return 是否成功
     */
    boolean updatePushStatus(String inboxId, String pushStatus, String errorMessage);

    /**
     * 统计接收者的未读消息数量
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 未读消息数量
     */
    Integer countUnreadMessages(String receiverId, String receiverType);

    /**
     * 获取接收者的消息统计
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 统计信息VO
     */
    ReceiverStatisticsVO getReceiverStatistics(String receiverId, String receiverType, 
                                              LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除收件箱记录（逻辑删除）
     *
     * @param inboxId 收件箱记录ID
     * @return 是否成功
     */
    boolean deleteInboxRecord(String inboxId);

    /**
     * 根据消息ID删除相关收件箱记录
     *
     * @param msgId 消息ID
     * @return 删除的记录数量
     */
    int deleteByMsgId(String msgId);
}