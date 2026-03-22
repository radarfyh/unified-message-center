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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.center.api.entity.UmpBroadcastReceiveRecord;
import ltd.huntinginfo.feng.center.mapper.UmpBroadcastReceiveRecordMapper;
import ltd.huntinginfo.feng.center.service.UmpBroadcastReceiveRecordService;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageBroadcastStatus;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageReadStatus;
import ltd.huntinginfo.feng.center.api.dto.BroadcastReceiveRecordQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastReceiveRecordStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.UnreadReceiverVO;
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
 * 广播消息接收记录表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpBroadcastReceiveRecordServiceImpl extends ServiceImpl<UmpBroadcastReceiveRecordMapper, UmpBroadcastReceiveRecord> implements UmpBroadcastReceiveRecordService {

    private final UmpBroadcastReceiveRecordMapper umpBroadcastReceiveRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean upsertReceiveRecord(String broadcastId, String receiverId, String receiverType) {
        if (!StringUtils.hasText(broadcastId) || !StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("广播ID、接收者ID和接收者类型不能为空");
        }
        
        // 创建新记录
        UmpBroadcastReceiveRecord record = new UmpBroadcastReceiveRecord();
        record.setBroadcastId(broadcastId);
        record.setReceiverId(receiverId);
        record.setReceiverType(receiverType);
        record.setReceiveStatus(MessageBroadcastStatus.PENDING.getCode());
        record.setReadStatus(MessageReadStatus.UNREAD.getCode());
        List<UmpBroadcastReceiveRecord> list = new ArrayList<>();
        list.add(record);
        int count = umpBroadcastReceiveRecordMapper.batchUpsert(list);

        if (count > 0) {
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpsertReceiveRecords(String broadcastId, List<Map<String, Object>> receivers) {
        if (!StringUtils.hasText(broadcastId) || CollectionUtils.isEmpty(receivers)) {
            throw new IllegalArgumentException("广播ID和接收者列表不能为空");
        }

        List<UmpBroadcastReceiveRecord> records = new ArrayList<>();
        
        for (Map<String, Object> receiver : receivers) {
            String receiverId = (String) receiver.get("receiverId");
            String receiverType = (String) receiver.get("receiverType");
            
            if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
                continue;
            }

            UmpBroadcastReceiveRecord record = new UmpBroadcastReceiveRecord();
            record.setBroadcastId(broadcastId);
            record.setReceiverId(receiverId);
            record.setReceiverType(receiverType);
            record.setReceiveStatus(MessageBroadcastStatus.PENDING.getCode());
            record.setReadStatus(MessageReadStatus.UNREAD.getCode());
            
            records.add(record);
        }

        if (!CollectionUtils.isEmpty(records)) {
            int upsertCount = umpBroadcastReceiveRecordMapper.batchUpsert(records);
            log.info("批量创建接收记录成功，广播ID: {}, 数量: {}", broadcastId, upsertCount);
            return upsertCount;
        }
        
        return 0;
    }

    @Override
    public BroadcastReceiveRecordDetailVO getReceiveRecord(String broadcastId, String receiverId, String receiverType) {
        UmpBroadcastReceiveRecord record = umpBroadcastReceiveRecordMapper.selectByPrimaryKey(
                broadcastId, receiverId, receiverType);
        if (record == null) {
            log.warn("接收记录不存在，广播ID: {}, 接收者: {}", broadcastId, receiverId);
            return null;
        }
        return convertToDetailVO(record);
    }

    @Override
    public Page<BroadcastReceiveRecordPageVO> queryReceiveRecordPage(BroadcastReceiveRecordQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpBroadcastReceiveRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getBroadcastId())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getBroadcastId, queryDTO.getBroadcastId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverId())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReceiverId, queryDTO.getReceiverId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverType())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReceiverType, queryDTO.getReceiverType());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiveStatus())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReceiveStatus, queryDTO.getReceiveStatus());
        }
        
        if (queryDTO.getReadStatus() != null) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReadStatus, queryDTO.getReadStatus());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpBroadcastReceiveRecord::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpBroadcastReceiveRecord::getCreateTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getUpdateTime);
        }

        // 执行分页查询
        Page<UmpBroadcastReceiveRecord> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpBroadcastReceiveRecord> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<BroadcastReceiveRecordPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<BroadcastReceiveRecordPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public List<BroadcastReceiveRecordDetailVO> queryReceiveRecordList(BroadcastReceiveRecordQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpBroadcastReceiveRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(queryDTO.getBroadcastId())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getBroadcastId, queryDTO.getBroadcastId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverId())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReceiverId, queryDTO.getReceiverId());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiverType())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReceiverType, queryDTO.getReceiverType());
        }
        
        if (StringUtils.hasText(queryDTO.getReceiveStatus())) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReceiveStatus, queryDTO.getReceiveStatus());
        }
        
        if (queryDTO.getReadStatus() != null) {
            queryWrapper.eq(UmpBroadcastReceiveRecord::getReadStatus, queryDTO.getReadStatus());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpBroadcastReceiveRecord::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpBroadcastReceiveRecord::getCreateTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getUpdateTime);
        }

        // 执行分页查询
        List<UmpBroadcastReceiveRecord> resultList = list(queryWrapper);

        // 转换为VO       
        List<BroadcastReceiveRecordDetailVO> voList = resultList.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
        
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReceiveStatus(String broadcastId, String receiverId, String receiverType, String receiveStatus) {
        if (!StringUtils.hasText(broadcastId) || !StringUtils.hasText(receiverId) || 
            !StringUtils.hasText(receiverType) || !StringUtils.hasText(receiveStatus)) {
            throw new IllegalArgumentException("参数不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime receiveTime = MessageBroadcastStatus.COMPLETED.getCode().equals(receiveStatus) ? now : null;       
        
        int updated = umpBroadcastReceiveRecordMapper.updateReceiveStatus(
                broadcastId, receiverId, receiverType, receiveStatus, receiveTime);
        
        boolean success = updated > 0;
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsDelivered(String broadcastId, String receiverId, String receiverType) {
        return updateReceiveStatus(broadcastId, receiverId, receiverType, MessageBroadcastStatus.COMPLETED.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchMarkAsDelivered(String broadcastId, List<String> receiverIds, String receiverType) {
        if (!StringUtils.hasText(broadcastId) || CollectionUtils.isEmpty(receiverIds) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("参数不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = umpBroadcastReceiveRecordMapper.batchUpdateReceiveStatus(broadcastId, receiverIds, receiverType, MessageBroadcastStatus.COMPLETED.getCode(), now);
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReadStatus(String broadcastId, String receiverId, String receiverType, Integer readStatus) {
        if (!StringUtils.hasText(broadcastId) || !StringUtils.hasText(receiverId) || 
            !StringUtils.hasText(receiverType) || readStatus == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime readTime = readStatus == MessageReadStatus.READ.getCode() ? now : null;
        
        int updated = umpBroadcastReceiveRecordMapper.updateReadStatus(
                broadcastId, receiverId, receiverType, readStatus, readTime);
        
        boolean success = updated > 0;
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(String broadcastId, String receiverId, String receiverType) {
        return updateReadStatus(broadcastId, receiverId, receiverType, MessageReadStatus.READ.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchMarkAsRead(String broadcastId, List<String> receiverIds, String receiverType) {
        if (!StringUtils.hasText(broadcastId) || CollectionUtils.isEmpty(receiverIds) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("参数不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        int updatedCount = umpBroadcastReceiveRecordMapper.batchUpdateReadStatus(
                broadcastId, receiverIds, receiverType, MessageReadStatus.READ.getCode(), now);
        
        return updatedCount;
    }

    @Override
    public Boolean markAsReadByReceiver(String receiverId, String receiverType, List<String> broadcastIds) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        LambdaUpdateWrapper<UmpBroadcastReceiveRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UmpBroadcastReceiveRecord::getReceiverId, receiverId)
                     .eq(UmpBroadcastReceiveRecord::getReceiverType, receiverType)
                     .eq(UmpBroadcastReceiveRecord::getReadStatus, MessageReadStatus.UNREAD.getCode())
                     .in(!CollectionUtils.isEmpty(broadcastIds), 
                         UmpBroadcastReceiveRecord::getBroadcastId, broadcastIds)
                     .set(UmpBroadcastReceiveRecord::getReadStatus, MessageReadStatus.READ.getCode())
                     .set(UmpBroadcastReceiveRecord::getReadTime, LocalDateTime.now());
        return update(updateWrapper);
    }

    @Override
    public BroadcastReceiveRecordStatisticsVO getBroadcastReceiveStatistics(String broadcastId) {
        if (!StringUtils.hasText(broadcastId)) {
            throw new IllegalArgumentException("广播ID不能为空");
        }

        Map<String, Object> statsMap = umpBroadcastReceiveRecordMapper.countByBroadcast(broadcastId);
        
        BroadcastReceiveRecordStatisticsVO statisticsVO = new BroadcastReceiveRecordStatisticsVO();
        statisticsVO.setBroadcastId(broadcastId);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setPendingCount(((Number) statsMap.getOrDefault("pending_count", 0)).longValue());
            statisticsVO.setDeliveredCount(((Number) statsMap.getOrDefault("delivered_count", 0)).longValue());
            statisticsVO.setFailedCount(((Number) statsMap.getOrDefault("failed_count", 0)).longValue());
            statisticsVO.setUnreadCount(((Number) statsMap.getOrDefault("unread_count", 0)).longValue());
            statisticsVO.setReadCount(((Number) statsMap.getOrDefault("read_count", 0)).longValue());
            
            // 计算比率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setDeliveredRate((double) statisticsVO.getDeliveredCount() / statisticsVO.getTotalCount() * 100);
                statisticsVO.setReadRate((double) statisticsVO.getReadCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public List<UnreadReceiverVO> getUnreadReceivers(String broadcastId, int limit) {
        if (!StringUtils.hasText(broadcastId)) {
            throw new IllegalArgumentException("广播ID不能为空");
        }

        List<UnreadReceiverVO> list = umpBroadcastReceiveRecordMapper.selectUnreadReceivers(broadcastId, limit);
        return list;
    }

    @Override
    public List<BroadcastReceiveRecordDetailVO> getReceiverBroadcasts(String receiverId, String receiverType,
                                                                     Integer readStatus, int limit) {
        if (!StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("接收者ID和接收者类型不能为空");
        }

        List<UmpBroadcastReceiveRecord> records = umpBroadcastReceiveRecordMapper.selectByReceiver(
                receiverId, receiverType, readStatus, limit);
        
        return records.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReceiveRecord(String broadcastId, String receiverId, String receiverType) {
        if (!StringUtils.hasText(broadcastId) || !StringUtils.hasText(receiverId) || !StringUtils.hasText(receiverType)) {
            throw new IllegalArgumentException("参数不能为空");
        }

        LambdaQueryWrapper<UmpBroadcastReceiveRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpBroadcastReceiveRecord::getBroadcastId, broadcastId)
                   .eq(UmpBroadcastReceiveRecord::getReceiverId, receiverId)
                   .eq(UmpBroadcastReceiveRecord::getReceiverType, receiverType);
        
        boolean success = remove(queryWrapper);
        if (success) {
            log.info("接收记录删除成功，广播ID: {}, 接收者: {}", broadcastId, receiverId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long deleteByBroadcastId(String broadcastId) {
        if (!StringUtils.hasText(broadcastId)) {
            throw new IllegalArgumentException("广播ID不能为空");
        }

        LambdaQueryWrapper<UmpBroadcastReceiveRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpBroadcastReceiveRecord::getBroadcastId, broadcastId);
        
        long count = count(queryWrapper);
        boolean success = remove(queryWrapper);
        
        if (success) {
            log.info("根据广播ID删除接收记录成功，广播ID: {}, 数量: {}", broadcastId, count);
            return count;
        }
        
        return 0L;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpBroadcastReceiveRecord> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpBroadcastReceiveRecord::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpBroadcastReceiveRecord::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getUpdateTime);
                }
                break;
            case "receiveTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpBroadcastReceiveRecord::getReceiveTime);
                } else {
                    queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getReceiveTime);
                }
                break;
            case "readTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpBroadcastReceiveRecord::getReadTime);
                } else {
                    queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getReadTime);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpBroadcastReceiveRecord::getUpdateTime);
                break;
        }
    }

    private BroadcastReceiveRecordPageVO convertToPageVO(UmpBroadcastReceiveRecord record) {
        BroadcastReceiveRecordPageVO vo = new BroadcastReceiveRecordPageVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    private BroadcastReceiveRecordDetailVO convertToDetailVO(UmpBroadcastReceiveRecord record) {
        BroadcastReceiveRecordDetailVO vo = new BroadcastReceiveRecordDetailVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}