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
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import ltd.huntinginfo.feng.center.mapper.UmpAppPermissionMapper;
import ltd.huntinginfo.feng.center.service.UmpAppPermissionService;
import ltd.huntinginfo.feng.center.api.dto.PermissionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.PermissionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionPageVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.ResourceTreeVO;
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
 * 应用权限表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpAppPermissionServiceImpl extends ServiceImpl<UmpAppPermissionMapper, UmpAppPermission> implements UmpAppPermissionService {

    private final UmpAppPermissionMapper umpAppPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createPermission(String appKey, String resourceCode, String resourceName, String operation) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(resourceCode) || 
            !StringUtils.hasText(operation)) {
            throw new IllegalArgumentException("应用标识、资源代码和操作类型不能为空");
        }

        // 检查权限是否已存在
        boolean exists = umpAppPermissionMapper.existsByAppKeyAndResourceCode(appKey, resourceCode);
        if (exists) {
            log.warn("权限已存在，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            throw new RuntimeException("权限已存在");
        }

        // 创建权限
        UmpAppPermission permission = new UmpAppPermission();
        permission.setAppKey(appKey);
        permission.setResourceCode(resourceCode);
        permission.setResourceName(StringUtils.hasText(resourceName) ? resourceName : resourceCode);
        permission.setStatus(1); // 默认启用

        if (save(permission)) {
            log.info("权限创建成功，应用标识: {}, 资源代码: {}, 操作: {}", appKey, resourceCode, operation);
            return permission.getId();
        } else {
            log.error("权限创建失败，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            throw new RuntimeException("权限创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchCreatePermissions(String appKey, List<Map<String, String>> permissions) {
        if (!StringUtils.hasText(appKey) || CollectionUtils.isEmpty(permissions)) {
            return false;
        }

        List<UmpAppPermission> permissionList = new ArrayList<>();

        for (Map<String, String> permissionMap : permissions) {
            String resourceCode = permissionMap.get("resourceCode");
            String resourceName = permissionMap.get("resourceName");
            String operation = permissionMap.get("operation");

            if (!StringUtils.hasText(resourceCode) || !StringUtils.hasText(operation)) {
                log.warn("权限数据不完整，跳过: {}", permissionMap);
                continue;
            }

            // 检查权限是否已存在
            boolean exists = umpAppPermissionMapper.existsByAppKeyAndResourceCode(appKey, resourceCode);
            if (exists) {
                log.debug("权限已存在，跳过: 应用标识: {}, 资源代码: {}", appKey, resourceCode);
                continue;
            }

            UmpAppPermission permission = new UmpAppPermission();
            permission.setAppKey(appKey);
            permission.setResourceCode(resourceCode);
            permission.setResourceName(StringUtils.hasText(resourceName) ? resourceName : resourceCode);
            permission.setStatus(1); // 默认启用

            permissionList.add(permission);
        }

        if (CollectionUtils.isEmpty(permissionList)) {
            return false;
        }

        Boolean success = this.saveBatch(permissionList);
        if (success) {
            log.info("批量创建权限成功，应用标识: {}", appKey);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(String permissionId, String resourceName, String operation, Integer status) {
        if (!StringUtils.hasText(permissionId)) {
            throw new IllegalArgumentException("权限ID不能为空");
        }

        UmpAppPermission permission = getById(permissionId);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", permissionId);
            return false;
        }

        if (StringUtils.hasText(resourceName)) {
            permission.setResourceName(resourceName);
        }
        if (status != null) {
            permission.setStatus(status);
        }

        boolean success = updateById(permission);
        if (success) {
            log.info("权限更新成功，权限ID: {}, 应用标识: {}, 资源代码: {}", 
                    permissionId, permission.getAppKey(), permission.getResourceCode());
        }
        
        return success;
    }

    @Override
    public PermissionDetailVO getPermissionByKeyAndResource(String appKey, String resourceCode) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(resourceCode)) {
            throw new IllegalArgumentException("应用标识和资源代码不能为空");
        }

        UmpAppPermission permission = umpAppPermissionMapper.selectByAppKeyAndResourceCode(appKey, resourceCode);
        if (permission == null) {
            log.warn("权限不存在，应用标识: {}, 资源代码: {}", appKey, resourceCode);
            return null;
        }

        return convertToDetailVO(permission);
    }
    
    @Override
    public boolean hasApiPermission(String appKey, String resourceCode) {
    	PermissionDetailVO vo = getPermissionByKeyAndResource(appKey, resourceCode);
    	if (BeanUtil.isEmpty(vo)) {
    		return false;
    	}
    	return true;
    }
    
    @Override
    public List<UmpAppPermission> getReceiveScopes(String appKey) {
        return lambdaQuery()
                .eq(UmpAppPermission::getAppKey, appKey)
                .eq(UmpAppPermission::getType, "RECEIVE")
                .eq(UmpAppPermission::getStatus, 1)
                .list();
    }
    
    @Override
    public Page<PermissionPageVO> queryPermissionPage(PermissionQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpAppPermission> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getAppKey())) {
            queryWrapper.eq(UmpAppPermission::getAppKey, queryDTO.getAppKey());
        }
        
        if (StringUtils.hasText(queryDTO.getResourceCode())) {
            queryWrapper.like(UmpAppPermission::getResourceCode, queryDTO.getResourceCode());
        }
        
        if (StringUtils.hasText(queryDTO.getResourceName())) {
            queryWrapper.like(UmpAppPermission::getResourceName, queryDTO.getResourceName());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpAppPermission::getStatus, queryDTO.getStatus());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpAppPermission::getCreateTime);
        }

        // 执行分页查询
        Page<UmpAppPermission> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpAppPermission> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<PermissionPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<PermissionPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<PermissionDetailVO> getPermissionsByAppKey(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        List<UmpAppPermission> permissions = umpAppPermissionMapper.selectByAppKey(appKey);
        return permissions.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermissionDetailVO> getAvailablePermissions(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return Collections.emptyList();
        }

        List<UmpAppPermission> permissions = umpAppPermissionMapper.selectAvailablePermissions(appKey);
        return permissions.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enablePermission(String permissionId) {
        return updatePermissionStatus(permissionId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean disablePermission(String permissionId) {
        return updatePermissionStatus(permissionId, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchEnablePermissions(List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return 0;
        }

        int updatedCount = umpAppPermissionMapper.batchUpdateStatus(permissionIds, 1);
        if (updatedCount > 0) {
            log.info("批量启用权限成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDisablePermissions(List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return 0;
        }

        int updatedCount = umpAppPermissionMapper.batchUpdateStatus(permissionIds, 0);
        if (updatedCount > 0) {
            log.info("批量禁用权限成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    public PermissionStatisticsVO getPermissionStatistics() {
        Map<String, Object> statsMap = umpAppPermissionMapper.selectPermissionStatistics();
        
        PermissionStatisticsVO statisticsVO = new PermissionStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setEnabledCount(((Number) statsMap.getOrDefault("enabled_count", 0)).longValue());
            statisticsVO.setDisabledCount(((Number) statsMap.getOrDefault("disabled_count", 0)).longValue());
            statisticsVO.setReadCount(((Number) statsMap.getOrDefault("read_count", 0)).longValue());
            statisticsVO.setWriteCount(((Number) statsMap.getOrDefault("write_count", 0)).longValue());
            statisticsVO.setAllOperationCount(((Number) statsMap.getOrDefault("all_operation_count", 0)).longValue());
            
            // 计算启用率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setEnableRate((double) statisticsVO.getEnabledCount() / statisticsVO.getTotalCount() * 100);
            }
            
            // 计算操作类型分布
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setReadRate((double) statisticsVO.getReadCount() / statisticsVO.getTotalCount() * 100);
                statisticsVO.setWriteRate((double) statisticsVO.getWriteCount() / statisticsVO.getTotalCount() * 100);
                statisticsVO.setAllOperationRate((double) statisticsVO.getAllOperationCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(String permissionId) {
        if (!StringUtils.hasText(permissionId)) {
            throw new IllegalArgumentException("权限ID不能为空");
        }

        boolean success = this.removeById(permissionId);
        if (success) {
            log.info("权限删除成功，权限ID: {}",  permissionId);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeletePermissions(List<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return false;
        }

        boolean success = this.removeByIds(permissionIds);
        if (success) {
            log.info("批量删除权限成功");
            return success;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePermissionsByAppKey(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return false;
        }

        LambdaQueryWrapper<UmpAppPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UmpAppPermission::getAppKey, appKey);
        Boolean success = this.remove(queryWrapper);

        if (success) {
            log.info("删除应用所有权限成功，应用标识: {}", appKey);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean copyPermissionsToApp(String sourceAppKey, String targetAppKey) {
        if (!StringUtils.hasText(sourceAppKey) || !StringUtils.hasText(targetAppKey)) {
            return false;
        }

        List<UmpAppPermission> sourcePermissions = umpAppPermissionMapper.selectByAppKey(sourceAppKey);
        if (CollectionUtils.isEmpty(sourcePermissions)) {
            return false;
        }

        List<UmpAppPermission> targetPermissions = new ArrayList<>();

        for (UmpAppPermission sourcePermission : sourcePermissions) {
            if (sourcePermission.getStatus() != 1) {
                continue;
            }

            // 检查目标应用是否已存在该权限
            boolean exists = umpAppPermissionMapper.existsByAppKeyAndResourceCode(
                targetAppKey, sourcePermission.getResourceCode());
            if (exists) {
                continue;
            }

            UmpAppPermission targetPermission = new UmpAppPermission();
            BeanUtils.copyProperties(sourcePermission, targetPermission);
            targetPermission.setId(null);
            targetPermission.setAppKey(targetAppKey);

            targetPermissions.add(targetPermission);
        }

        if (CollectionUtils.isEmpty(targetPermissions)) {
            return false;
        }

        Boolean success = this.saveBatch(targetPermissions);
        if (success) {
            log.info("复制权限成功，源应用: {}, 目标应用: {}", sourceAppKey, targetAppKey);
        }
        
        return success;
    }

    @Override
    public ResourceTreeVO getResourceTree(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            // 返回空对象（可根据项目约定返回 null 或空对象）
            ResourceTreeVO empty = new ResourceTreeVO();
            empty.setAppKey(appKey);
            empty.setTotalResources(0);
            empty.setResources(Collections.emptyList());
            return empty;
        }

        List<UmpAppPermission> permissions = umpAppPermissionMapper.selectByAppKey(appKey);
        if (CollectionUtils.isEmpty(permissions)) {
            ResourceTreeVO empty = new ResourceTreeVO();
            empty.setAppKey(appKey);
            empty.setTotalResources(0);
            empty.setResources(Collections.emptyList());
            return empty;
        }

        // 按资源代码分组
        Map<String, List<UmpAppPermission>> groupedPermissions = permissions.stream()
                .filter(p -> p.getStatus() == 1)
                .collect(Collectors.groupingBy(UmpAppPermission::getResourceCode));

        // 构建资源节点列表
        List<ResourceTreeVO.ResourceNodeVO> nodeList = new ArrayList<>();
        for (Map.Entry<String, List<UmpAppPermission>> entry : groupedPermissions.entrySet()) {
            String resourceCode = entry.getKey();
            List<UmpAppPermission> resourcePermissions = entry.getValue();

            ResourceTreeVO.ResourceNodeVO node = new ResourceTreeVO.ResourceNodeVO();
            node.setResourceCode(resourceCode);
            node.setResourceName(resourcePermissions.get(0).getResourceName());

            nodeList.add(node);
        }

        // 组装最终结果
        ResourceTreeVO treeVO = new ResourceTreeVO();
        treeVO.setAppKey(appKey);
        treeVO.setTotalResources(nodeList.size());
        treeVO.setResources(nodeList);

        return treeVO;
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpAppPermission> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getUpdateTime);
                }
                break;
            case "resourceCode":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getResourceCode);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getResourceCode);
                }
                break;
            case "resourceName":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getResourceName);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getResourceName);
                }
                break;
            case "status":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppPermission::getStatus);
                } else {
                    queryWrapper.orderByDesc(UmpAppPermission::getStatus);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpAppPermission::getCreateTime);
                break;
        }
    }

    private boolean updatePermissionStatus(String permissionId, Integer status) {
        if (!StringUtils.hasText(permissionId) || status == null) {
            throw new IllegalArgumentException("权限ID和状态不能为空");
        }

        UmpAppPermission permission = getById(permissionId);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", permissionId);
            return false;
        }

        if (permission.getStatus().equals(status)) {
            log.debug("权限状态未改变，权限ID: {}, 状态: {}", permissionId, status);
            return true;
        }

        permission.setStatus(status);
        
        boolean success = updateById(permission);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("权限{}成功，权限ID: {}, 应用标识: {}, 资源代码: {}", 
                    action, permissionId, permission.getAppKey(), permission.getResourceCode());
        }
        
        return success;
    }

    private PermissionDetailVO convertToDetailVO(UmpAppPermission permission) {
        PermissionDetailVO vo = new PermissionDetailVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }

    private PermissionPageVO convertToPageVO(UmpAppPermission permission) {
        PermissionPageVO vo = new PermissionPageVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}