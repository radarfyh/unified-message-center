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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgCallback;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigRequest;
import ltd.huntinginfo.feng.center.api.json.CallbackConfigResponse;
import ltd.huntinginfo.feng.center.api.dto.CallbackQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.CallbackDetailVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackPageVO;
import ltd.huntinginfo.feng.center.api.vo.CallbackStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 回调记录表服务接口
 */
public interface UmpMsgCallbackService extends IService<UmpMsgCallback> {

    /**
     * 创建回调记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @param callbackUrl 回调地址
     * @param callbackMethod 回调方法
     * @param callbackData 回调数据
     * @param signature 回调签名（可选）
     * @param callbackId 回调ID（可选）
     * @return 回调记录ID
     */
    String createCallback(String msgId, String callbackUrl, 
    		Map<String, Object> callbackData, String signature);

    /**
     * 批量创建回调记录
     *
     * @param callbacks 回调记录列表
     * @return 成功创建数量
     */
    int batchCreateCallbacks(List<UmpMsgCallback> callbacks);

    /**
     * 根据消息ID和接收者ID查询回调记录
     *
     * @param msgId 消息ID
     * @param receiverId 接收者ID
     * @return 回调记录列表
     */
    List<CallbackDetailVO> getCallbacksByMsgAndReceiver(String msgId, String receiverId);

    /**
     * 分页查询回调记录
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<CallbackPageVO> queryCallbackPage(CallbackQueryDTO queryDTO);

    /**
     * 获取回调记录详情
     *
     * @param callbackId 回调记录ID
     * @return 回调记录详情VO
     */
    CallbackDetailVO getCallbackDetail(String callbackId);

    /**
     * 更新回调状态
     *
     * @param callbackId 回调记录ID
     * @param status 状态
     * @param responseBody 响应内容（可选）
     * @param errorMessage 错误信息（可选）
     * @return 是否成功
     */
    boolean updateCallbackStatus(String callbackId, String status, 
    		Map<String, Object> responseBody, String errorMessage);

    /**
     * 标记回调为处理中
     *
     * @param callbackId 回调记录ID
     * @return 是否成功
     */
    boolean markAsProcessing(String callbackId);

    /**
     * 标记回调为成功
     *
     * @param callbackId 回调记录ID
     * @param responseBody 响应内容
     * @return 是否成功
     */
    boolean markAsSuccess(String callbackId, Map<String, Object> responseBody);

    /**
     * 标记回调为失败
     *
     * @param callbackId 回调记录ID
     * @param errorMessage 错误信息
     * @return 是否成功
     */
    boolean markAsFailed(String callbackId, String errorMessage);


    /**
     * 获取回调统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param msgId 消息ID（可选）
     * @return 统计信息VO
     */
    CallbackStatisticsVO getCallbackStatistics(LocalDateTime startTime,
                                             LocalDateTime endTime,
                                             String msgId);

    /**
     * 删除回调记录
     *
     * @param callbackId 回调记录ID
     * @return 是否成功
     */
    boolean deleteCallback(String callbackId);

    /**
     * 根据消息ID删除回调记录
     *
     * @param msgId 消息ID
     * @return 删除的记录数量
     */
    long deleteByMsgId(String msgId);
}