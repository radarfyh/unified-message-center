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
package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import ltd.huntinginfo.feng.center.api.dto.PermissionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.PermissionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionPageVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.ResourceTreeVO;
import ltd.huntinginfo.feng.center.service.UmpAppPermissionService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 应用权限表控制器
 * 提供应用权限的创建、查询、更新等接口
 */
@Validated
@RestController
@RequestMapping("/app/permission")
@RequiredArgsConstructor
@Tag(name = "应用权限管理", description = "应用权限的增删改查和状态管理")
public class UmpAppPermissionController {

    private final UmpAppPermissionService umpAppPermissionService;

    @Operation(summary = "创建应用权限", description = "创建新的应用权限")
    @PostMapping("/create")
    @SysLog("创建应用权限")
    @HasPermission("ump_permission_add")
    public R<String> createPermission(
            @RequestParam String appKey,
            @RequestParam String resourceCode,
            @RequestParam(required = false) String resourceName,
            @RequestParam String operation) {
        try {
            String permissionId = umpAppPermissionService.createPermission(
                    appKey, resourceCode, resourceName, operation);
            return R.ok(permissionId, "权限创建成功");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("权限创建失败，请稍后重试");
        }
    }

    @Operation(summary = "批量创建应用权限", description = "批量创建应用权限")
    @PostMapping("/batch-create")
    @SysLog("批量创建应用权限")
    @HasPermission("ump_permission_add")
    public R<Boolean> batchCreatePermissions(
            @RequestParam String appKey,
            @RequestBody List<Map<String, String>> permissions) {
        try {
            boolean success = umpAppPermissionService.batchCreatePermissions(appKey, permissions);
            return success ? R.ok(true, "批量创建成功") : R.failed(false, "批量创建失败（无记录创建）");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("批量创建权限失败，请稍后重试");
        }
    }

