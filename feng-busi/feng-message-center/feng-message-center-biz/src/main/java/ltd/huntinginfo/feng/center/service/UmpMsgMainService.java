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

import jakarta.validation.Valid;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.json.MessageReceiver;
import ltd.huntinginfo.feng.center.api.json.TaskData;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.dto.MsgCodingDTO;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessagePollRequest;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessageUnreadRequest;
import ltd.huntinginfo.feng.center.api.dto.MessageQueryDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageReceivingUnitDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageRecipientDTO;
import ltd.huntinginfo.feng.center.api.vo.MessageDetailVO;
import ltd.huntinginfo.feng.center.api.vo.MessagePageVO;
import ltd.huntinginfo.feng.center.api.vo.MessageStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.MsgCodingVO;
import ltd.huntinginfo.feng.center.api.vo.UnifiedMessageDetail;
import ltd.huntinginfo.feng.common.core.mq.dto.MqMessage;
import ltd.huntinginfo.feng.common.core.util.UnifiedMessageResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息主表服务接口
 */
public interface UmpMsgMainService extends IService<UmpMsgMain> {

    /**
     * 创建消息
     *
     * @param sendDTO 消息发送DTO
     * @return 消息ID
     */
    String createMessage(MessageSendDTO sendDTO);

    /**
     * 创建代理消息
     *
     * @param sendDTO 消息发送DTO
     * @param agentAppKey 代理平台标识
     * @param agentMsgId 代理消息ID
     * @return 消息ID
     */
    String createAgentMessage(MessageSendDTO sendDTO, String agentAppKey, String agentMsgId);

    /**
     * 根据消息编码查询消息详情
     *
     * @param msgCode 消息编码
     * @return 消息详情VO
     */
    MessageDetailVO getMessageByCode(String msgCode);
    
    /**
     * 根据消息ID查询消息详情
     *
     * @param msgId 消息ID
     * @return 消息详情VO
     */
    MessageDetailVO getMessageById(String msgId);

    /**
     * 分页查询消息
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<MessagePageVO> queryMessagePage(MessageQueryDTO queryDTO);

    /**
     * 更新消息状态
     *
     * @param message 消息
     * @param status 目标状态
     * @return 是否成功
     */
    boolean updateMessageStatus(String status, MqMessage<TaskData> message);
    
    /**
     * 更新消息的已读统计
     *
     * @param msgId 消息ID
     * @param readCount 已读人数
     * @return 是否成功
     */
    boolean updateReadStatistics(String msgId, int readCount);

    /**
     * 获取消息统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param appKey 应用标识（可选）
     * @return 统计信息VO
     */
    MessageStatisticsVO getMessageStatistics(LocalDateTime startTime, LocalDateTime endTime, String appKey);
    
    /**
     * 根据应用key和消息类型统计
     *
     * @param targetDate 统计日期
     * @return 统计信息VO
     */
    MessageStatisticsVO getMessageStatisticsGroupByAppAndType(LocalDate targetDate);

    /**
     * 根据接收者ID查询未读消息(可以读，但是未读）
     *
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @param limit 限制数量
     * @return 未读消息列表
     */
    List<MessageDetailVO> getUnreadMessages(String receiverId, String receiverType, int limit);
    
    /**
     * 获取过期消息
     * @param expiredTime
     * @return
     */
    List<UmpMsgMain> getExpiredMessages(LocalDateTime expiredTime);

    /**
     * 检查消息是否存在且有效
     *
     * @param msgId 消息ID
     * @return 是否存在且未删除
     */
    boolean existsAndValid(String msgId);
    
    /**
     * 更新接收者统计信息
     */
    boolean updateReceiverCount(String messageId, Integer totalReceivers, 
                               Integer receivedCount, Integer readCount);
    /**
     * 查询未读消息
     */
	List<MessageDetailVO> getAllUnreadMessages(int limit);
	
	/**
	 * 根据app查询未读消息
	 * @param appKey 应用标识
	 * @param request 读取请求
	 * @return
	 */
	UnifiedMessageResponse getUnreadMessagesByApp(String appKey, UnifiedMessageUnreadRequest request);
	
	/**
	 * 根据游标查询未被业务系统接收的消息
	 * @param appKey 应用标识
	 * @param request 拉取请求
	 * @return
	 */
	UnifiedMessageResponse<UnifiedMessageDetail> getUnreceivedMessagesByCursor(String appKey, UnifiedMessagePollRequest request);

	/**
	 * 上报业务已接收状态
	 * @param messageId 消息标识
	 * @param receiverId 接收者标识
	 * @param receiverType 接收者类型
	 * @param broadcastId 广播标识
	 * @param appKey 应用标识
	 */
	void reportBizReceived(String messageId, String receiverId, String receiverType, String broadcastId, String appKey);

	/**
	 * 上报业务已拉取状态
	 * @param messageId 消息标识
	 * @param receiverId 接收者标识
	 * @param receiverType 接收者类型
	 * @param broadcastId 广播标识
	 * @param appKey 应用标识
	 */
	void reportBizPulled(String messageId, String receiverId, String receiverType, String broadcastId, String appKey);

	/**
	 * 上报业务已读状态
	 * @param messageId 消息标识
	 * @param receiverId 接收者标识
	 * @param receiverType 接收者类型
	 * @param broadcastId 广播标识
	 * @param appKey 应用标识
	 */
	void reportBizRead(String messageId, String receiverId, String receiverType, String broadcastId, String appKey);

	/**
	 * 生成32位消息编码
	 * @param request 申请信息
	 * @param appKey 应用Key
	 * @return
	 */
	MsgCodingVO generateMessageCode(@Valid MsgCodingDTO request, String appKey);
}
