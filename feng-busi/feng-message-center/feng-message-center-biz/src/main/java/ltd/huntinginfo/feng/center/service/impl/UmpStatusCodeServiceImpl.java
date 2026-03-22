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

import cn.hutool.json.JSONUtil;
import ltd.huntinginfo.feng.center.api.dto.StatusCodeQueryDTO;
import ltd.huntinginfo.feng.center.api.entity.UmpStatusCode;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeDetailVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodePageVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeTreeVO;
import ltd.huntinginfo.feng.center.mapper.UmpStatusCodeMapper;
import ltd.huntinginfo.feng.center.service.UmpStatusCodeService;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息状态码表服务实现类
 * 打印日志（@Slf4j），异常日志使用log.error,一般错误使用log.warn
 * 各个方法返回有效数据，一般不返回错误代码，错误代码（BusinessEnum）使用异常（BusinessException）来控制
 * 使用baseMapper访问自身数据库映射接口（xxxMapper）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpStatusCodeServiceImpl extends ServiceImpl<UmpStatusCodeMapper, UmpStatusCode> implements UmpStatusCodeService {

    private final UmpStatusCodeMapper umpStatusCodeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createStatusCode(String statusCode, String statusName, String statusDesc,
                                  String category, String parentCode, Integer sortOrder,
                                  Integer isFinal, Integer canRetry) {
        if (!StringUtils.hasText(statusCode) || !StringUtils.hasText(statusName) || 
            !StringUtils.hasText(statusDesc) || !StringUtils.hasText(category)) {
            throw new IllegalArgumentException("状态码、状态名称、状态描述和分类不能为空");
        }

        // 检查状态码是否已存在
        if (existsByStatusCode(statusCode)) {
            log.warn("状态码已存在，状态码: {}", statusCode);
            throw new BusinessException(BusinessEnum.UMP_CODE_EXISTED.getCode(), "状态码已存在");
        }

        // 检查父状态码是否存在
        if (StringUtils.hasText(parentCode)) {
            UmpStatusCode parent = umpStatusCodeMapper.selectByStatusCode(parentCode);
            if (parent == null) {
                log.warn("父状态码不存在，父状态码: {}", parentCode);
                throw new BusinessException(BusinessEnum.UMP_CODE_EXISTED.getCode(), "父状态码不存在");
            }
        }

        // 创建状态码
        UmpStatusCode statusCodeEntity = new UmpStatusCode();
        statusCodeEntity.setStatusCode(statusCode);
        statusCodeEntity.setStatusName(statusName);
        statusCodeEntity.setStatusDesc(statusDesc);
        statusCodeEntity.setCategory(category);
        statusCodeEntity.setParentCode(parentCode);
        statusCodeEntity.setSortOrder(sortOrder != null ? sortOrder : 0);
        statusCodeEntity.setIsFinal(isFinal != null ? isFinal : 0);
        statusCodeEntity.setCanRetry(canRetry != null ? canRetry : 1);
        statusCodeEntity.setStatus(1); // 默认启用
        // createTime 由自动填充处理器处理，此处无需手动设置

        if (save(statusCodeEntity)) {
            log.info("状态码创建成功，状态码: {}, 状态名称: {}", statusCode, statusName);
            return statusCodeEntity.getId();
        } else {
            log.error("状态码创建失败，状态码: {}", statusCode);
            throw new BusinessException(BusinessEnum.UMP_CREATE_FAILED.getCode(), "状态码创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatusCode(String id, String statusName, String statusDesc,
                                   Integer sortOrder, Integer isFinal, Integer canRetry,
                                   Integer status) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("状态码ID不能为空");
        }

        UmpStatusCode statusCodeEntity = getById(id);
        if (statusCodeEntity == null) {
            log.warn("状态码不存在，状态码ID: {}", id);
            return false;
        }

        boolean updated = false;
        if (StringUtils.hasText(statusName)) {
            statusCodeEntity.setStatusName(statusName);
            updated = true;
        }
        if (StringUtils.hasText(statusDesc)) {
            statusCodeEntity.setStatusDesc(statusDesc);
            updated = true;
        }
        if (sortOrder != null) {
            statusCodeEntity.setSortOrder(sortOrder);
            updated = true;
        }
        if (isFinal != null) {
            statusCodeEntity.setIsFinal(isFinal);
            updated = true;
        }
        if (canRetry != null) {
            statusCodeEntity.setCanRetry(canRetry);
            updated = true;
        }
        if (status != null) {
            statusCodeEntity.setStatus(status);
            updated = true;
        }

        if (updated) {
            // updateTime 由自动填充处理器处理，此处无需手动设置
            boolean success = updateById(statusCodeEntity);
            if (success) {
                log.info("状态码更新成功，状态码ID: {}, 状态码: {}", id, statusCodeEntity.getStatusCode());
            }
            return success;
        }
        
        return true; // 无更新项也视为成功
    }

    @Override
    public StatusCodeDetailVO getByStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            throw new IllegalArgumentException("状态码不能为空");
        }

        UmpStatusCode statusCodeEntity = umpStatusCodeMapper.selectByStatusCode(statusCode);
        if (statusCodeEntity == null) {
            log.warn("状态码不存在，状态码: {}", statusCode);
            return null;
        }

        return convertToDetailVO(statusCodeEntity);
    }

    @Override
    public Page<StatusCodePageVO> queryStatusCodePage(StatusCodeQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpStatusCode> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getStatusCode())) {
            queryWrapper.like(UmpStatusCode::getStatusCode, queryDTO.getStatusCode());
        }
        
        if (StringUtils.hasText(queryDTO.getStatusName())) {
            queryWrapper.like(UmpStatusCode::getStatusName, queryDTO.getStatusName());
        }
        
        if (StringUtils.hasText(queryDTO.getCategory())) {
            queryWrapper.eq(UmpStatusCode::getCategory, queryDTO.getCategory());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpStatusCode::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getParentCode())) {
            queryWrapper.eq(UmpStatusCode::getParentCode, queryDTO.getParentCode());
        }
        
        if (queryDTO.getIsFinal() != null) {
            queryWrapper.eq(UmpStatusCode::getIsFinal, queryDTO.getIsFinal());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByAsc(UmpStatusCode::getCategory)
                       .orderByAsc(UmpStatusCode::getSortOrder)
                       .orderByAsc(UmpStatusCode::getStatusCode);
        }

        // 执行分页查询
        Page<UmpStatusCode> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpStatusCode> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<StatusCodePageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<StatusCodePageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<StatusCodeDetailVO> getByCategory(String category, boolean enabledOnly) {
        if (!StringUtils.hasText(category)) {
            throw new IllegalArgumentException("分类不能为空");
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpStatusCode> statusCodes = umpStatusCodeMapper.selectByCategory(category, status);
        
        return statusCodes.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(StatusCodeDetailVO::getSortOrder)
                        .thenComparing(StatusCodeDetailVO::getStatusCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<StatusCodeDetailVO> getByParentCode(String parentCode, boolean enabledOnly) {
        if (!StringUtils.hasText(parentCode)) {
            throw new IllegalArgumentException("父状态码不能为空");
        }

        Integer status = enabledOnly ? 1 : null;
        List<UmpStatusCode> statusCodes = umpStatusCodeMapper.selectByParentCode(parentCode, status);
        
        return statusCodes.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(StatusCodeDetailVO::getSortOrder)
                        .thenComparing(StatusCodeDetailVO::getStatusCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<StatusCodeDetailVO> getAllEnabled() {
        List<UmpStatusCode> statusCodes = umpStatusCodeMapper.selectAllEnabled();
        
        return statusCodes.stream()
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(StatusCodeDetailVO::getCategory)
                        .thenComparing(StatusCodeDetailVO::getSortOrder)
                        .thenComparing(StatusCodeDetailVO::getStatusCode))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableStatusCode(String id) {
        return updateStatusCodeStatus(id, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disableStatusCode(String id) {
        return updateStatusCodeStatus(id, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchEnableStatusCodes(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int updatedCount = umpStatusCodeMapper.batchUpdateStatus(ids, 1);
        if (updatedCount > 0) {
            log.info("批量启用状态码成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDisableStatusCodes(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int updatedCount = umpStatusCodeMapper.batchUpdateStatus(ids, 0);
        if (updatedCount > 0) {
            log.info("批量禁用状态码成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    public boolean existsByStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return false;
        }

        return umpStatusCodeMapper.existsByStatusCode(statusCode);
    }

    @Override
    public StatusCodeStatisticsVO getStatusCodeStatistics() {
        Map<String, Object> statsMap = umpStatusCodeMapper.selectStatusCodeStatistics();
        
        StatusCodeStatisticsVO statisticsVO = new StatusCodeStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(getLong(statsMap, "total_count"));
            statisticsVO.setEnabledCount(getLong(statsMap, "enabled_count"));
            statisticsVO.setDisabledCount(getLong(statsMap, "disabled_count"));
            
            // 分类统计 - 处理 JSON 字符串
            Map<String, Long> categoryStats = new HashMap<>();
            Object categoryStatsObj = statsMap.get("category_stats");
            if (categoryStatsObj != null) {
                try {
                    // JSON_OBJECTAGG 返回的是 JSON 字符串，需要解析
                    String categoryStatsStr = categoryStatsObj.toString();
                    Map<String, Object> categoryMap = JSONUtil.parseObj(categoryStatsStr);
                    categoryMap.forEach((key, value) -> 
                        categoryStats.put(key, ((Number) value).longValue()));
                } catch (Exception e) {
                    log.warn("解析分类统计 JSON 失败", e);
                }
            }
            statisticsVO.setCategoryStats(categoryStats);
            
            statisticsVO.setFinalCount(getLong(statsMap, "final_count"));
            statisticsVO.setNonFinalCount(getLong(statsMap, "non_final_count"));
            statisticsVO.setRetryableCount(getLong(statsMap, "retryable_count"));
            statisticsVO.setNonRetryableCount(getLong(statsMap, "non_retryable_count"));
            
            // 计算启用率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setEnableRate((double) statisticsVO.getEnabledCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    public List<StatusCodeTreeVO> getStatusCodeTree(String category, boolean enabledOnly) {
        Integer status = enabledOnly ? 1 : null;
        List<Map<String, Object>> treeData = umpStatusCodeMapper.selectStatusCodeTree(category, status);
        
        // 将数据库返回的数据转换为树形结构
        List<StatusCodeTreeVO> treeList = new ArrayList<>();
        Map<String, StatusCodeTreeVO> nodeMap = new HashMap<>();
        
        // 第一遍：创建所有节点
        for (Map<String, Object> row : treeData) {
            StatusCodeTreeVO node = new StatusCodeTreeVO();
            node.setId((String) row.get("id"));
            node.setStatusCode((String) row.get("status_code"));
            node.setStatusName((String) row.get("status_name"));
            node.setCategory((String) row.get("category"));
            node.setParentCode((String) row.get("parent_code"));
            node.setSortOrder((Integer) row.get("sort_order"));
            node.setIsFinal((Integer) row.get("is_final"));
            node.setCanRetry((Integer) row.get("can_retry"));
            node.setStatus((Integer) row.get("status"));
            node.setChildren(new ArrayList<>());
            
            nodeMap.put(node.getStatusCode(), node);
        }
        
        // 第二遍：构建树形结构
        for (StatusCodeTreeVO node : nodeMap.values()) {
            if (node.getParentCode() == null || node.getParentCode().isEmpty()) {
                treeList.add(node);
            } else {
                StatusCodeTreeVO parent = nodeMap.get(node.getParentCode());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        
        // 排序
        treeList.sort(Comparator.comparing(StatusCodeTreeVO::getSortOrder)
                .thenComparing(StatusCodeTreeVO::getStatusCode));
        
        for (StatusCodeTreeVO node : nodeMap.values()) {
            if (!node.getChildren().isEmpty()) {
                node.getChildren().sort(Comparator.comparing(StatusCodeTreeVO::getSortOrder)
                        .thenComparing(StatusCodeTreeVO::getStatusCode));
            }
        }
        
        return treeList;
    }

    @Override
    public Map<String, String> getStatusCodeMap(String category, boolean enabledOnly) {
        if (!StringUtils.hasText(category)) {
            throw new IllegalArgumentException("分类不能为空");
        }

        Integer status = enabledOnly ? 1 : null;
        return umpStatusCodeMapper.selectStatusCodeMap(category, status);
    }

    @Override
    public boolean isFinalStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return false;
        }

        UmpStatusCode statusCodeEntity = umpStatusCodeMapper.selectByStatusCode(statusCode);
        return statusCodeEntity != null && statusCodeEntity.getStatus() == 1 && statusCodeEntity.getIsFinal() == 1;
    }

    @Override
    public boolean canRetryStatusCode(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return false;
        }

        UmpStatusCode statusCodeEntity = umpStatusCodeMapper.selectByStatusCode(statusCode);
        return statusCodeEntity != null && statusCodeEntity.getStatus() == 1 && statusCodeEntity.getCanRetry() == 1;
    }

    @Override
    public List<StatusCodeDetailVO> getValidTransitionStatusCodes(String currentStatusCode) {
        if (!StringUtils.hasText(currentStatusCode)) {
            return Collections.emptyList();
        }

        UmpStatusCode current = umpStatusCodeMapper.selectByStatusCode(currentStatusCode);
        if (current == null || current.getStatus() != 1) {
            return Collections.emptyList();
        }

        // 如果是最终状态，不能流转到其他状态
        if (current.getIsFinal() == 1) {
            return Collections.emptyList();
        }

        // 获取同分类下的所有启用的状态码
        List<UmpStatusCode> statusCodes = umpStatusCodeMapper.selectByCategory(current.getCategory(), 1);
        
        return statusCodes.stream()
                .filter(code -> !code.getStatusCode().equals(currentStatusCode)) // 排除当前状态
                .filter(code -> code.getIsFinal() == 1 || !Objects.equals(code.getParentCode(), currentStatusCode)) // 避免循环引用
                .map(this::convertToDetailVO)
                .sorted(Comparator.comparing(StatusCodeDetailVO::getSortOrder)
                        .thenComparing(StatusCodeDetailVO::getStatusCode))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateStatusTransition(String currentStatusCode, String targetStatusCode) {
        if (!StringUtils.hasText(currentStatusCode) || !StringUtils.hasText(targetStatusCode)) {
            return false;
        }

        if (currentStatusCode.equals(targetStatusCode)) {
            return false; // 不允许自我流转
        }

        UmpStatusCode current = umpStatusCodeMapper.selectByStatusCode(currentStatusCode);
        UmpStatusCode target = umpStatusCodeMapper.selectByStatusCode(targetStatusCode);

        if (current == null || target == null || current.getStatus() != 1 || target.getStatus() != 1) {
            return false;
        }

        // 如果当前状态是最终状态，不能流转
        if (current.getIsFinal() == 1) {
            return false;
        }

        // 必须属于同一分类
        if (!current.getCategory().equals(target.getCategory())) {
            return false;
        }

        // 不能流转到自己的父状态（避免循环）
        if (currentStatusCode.equals(target.getParentCode())) {
            return false;
        }

        // 不能流转到自己的子状态（避免循环）
        if (targetStatusCode.equals(current.getParentCode())) {
            return false;
        }

        return true;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpStatusCode> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "statusCode":
                if (asc) {
                    queryWrapper.orderByAsc(UmpStatusCode::getStatusCode);
                } else {
                    queryWrapper.orderByDesc(UmpStatusCode::getStatusCode);
                }
                break;
            case "statusName":
                if (asc) {
                    queryWrapper.orderByAsc(UmpStatusCode::getStatusName);
                } else {
                    queryWrapper.orderByDesc(UmpStatusCode::getStatusName);
                }
                break;
            case "category":
                if (asc) {
                    queryWrapper.orderByAsc(UmpStatusCode::getCategory);
                } else {
                    queryWrapper.orderByDesc(UmpStatusCode::getCategory);
                }
                break;
            case "sortOrder":
                if (asc) {
                    queryWrapper.orderByAsc(UmpStatusCode::getSortOrder);
                } else {
                    queryWrapper.orderByDesc(UmpStatusCode::getSortOrder);
                }
                break;
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpStatusCode::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpStatusCode::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpStatusCode::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpStatusCode::getUpdateTime);
                }
                break;
            default:
                queryWrapper.orderByAsc(UmpStatusCode::getCategory)
                           .orderByAsc(UmpStatusCode::getSortOrder)
                           .orderByAsc(UmpStatusCode::getStatusCode);
                break;
        }
    }

    private boolean updateStatusCodeStatus(String id, Integer status) {
        if (!StringUtils.hasText(id) || status == null) {
            throw new IllegalArgumentException("状态码ID和状态不能为空");
        }

        UmpStatusCode statusCodeEntity = getById(id);
        if (statusCodeEntity == null) {
            log.warn("状态码不存在，状态码ID: {}", id);
            return false;
        }

        if (statusCodeEntity.getStatus().equals(status)) {
            log.debug("状态码状态未改变，状态码ID: {}, 状态: {}", id, status);
            return true;
        }

        statusCodeEntity.setStatus(status);
        // updateTime 由自动填充处理器处理，此处无需手动设置
        
        boolean success = updateById(statusCodeEntity);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("状态码{}成功，状态码ID: {}, 状态码: {}", action, id, statusCodeEntity.getStatusCode());
        }
        
        return success;
    }

    private StatusCodeDetailVO convertToDetailVO(UmpStatusCode statusCodeEntity) {
        if (statusCodeEntity == null) return null;
        StatusCodeDetailVO vo = new StatusCodeDetailVO();
        BeanUtils.copyProperties(statusCodeEntity, vo);
        return vo;
    }

    private StatusCodePageVO convertToPageVO(UmpStatusCode statusCodeEntity) {
        if (statusCodeEntity == null) return null;
        StatusCodePageVO vo = new StatusCodePageVO();
        BeanUtils.copyProperties(statusCodeEntity, vo);
        return vo;
    }

    // 辅助方法：安全地从Map中获取Long值
    private Long getLong(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? 0L : ((Number) val).longValue();
    }
}