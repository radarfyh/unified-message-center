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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.service.UmpMsgBroadcastService;
import ltd.huntinginfo.feng.common.core.constant.enums.MessageBroadcastStatus;
import ltd.huntinginfo.feng.common.core.constant.enums.MessagePushStatus;
import ltd.huntinginfo.feng.center.api.dto.BroadcastQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastDetailVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastPageVO;
import ltd.huntinginfo.feng.center.api.vo.BroadcastStatisticsVO;
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
 * 广播信息筒表服务实现类
 * 打印日志（@Slf4j），异常日志使用log.error,一般错误使用log.warn
 * 各个方法返回有效数据，一般不返回错误代码，错误代码（BusinessEnum）使用异常（BusinessException）来控制
 * 使用baseMapper访问自身数据库映射接口（xxxMapper）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpMsgBroadcastServiceImpl extends ServiceImpl<UmpMsgBroadcastMapper, UmpMsgBroadcast> implements UmpMsgBroadcastService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBroadcast(String msgId, String broadcastType, MessageReceivingUnit unit,
                                 ReceivingScope targetScope, String targetDescription) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }
        
        if (!StringUtils.hasText(broadcastType)) {
            throw new IllegalArgumentException("广播类型不能为空");
        }
        
        // 广播目标：DEPT时unit不能为空，CUSTOM时targetScope不能为空
        if (BeanUtil.isEmpty(unit) && BeanUtil.isEmpty(targetScope)) {
            throw new IllegalArgumentException("广播目标范围不能为空");
        }
        
    	// 检查是否已存在
        UmpMsgBroadcast existing = baseMapper.selectByMsgId(msgId);
        if (existing != null) {
            log.warn("广播记录已存在，消息ID: {}", msgId);
            return existing.getId();
        }

        // 创建新记录
        UmpMsgBroadcast broadcast = new UmpMsgBroadcast();
        broadcast.setMsgId(msgId);
        broadcast.setBroadcastType(broadcastType);
        broadcast.setReceivingUnitId(unit.getReceivingUnitId());
        broadcast.setReceivingUnitCode(unit.getReceivingUnitCode());
        broadcast.setReceivingUnitName(unit.getReceivingUnitName());
        
        // 只有接收者类型为CUSTOM的时候receiverScope才不为空
        broadcast.setReceivingScope(targetScope);
        broadcast.setReceivingDescription(targetDescription);
        broadcast.setStatus(MessageBroadcastStatus.DISTRIBUTING.getCode());
        broadcast.setStartTime(LocalDateTime.now());
        
        // 设置默认统计值
        broadcast.setTotalReceivers(0);
        broadcast.setDistributedCount(0);
        broadcast.setReceivedCount(0);
        broadcast.setReadCount(0);
        broadcast.setPushStatus(MessagePushStatus.PENDING.getCode());
        broadcast.setPushCount(0);

        if (save(broadcast)) {
            return broadcast.getId();
        } else {
            log.error("广播记录创建失败，消息ID: {}", msgId);
            throw new RuntimeException("广播记录创建失败");
        }
    }

    @Override
    public BroadcastDetailVO getBroadcastByMsgId(String msgId) {
        if (!StringUtils.hasText(msgId)) {
            throw new IllegalArgumentException("消息ID不能为空");
        }

        UmpMsgBroadcast broadcast = baseMapper.selectByMsgId(msgId);
        if (broadcast == null) {
            log.warn("广播记录不存在，消息ID: {}", msgId);
            return null;
        }

        return convertToDetailVO(broadcast);
    }

    @Override
    public Page<BroadcastPageVO> queryBroadcastPage(BroadcastQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpMsgBroadcast> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getBroadcastType())) {
            queryWrapper.eq(UmpMsgBroadcast::getBroadcastType, queryDTO.getBroadcastType());
        }
        
        if (StringUtils.hasText(queryDTO.getStatus())) {
            queryWrapper.eq(UmpMsgBroadcast::getStatus, queryDTO.getStatus());
        }
        
        if (queryDTO.getStartTime() != null) {
            queryWrapper.ge(UmpMsgBroadcast::getCreateTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            queryWrapper.le(UmpMsgBroadcast::getCreateTime, queryDTO.getEndTime());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpMsgBroadcast::getCreateTime);
        }

        // 执行分页查询
        Page<UmpMsgBroadcast> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpMsgBroadcast> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<BroadcastPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<BroadcastPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBroadcastStatistics(String broadcastId, Integer distributedCount,
                                           Integer receivedCount, Integer readCount) {
        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }

        String newStatus = calculateStatus(
        	    broadcast.getTotalReceivers(),
        	    distributedCount != null ? distributedCount : broadcast.getDistributedCount(),
        	    receivedCount != null ? receivedCount : broadcast.getReceivedCount(),
        	    readCount != null ? readCount : broadcast.getReadCount()
        	);
        
        int updated = baseMapper.updateBroadcastStatistics(
        	        broadcastId, distributedCount, receivedCount, readCount, newStatus);
        
        boolean success = updated > 0;
        if (success) {
            log.info("广播统计信息更新成功，广播ID: {}, 分发: {}, 接收: {}, 已读: {}", 
                    broadcastId, distributedCount, receivedCount, readCount);
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDistributedCount(String broadcastId, Integer distributedCount) {
        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }
        // 基于新的 distributedCount 计算状态
        String newStatus = calculateStatus(
            broadcast.getTotalReceivers(),
            distributedCount,
            broadcast.getReceivedCount(),
            broadcast.getReadCount()
        );
        int updated = baseMapper.updateDistributedCount(
                broadcastId, distributedCount, newStatus);
        boolean success = updated > 0;
        if (success) {
            log.info("广播统计信息更新成功，广播ID: {}, 分发: {}", broadcastId, distributedCount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReceivedCount(String broadcastId, Integer receivedCount) {
        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }
        String newStatus = calculateStatus(
            broadcast.getTotalReceivers(),
            broadcast.getDistributedCount(),
            receivedCount,
            broadcast.getReadCount()
        );
        int updated = baseMapper.updateReceivedCount(
                broadcastId, receivedCount, newStatus);
        boolean success = updated > 0;
        if (success) {
            log.info("广播统计信息更新成功，广播ID: {}, 接收: {}", broadcastId, receivedCount);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateReadCount(String broadcastId, Integer readCount) {
        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }
        String newStatus = calculateStatus(
            broadcast.getTotalReceivers(),
            broadcast.getDistributedCount(),
            broadcast.getReceivedCount(),
            readCount
        );
        int updated = baseMapper.updateReadCount(
                broadcastId, readCount, newStatus);
        boolean success = updated > 0;
        if (success) {
            log.info("广播统计信息更新成功，广播ID: {}, 已读: {}", broadcastId, readCount);
        }
        return success;
    }
    
    /**
     * 更新广播总接收人数（新增方法）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTotalReceivers(String broadcastId, Integer totalReceivers) {
        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }

        // 使用 lambda 更新，无需修改 Mapper
        boolean success = lambdaUpdate()
                .eq(UmpMsgBroadcast::getId, broadcastId)
                .set(UmpMsgBroadcast::getTotalReceivers, totalReceivers)
                .update();
        
        if (success) {
            log.info("广播总接收人数更新成功，广播ID: {}, 总接收人数: {}", broadcastId, totalReceivers);
        }
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBroadcastStatus(String broadcastId, String status) {
        if (!StringUtils.hasText(broadcastId) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("广播ID和状态不能为空");
        }

        UmpMsgBroadcast broadcast = getById(broadcastId);
        if (broadcast == null) {
            log.warn("广播记录不存在，ID: {}", broadcastId);
            return false;
        }

        broadcast.setStatus(status);
        
        // 根据状态设置相应的时间字段
        if (MessageBroadcastStatus.COMPLETED.getCode().equals(status) && broadcast.getCompleteTime() == null) {
            broadcast.setCompleteTime(LocalDateTime.now());
        }

        boolean success = updateById(broadcast);

        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateBroadcastStatus(List<String> broadcastIds, String status) {
        if (CollectionUtils.isEmpty(broadcastIds) || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("广播ID列表和状态不能为空");
        }

        int updatedCount = baseMapper.batchUpdateStatus(broadcastIds, status);
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsDistributing(String broadcastId) {
        return updateBroadcastStatus(broadcastId, MessageBroadcastStatus.DISTRIBUTING.getCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsCompleted(String broadcastId) {
        return updateBroadcastStatus(broadcastId, MessageBroadcastStatus.COMPLETED.getCode());
    }

    @Override
    public BroadcastStatisticsVO getBroadcastStatistics(LocalDateTime startTime,
                                                       LocalDateTime endTime,
                                                       String broadcastType) {
        Map<String, Object> statsMap = baseMapper.selectBroadcastStatistics(
                startTime, endTime, broadcastType);
        
        BroadcastStatisticsVO statisticsVO = new BroadcastStatisticsVO();
        statisticsVO.setStartTime(startTime);
        statisticsVO.setEndTime(endTime);
        statisticsVO.setBroadcastType(broadcastType);
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setDistributingCount(((Number) statsMap.getOrDefault("distributing_count", 0)).longValue());
            statisticsVO.setCompletedCount(((Number) statsMap.getOrDefault("completed_count", 0)).longValue());
            statisticsVO.setTotalReceivers(((Number) statsMap.getOrDefault("total_receivers", 0)).longValue());
            statisticsVO.setDistributedReceivers(((Number) statsMap.getOrDefault("distributed_receivers", 0)).longValue());
            statisticsVO.setReceivedReceivers(((Number) statsMap.getOrDefault("received_receivers", 0)).longValue());
            statisticsVO.setReadReceivers(((Number) statsMap.getOrDefault("read_receivers", 0)).longValue());
            
            // 计算比率
            if (statisticsVO.getTotalReceivers() > 0) {
                statisticsVO.setDistributeRate((double) statisticsVO.getDistributedReceivers() / statisticsVO.getTotalReceivers() * 100);
                statisticsVO.setReceiveRate((double) statisticsVO.getReceivedReceivers() / statisticsVO.getTotalReceivers() * 100);
                statisticsVO.setReadRate((double) statisticsVO.getReadReceivers() / statisticsVO.getTotalReceivers() * 100);
            }
        }
        
        return statisticsVO;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpMsgBroadcast> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getCreateTime);
                }
                break;
            case "startTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getStartTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getStartTime);
                }
                break;
            case "completeTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getCompleteTime);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getCompleteTime);
                }
                break;
            case "totalReceivers":
                if (asc) {
                    queryWrapper.orderByAsc(UmpMsgBroadcast::getTotalReceivers);
                } else {
                    queryWrapper.orderByDesc(UmpMsgBroadcast::getTotalReceivers);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpMsgBroadcast::getCreateTime);
                break;
        }
    }

    /**
     * 根据各统计值计算广播状态
     */
    private String calculateStatus(Integer totalReceivers, Integer distributedCount, Integer receivedCount, Integer readCount) {
        if (totalReceivers == null || totalReceivers == 0) {
            return MessageBroadcastStatus.DISTRIBUTING.getCode();
        }
        int dist = distributedCount != null ? distributedCount : 0;
        if (dist >= totalReceivers) {
            return MessageBroadcastStatus.COMPLETED.getCode();
        } else if (dist > 0) {
            return MessageBroadcastStatus.PARTIAL.getCode();
        } else {
            return MessageBroadcastStatus.DISTRIBUTING.getCode();
        }
    }

    private BroadcastDetailVO convertToDetailVO(UmpMsgBroadcast broadcast) {
        BroadcastDetailVO vo = new BroadcastDetailVO();
        BeanUtils.copyProperties(broadcast, vo);
        
        // 计算分发进度
        if (broadcast.getTotalReceivers() != null && broadcast.getTotalReceivers() > 0) {
            vo.setDistributeProgress(broadcast.getDistributedCount() != null ? 
                    (double) broadcast.getDistributedCount() / broadcast.getTotalReceivers() * 100 : 0);
            vo.setReceiveProgress(broadcast.getReceivedCount() != null ? 
                    (double) broadcast.getReceivedCount() / broadcast.getTotalReceivers() * 100 : 0);
            vo.setReadProgress(broadcast.getReadCount() != null ? 
                    (double) broadcast.getReadCount() / broadcast.getTotalReceivers() * 100 : 0);
        }
        
        return vo;
    }

    private BroadcastPageVO convertToPageVO(UmpMsgBroadcast broadcast) {
        BroadcastPageVO vo = new BroadcastPageVO();
        BeanUtils.copyProperties(broadcast, vo);
        
        // 计算分发进度
        if (broadcast.getTotalReceivers() != null && broadcast.getTotalReceivers() > 0) {
            vo.setDistributeProgress(broadcast.getDistributedCount() != null ? 
                    (double) broadcast.getDistributedCount() / broadcast.getTotalReceivers() * 100 : 0);
        }
        
        return vo;
    }

	@Override
	public void incrementReadCount(String broadcastId) {
		baseMapper.incrementReadCount(broadcastId);		
	}
}