    @Operation(summary = "更新应用权限", description = "更新应用权限信息")
    @PutMapping("/{permissionId}")
    @SysLog("更新应用权限")
    @HasPermission("ump_permission_edit")
    public R<Boolean> updatePermission(
            @Parameter(description = "权限ID", required = true)
            @PathVariable String permissionId,
            @RequestParam(required = false) String resourceName,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpAppPermissionService.updatePermission(
                    permissionId, resourceName, operation, status);
            return success ? R.ok(true, "权限更新成功") : R.failed(false, "权限更新失败（无记录更新）");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("权限更新失败，请稍后重试");
        }
    }

    @Operation(summary = "根据应用标识和资源代码查询权限", description = "根据应用标识和资源代码查询权限详情")
    @GetMapping("/detail")
    public R<PermissionDetailVO> getPermissionByKeyAndResource(
            @RequestParam String appKey,
            @RequestParam String resourceCode) {
        PermissionDetailVO permission = umpAppPermissionService.getPermissionByKeyAndResource(appKey, resourceCode);
        return R.ok(permission);
    }

    @Operation(summary = "分页查询应用权限", description = "根据条件分页查询应用权限列表")
    @PostMapping("/page")
    public R<Page<PermissionPageVO>> queryPermissionPage(@Valid @RequestBody PermissionQueryDTO queryDTO) {
        Page<PermissionPageVO> page = umpAppPermissionService.queryPermissionPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据应用标识查询权限列表", description = "查询指定应用的所有权限")
    @GetMapping("/app/{appKey}")
    public R<List<PermissionDetailVO>> getPermissionsByAppKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        List<PermissionDetailVO> permissions = umpAppPermissionService.getPermissionsByAppKey(appKey);
        return R.ok(permissions);
    }

    @Operation(summary = "查询可用的应用权限列表", description = "查询指定应用的可用权限列表")
    @GetMapping("/available/{appKey}")
    public R<List<PermissionDetailVO>> getAvailablePermissions(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        List<PermissionDetailVO> permissions = umpAppPermissionService.getAvailablePermissions(appKey);
        return R.ok(permissions);
    }

    @Operation(summary = "启用权限", description = "启用应用权限")
    @PutMapping("/enable/{permissionId}")
    @SysLog("启用权限")
    @HasPermission("ump_permission_enable")
    public R<Boolean> enablePermission(
            @Parameter(description = "权限ID", required = true)
            @PathVariable String permissionId) {
        boolean success = umpAppPermissionService.enablePermission(permissionId);
        return success ? R.ok(true, "权限已启用") : R.failed(false, "启用失败（权限不存在或已启用）");
    }

    @Operation(summary = "禁用权限", description = "禁用应用权限")
    @PutMapping("/disable/{permissionId}")
    @SysLog("禁用权限")
    @HasPermission("ump_permission_disable")
    public R<Boolean> disablePermission(
            @Parameter(description = "权限ID", required = true)
            @PathVariable String permissionId) {
        boolean success = umpAppPermissionService.disablePermission(permissionId);
        return success ? R.ok(true, "权限已禁用") : R.failed(false, "禁用失败（权限不存在或已禁用）");
    }

    @Operation(summary = "批量启用权限", description = "批量启用应用权限")
    @PutMapping("/enable/batch")
    @SysLog("批量启用权限")
    @HasPermission("ump_permission_enable")
    public R<Integer> batchEnablePermissions(@RequestBody List<String> permissionIds) {
        int updatedCount = umpAppPermissionService.batchEnablePermissions(permissionIds);
        String msg = updatedCount > 0 ? "成功启用 " + updatedCount + " 条权限" : "没有权限被启用（可能已启用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "批量禁用权限", description = "批量禁用应用权限")
    @PutMapping("/disable/batch")
    @SysLog("批量禁用权限")
    @HasPermission("ump_permission_disable")
    public R<Integer> batchDisablePermissions(@RequestBody List<String> permissionIds) {
        int updatedCount = umpAppPermissionService.batchDisablePermissions(permissionIds);
        String msg = updatedCount > 0 ? "成功禁用 " + updatedCount + " 条权限" : "没有权限被禁用（可能已禁用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "检查应用权限", description = "检查应用是否有权限访问资源")
    @GetMapping("/check")
    public R<Boolean> checkPermission(
            @RequestParam String appKey,
            @RequestParam String resourceCode) {
        boolean hasPermission = umpAppPermissionService.hasApiPermission(appKey, resourceCode);
        return R.ok(hasPermission);
    }

    @Operation(summary = "获取权限统计", description = "获取权限的统计信息")
    @GetMapping("/statistics")
    public R<PermissionStatisticsVO> getPermissionStatistics() {
        PermissionStatisticsVO statistics = umpAppPermissionService.getPermissionStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "删除权限", description = "删除应用权限")
    @DeleteMapping("/{permissionId}")
    @SysLog("删除权限")
    @HasPermission("ump_permission_del")
    public R<Boolean> deletePermission(
            @Parameter(description = "权限ID", required = true)
            @PathVariable String permissionId) {
        boolean success = umpAppPermissionService.deletePermission(permissionId);
        return success ? R.ok(true, "权限删除成功") : R.failed(false, "删除失败（权限不存在）");
    }

    @Operation(summary = "批量删除权限", description = "批量删除应用权限")
    @DeleteMapping("/batch")
    @SysLog("批量删除权限")
    @HasPermission("ump_permission_del")
    public R<Boolean> batchDeletePermissions(@RequestBody List<String> permissionIds) {
        boolean success = umpAppPermissionService.batchDeletePermissions(permissionIds);
        return success ? R.ok(true, "批量删除成功") : R.failed(false, "批量删除失败（没有记录被删除）");
    }

    @Operation(summary = "根据应用标识删除权限", description = "删除指定应用的所有权限")
    @DeleteMapping("/app/{appKey}")
    @SysLog("根据应用标识删除权限")
    @HasPermission("ump_permission_del")
    public R<Boolean> deletePermissionsByAppKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        boolean success = umpAppPermissionService.deletePermissionsByAppKey(appKey);
        return success ? R.ok(true, "删除应用所有权限成功") : R.failed(false, "删除失败（应用不存在或无权限）");
    }

    @Operation(summary = "复制权限到其他应用", description = "将源应用的权限复制到目标应用")
    @PostMapping("/copy")
    @SysLog("复制权限")
    @HasPermission("ump_permission_add")
    public R<Boolean> copyPermissionsToApp(
            @RequestParam String sourceAppKey,
            @RequestParam String targetAppKey) {
        boolean success = umpAppPermissionService.copyPermissionsToApp(sourceAppKey, targetAppKey);
        return success ? R.ok(true, "权限复制成功") : R.failed(false, "权限复制失败（无源权限或目标应用已有部分权限）");
    }

    @Operation(summary = "获取应用资源树", description = "获取应用的可访问资源树结构")
    @GetMapping("/resource-tree/{appKey}")
    public R<ResourceTreeVO> getResourceTree(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        ResourceTreeVO resourceTree = umpAppPermissionService.getResourceTree(appKey);
        return R.ok(resourceTree);
    }
}