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
package ltd.huntinginfo.feng.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgInboxService;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageProcessStatus;
import ltd.huntinginfo.feng.common.core.constant.enums.MessagePushStatus;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageReadStatus;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.center.api.dto.InboxQueryDTO;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.vo.InboxDetailVO;
import ltd.huntinginfo.feng.center.api.vo.InboxPageVO;
import ltd.huntinginfo.feng.center.api.vo.ReceiverStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收件箱表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgInboxServiceImpl extends ServiceImpl<UmpMsgInboxMapper, UmpMsgInbox> implements UmpMsgInboxService {
    @Override
    public Page<InboxPageVO> queryInboxPage(InboxQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getReceiverId())) {
            queryWrapper.eq(UmpMsgInbox::getReceiverId, queryDTO.getReceiverId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverType())) {
            queryWrapper.eq(UmpMsgInbox::getReceiverType, queryDTO.getReceiverType());
        }
        
        if (queryDTO.getReadStatus() != null) {
            queryWrapper.eq(UmpMsgInbox::getReadStatus, queryDTO.getReadStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiveStatus())) {
            queryWrapper.eq(UmpMsgInbox::getReceiveStatus, queryDTO.getReceiveStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgInbox::getMsgId, queryDTO.getMsgId());
        }
        
        if (StringUtils.hasText(queryDTO.getDistributeMode())) {
            queryWrapper.eq(UmpMsgInbox::getDistributeMode, queryDTO.getDistributeMode());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgInbox::getDistributeTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgInbox::getDistributeTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
        }

        // 执行分页查询
        Page<UmpMsgInbox> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgInbox> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<InboxPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<InboxPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public List<InboxDetailVO> queryInboxList(InboxQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getReceiverId())) {
            queryWrapper.eq(UmpMsgInbox::getReceiverId, queryDTO.getReceiverId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverType())) {
            queryWrapper.eq(UmpMsgInbox::getReceiverType, queryDTO.getReceiverType());
        }
        
        if (queryDTO.getReadStatus() != null) {
            queryWrapper.eq(UmpMsgInbox::getReadStatus, queryDTO.getReadStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiveStatus())) {
            queryWrapper.eq(UmpMsgInbox::getReceiveStatus, queryDTO.getReceiveStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getMsgId())) {
            queryWrapper.eq(UmpMsgInbox::getMsgId, queryDTO.getMsgId());
        }
        
        if (StringUtils.hasText(queryDTO.getDistributeMode())) {
            queryWrapper.eq(UmpMsgInbox::getDistributeMode, queryDTO.getDistributeMode());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgInbox::getDistributeTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgInbox::getDistributeTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
        }

        List<UmpMsgInbox> resultList = this.list(queryWrapper);

        List<InboxDetailVO> voList = resultList.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
        
        return voList;
    }

    private void applySort(LambdaQueryWrapper<UmpMsgInbox> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "distributeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getDistributeTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
                }
                break;
            case "receiveTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getReceiveTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getReceiveTime);
                }
                break;
            case "readTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getReadTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getReadTime);
                }
                break;
            case "lastPushTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgInbox::getLastPushTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgInbox::getLastPushTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgInbox::getDistributeTime);
                break;
        }
    }
    
    @Override
    public InboxDetailVO getInboxDetail(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return null;
        }
        return convertToDetailVO(inbox);
    }

    @Override
    public InboxDetailVO getByMsgAndReceiver(String msgId, String receiverId, String receiverType) {
        // 使用 Lambda 查询代替 Mapper 自定义方法
        UmpMsgInbox inbox = lambdaQuery()
                .eq(UmpMsgInbox::getMsgId, msgId)
                .eq(UmpMsgInbox::getReceiverId, receiverId)
                .eq(UmpMsgInbox::getReceiverType, receiverType)
                .one();
        if (inbox == null) {
            return null;
        }
        return convertToDetailVO(inbox);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createInboxRecord(String msgId, MessageRecipient recipient, String distributeMode) {
    	if (StrUtil.isBlank(msgId)) {
            log.error("createInboxRecord 入参消息ID为空");
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "createInboxRecord 入参消息ID为空");
    	}
    	
    	if (BeanUtil.isEmpty(recipient)) {
            log.error("createInboxRecord 入参个人信息为空");
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "createInboxRecord 入参个人信息为空");
    	}
    	
    	UmpMsgInbox inbox = buildInbox(msgId, recipient, distributeMode);
    	if (inbox == null) {
    		return null;
    	}

        if (save(inbox)) {
            log.info("收件箱记录创建成功，消息ID: {}, 接收者ID: {}", msgId, inbox.getReceiverId());
            return inbox.getId();
        } else {
            log.error("收件箱记录创建失败，消息ID: {}, 接收者ID: {}", msgId, inbox.getReceiverId());
            throw new BusinessException(BusinessEnum.UMP_CREATE_FAILED.getCode(), "createInboxRecord 收件箱记录创建失败"); 
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateInboxRecords(String msgId, List<MessageRecipient> receivers, String distributeMode) {
        if (CollectionUtils.isEmpty(receivers)) {
            return 0;
        }

        int createdCount = 0;
        List<UmpMsgInbox> inboxList = new ArrayList<>();

        for (MessageRecipient recipient : receivers) {
        	UmpMsgInbox inbox = buildInbox(msgId, recipient, distributeMode);

            if (inbox != null) {
            	inboxList.add(inbox);
            	createdCount++;
            }            
        }

        if (!CollectionUtils.isEmpty(inboxList)) {
            saveBatch(inboxList);
            log.info("批量创建收件箱记录成功，消息ID: {}, 数量: {}", msgId, createdCount);
        }

        return createdCount;
    }

    private UmpMsgInbox buildInbox(String msgId, MessageRecipient recipient, String distributeMode) {
        String receiverId = recipient.getReceiverId();
        String receiverType = recipient.getReceiverType();

        // 检查是否已存在 - 使用 Lambda 查询
        long count = lambdaQuery()
                .eq(UmpMsgInbox::getMsgId, msgId)
                .eq(UmpMsgInbox::getReceiverId, receiverId)
                .eq(UmpMsgInbox::getReceiverType, receiverType)
                .count();
        if (count > 0) {
            log.warn("收件箱记录已存在，消息ID: {}, 接收者ID: {}", msgId, receiverId);
            return null;
        }

        UmpMsgInbox inbox = new UmpMsgInbox();
        inbox.setMsgId(msgId);
        inbox.setReceiverId(receiverId);
        inbox.setReceiverType(receiverType);
        inbox.setReceiverName(recipient.getReceiverName());
        inbox.setReceiverPhone(recipient.getReceiverPhone());
        inbox.setReceiverIdNumber(recipient.getReceiverIdNumber());
        inbox.setReceivingUnitId(recipient.getReceivingUnitId());
        inbox.setReceivingUnitCode(recipient.getReceivingUnitCode());
        inbox.setReceivingUnitName(recipient.getReceivingUnitName());
        inbox.setDistributeMode(StringUtils.hasText(distributeMode) ? distributeMode : MqMessageEventConstants.DistributeModes.INBOX);
        inbox.setDistributeTime(LocalDateTime.now());
        inbox.setReceiveStatus(MessageProcessStatus.PENDING.getCode());
        inbox.setReceiveTime(null);
        inbox.setReadTime(null);
        inbox.setReadStatus(MessageReadStatus.UNREAD.getCode());
        inbox.setPushStatus(MessagePushStatus.PENDING.getCode());
        inbox.setPushCount(0);
        inbox.setLastPushTime(null);
        inbox.setErrorMessage(null);
        return inbox;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsReceived(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        if (MessageProcessStatus.SUCCESS.getCode().equals(inbox.getReceiveStatus())) {
            log.debug("收件箱记录已是已接收状态，ID: {}", inboxId);
            return true;
        }

        inbox.setReceiveStatus(MessageProcessStatus.SUCCESS.getCode());
        inbox.setReceiveTime(LocalDateTime.now());
        
        boolean success = updateById(inbox);
       
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        if (inbox.getReadStatus() == MessageReadStatus.READ.getCode()) {
            log.debug("收件箱记录已是已读状态，ID: {}", inboxId);
            return true;
        }

        inbox.setReadStatus(MessageReadStatus.READ.getCode());
        inbox.setReadTime(LocalDateTime.now());
        
        boolean success = updateById(inbox);
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchMarkAsRead(List<String> inboxIds) {
        if (CollectionUtils.isEmpty(inboxIds)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = baseMapper.batchUpdateReadStatus(inboxIds, MessageReadStatus.READ.getCode(), now);
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markAsReadByReceiver(String receiverId, String receiverType, List<String> msgIds) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgInbox::getReceiverId, receiverId)
                   .eq(UmpMsgInbox::getReceiverType, receiverType)
                   .eq(UmpMsgInbox::getReadStatus, MessageReadStatus.UNREAD.getCode());

        if (!CollectionUtils.isEmpty(msgIds)) {
            queryWrapper.in(UmpMsgInbox::getMsgId, msgIds);
        }

        List<UmpMsgInbox> unreadInboxes = list(queryWrapper);
        if (CollectionUtils.isEmpty(unreadInboxes)) {
            return 0;
        }

        List<String> inboxIds = unreadInboxes.stream()
                .map(UmpMsgInbox::getId)
                .collect(Collectors.toList());

        return batchMarkAsRead(inboxIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePushStatus(String inboxId, String pushStatus, String errorMessage) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        int pushCount = inbox.getPushCount() + 1;
        LocalDateTime lastPushTime = LocalDateTime.now();

        int updated = baseMapper.updatePushStatus(inboxId, pushStatus, pushCount, lastPushTime, errorMessage);

        return updated > 0;
    }

    @Override
    public Integer countUnreadMessages(String receiverId, String receiverType) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }
        
        return baseMapper.countUnreadByReceiver(receiverId, receiverType);
    }

    @Override
    public ReceiverStatisticsVO getReceiverStatistics(String receiverId, String receiverType, 
                                                     LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statsMap = baseMapper.selectReceiverStatistics(receiverId, receiverType, startTime, endTime);
        
        ReceiverStatisticsVO statisticsVO = new ReceiverStatisticsVO();
        statisticsVO.setReceiverId(receiverId);
        statisticsVO.setReceiverType(receiverType);
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).intValue());
            statisticsVO.setUnreadCount(((Number) statsMap.getOrDefault("unread_count", 0)).intValue());
            statisticsVO.setReadCount(((Number) statsMap.getOrDefault("read_count", 0)).intValue());
            statisticsVO.setReceivedCount(((Number) statsMap.getOrDefault("received_count", 0)).intValue());
            statisticsVO.setFailedCount(((Number) statsMap.getOrDefault("failed_count", 0)).intValue());
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInboxRecord(String inboxId) {
        UmpMsgInbox inbox = getById(inboxId);
        if (inbox == null) {
            log.warn("收件箱记录不存在，ID: {}", inboxId);
            return false;
        }

        // 逻辑删除，实际业务可能需要物理删除
        boolean success = removeById(inboxId);
        if (success) {
            log.info("收件箱记录删除成功，ID: {}", inboxId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByMsgId(String msgId) {
        LambdaQueryWrapper<UmpMsgInbox> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpMsgInbox::getMsgId, msgId);
        
        long deletedCount = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据消息ID删除收件箱记录成功，消息ID: {}, 数量: {}", msgId, deletedCount);
            return (int) deletedCount;
        }
        
        return 0;
    }

    // ============ 私有方法 ============

    private InboxPageVO convertToPageVO(UmpMsgInbox inbox) {
        InboxPageVO vo = new InboxPageVO();
        BeanUtils.copyProperties(inbox, vo);
        return vo;
    }

    private InboxDetailVO convertToDetailVO(UmpMsgInbox inbox) {
        InboxDetailVO vo = new InboxDetailVO();
        BeanUtils.copyProperties(inbox, vo);
        return vo;
    }
}