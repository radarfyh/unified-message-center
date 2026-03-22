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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.center.api.dto.BroadcastQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 广播信息筒表服务接口
 */
public interface UmpMsgBroadcastService extends IService<UmpMsgBroadcast> {

    /**
     * 创建广播记录
     *
     * @param msgId 消息ID
     * @param broadcastType 广播类型
     * @param targetScope 目标范围配置：只有接收者类型为CUSTOM时targetScope不为空
     * @param targetDescription 目标范围描述
     * @return 广播记录ID
     */
    String createBroadcast(String msgId, String broadcastType, MessageReceivingUnit unit,
    		ReceivingScope targetScope, String targetDescription);

    /**
     * 根据消息ID查询广播记录
     *
     * @param msgId 消息ID
     * @return 广播记录详情VO
     */
    BroadcastDetailVO getBroadcastByMsgId(String msgId);

    /**
     * 分页查询广播记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<BroadcastPageVO> queryBroadcastPage(BroadcastQueryDTO queryDTO);

    /**
     * 更新广播统计信息
     *
     * @param broadcastId 广播ID
     * @param distributedCount 已分发数量
     * @param receivedCount 已接收数量
     * @param readCount 已读人数
     * @return 是否成功
     */
    boolean updateBroadcastStatistics(String broadcastId, Integer distributedCount,
                                     Integer receivedCount, Integer readCount);

    /**
     * 更新广播状态
     *
     * @param broadcastId 广播ID
     * @param status 目标状态
     * @return 是否成功
     */
    boolean updateBroadcastStatus(String broadcastId, String status);

    /**
     * 批量更新广播状态
     *
     * @param broadcastIds 广播ID列表
     * @param status 目标状态
     * @return 成功更新数量
     */
    int batchUpdateBroadcastStatus(List<String> broadcastIds, String status);

    /**
     * 标记广播为分发中
     *
     * @param broadcastId 广播ID
     * @return 是否成功
     */
    boolean markAsDistributing(String broadcastId);

    /**
     * 标记广播为完成
     *
     * @param broadcastId 广播ID
     * @return 是否成功
     */
    boolean markAsCompleted(String broadcastId);

    /**
     * 获取广播统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param broadcastType 广播类型（可选）
     * @return 统计信息VO
     */
    BroadcastStatisticsVO getBroadcastStatistics(LocalDateTime startTime,
                                                LocalDateTime endTime,
                                                String broadcastType);

    /**
     * 已读数量加一
     * @param broadcastId
     */
	void incrementReadCount(String broadcastId);

	/**
	 * 修改已分发数量
	 * @param broadcastId
	 * @param distributedCount 已分发数量
	 * @return
	 */
	boolean updateDistributedCount(String broadcastId, Integer distributedCount);

	/**
	 * 修改已接收数量
	 * @param broadcastId
	 * @param receivedCount 已接收数量
	 * @return
	 */
	boolean updateReceivedCount(String broadcastId, Integer receivedCount);

	/**
	 * 修改已读人数
	 * @param broadcastId
	 * @param readCount 已读人数
	 * @return
	 */
	boolean updateReadCount(String broadcastId, Integer readCount);

	/**
	 * 更新广播总接收人数（新增方法）
	 */
	boolean updateTotalReceivers(String broadcastId, Integer totalReceivers);
}