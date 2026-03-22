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

import ltd.huntinginfo.feng.center.api.dto.AppQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.api.vo.AppPageVO;
import ltd.huntinginfo.feng.center.api.vo.AppTotalStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpAppCredentialService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import ltd.huntinginfo.feng.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用认证凭证表控制器
 * 提供应用凭证的创建、查询、更新等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j等）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/app/credential")
@RequiredArgsConstructor
@Tag(name = "应用凭证管理", description = "应用凭证的增删改查和状态管理")
public class UmpAppCredentialController {

    private final UmpAppCredentialService umpAppCredentialService;

    @Operation(summary = "创建应用凭证", description = "创建新的应用凭证")
    @PostMapping("/create")
    @SysLog("创建应用凭证")
    @HasPermission("ump_app_add")
    public R<String> createApp(
            @RequestParam String appKey,
            @RequestParam String appSecret,
            @RequestParam String appName,
            @RequestParam String appType,
            @RequestParam(required = false) String appDesc,
            @RequestParam(required = false) String appIcon,
            @RequestParam(required = false) String homeUrl,
            @RequestParam(required = false) String defaultPushMode,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String callbackAuthMode,
            @RequestParam(required = false) Integer rateLimit,
            @RequestParam(required = false) Integer maxMsgSize,
            @RequestBody(required = false) List<String> ipWhitelist,
            @RequestParam(required = false) LocalDateTime secretExpireTime) {
        try {
            String appId = umpAppCredentialService.createApp(
                    appKey, appSecret, appName, appType, appDesc, appIcon, homeUrl,
                    defaultPushMode, callbackUrl, callbackAuthMode, rateLimit,
                    maxMsgSize, ipWhitelist, secretExpireTime);
            return R.ok(appId, "应用创建成功");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("应用创建失败，请稍后重试");
        }
    }

    @Operation(summary = "更新应用凭证", description = "更新应用凭证信息")
    @PutMapping("/{appId}")
    @SysLog("更新应用凭证")
    @HasPermission("ump_app_edit")
    public R<Boolean> updateApp(
            @Parameter(description = "应用ID", required = true)
            @PathVariable String appId,
            @RequestParam(required = false) String appName,
            @RequestParam(required = false) String appDesc,
            @RequestParam(required = false) String appIcon,
            @RequestParam(required = false) String homeUrl,
            @RequestParam(required = false) String defaultPushMode,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String callbackAuthMode,
            @RequestParam(required = false) Integer rateLimit,
            @RequestParam(required = false) Integer maxMsgSize,
            @RequestBody(required = false) List<String> ipWhitelist,
            @RequestParam(required = false) Integer status) {
        try {
            boolean success = umpAppCredentialService.updateApp(
                    appId, appName, appDesc, appIcon, homeUrl, defaultPushMode,
                    callbackUrl, callbackAuthMode, rateLimit, maxMsgSize, ipWhitelist, status);
            return success ? R.ok(true, "应用更新成功") : R.failed(false, "应用更新失败（无记录更新）");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("应用更新失败，请稍后重试");
        }
    }

    @Operation(summary = "重置应用密钥", description = "重置应用密钥并设置过期时间")
    @PutMapping("/reset-secret/{appId}")
    @SysLog("重置应用密钥")
    @HasPermission("ump_app_edit")
    public R<Boolean> resetAppSecret(
            @Parameter(description = "应用ID", required = true)
            @PathVariable String appId,
            @RequestParam String newSecret,
            @RequestParam(required = false) Integer expireDays) {
        try {
            boolean success = umpAppCredentialService.resetAppSecret(appId, newSecret, expireDays);
            return success ? R.ok(true, "密钥重置成功") : R.failed(false, "密钥重置失败");
        } catch (IllegalArgumentException e) {
            return R.failed(e.getMessage());
        } catch (Exception e) {
            return R.failed("密钥重置失败，请稍后重试");
        }
    }

    @Operation(summary = "根据应用标识查询应用", description = "根据应用标识查询应用详情")
    @GetMapping("/key/{appKey}")
    public R<AppDetailVO> getAppByKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        AppDetailVO app = umpAppCredentialService.getAppByKey(appKey);
        return R.ok(app);
    }

    @Inner
    @Operation(summary = "根据应用标识查询应用详情-Feign", description = "根据应用标识查询应用详情")
    @GetMapping("/detail/{appKey}")
    public AppDetailVO getAppDetailByKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        return umpAppCredentialService.getAppByKey(appKey);
    }

    @Inner
    @Operation(summary = "根据应用标识查询密钥-Feign", description = "根据应用标识查询密钥")
    @GetMapping("/secret/{appKey}")
    public String getAppSecretByKey(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        AppDetailVO app = umpAppCredentialService.getAppByKey(appKey);
        if (app == null) {
            return "";
        }
        return umpAppCredentialService.getAppSecret(app.getId());
    }

    @Operation(summary = "分页查询应用", description = "根据条件分页查询应用列表")
    @PostMapping("/page")
    public R<Page<AppPageVO>> queryAppPage(@Valid @RequestBody AppQueryDTO queryDTO) {
        Page<AppPageVO> page = umpAppCredentialService.queryAppPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询可用应用列表", description = "查询所有可用的应用凭证")
    @GetMapping("/available")
    public R<List<AppDetailVO>> getAvailableApps() {
        List<AppDetailVO> apps = umpAppCredentialService.getAvailableApps();
        return R.ok(apps);
    }

    @Operation(summary = "启用应用", description = "启用应用凭证")
    @PutMapping("/enable/{appId}")
    @SysLog("启用应用")
    @HasPermission("ump_app_enable")
    public R<Boolean> enableApp(
            @Parameter(description = "应用ID", required = true)
            @PathVariable String appId) {
        boolean success = umpAppCredentialService.enableApp(appId);
        return success ? R.ok(true, "应用已启用") : R.failed(false, "启用失败（应用不存在或已启用）");
    }

    @Operation(summary = "禁用应用", description = "禁用应用凭证")
    @PutMapping("/disable/{appId}")
    @SysLog("禁用应用")
    @HasPermission("ump_app_disable")
    public R<Boolean> disableApp(
            @Parameter(description = "应用ID", required = true)
            @PathVariable String appId) {
        boolean success = umpAppCredentialService.disableApp(appId);
        return success ? R.ok(true, "应用已禁用") : R.failed(false, "禁用失败（应用不存在或已禁用）");
    }

    @Operation(summary = "批量启用应用", description = "批量启用应用凭证")
    @PutMapping("/enable/batch")
    @SysLog("批量启用应用")
    @HasPermission("ump_app_enable")
    public R<Integer> batchEnableApps(@RequestBody List<String> appIds) {
        int updatedCount = umpAppCredentialService.batchEnableApps(appIds);
        String msg = updatedCount > 0 ? "成功启用 " + updatedCount + " 个应用" : "没有应用被启用（可能已启用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "批量禁用应用", description = "批量禁用应用凭证")
    @PutMapping("/disable/batch")
    @SysLog("批量禁用应用")
    @HasPermission("ump_app_disable")
    public R<Integer> batchDisableApps(@RequestBody List<String> appIds) {
        int updatedCount = umpAppCredentialService.batchDisableApps(appIds);
        String msg = updatedCount > 0 ? "成功禁用 " + updatedCount + " 个应用" : "没有应用被禁用（可能已禁用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "检查应用可用性", description = "检查应用是否可用")
    @GetMapping("/available/{appKey}")
    public R<Boolean> isAppAvailable(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        boolean available = umpAppCredentialService.isAppAvailable(appKey);
        return R.ok(available);
    }

    @Operation(summary = "验证应用凭证", description = "验证应用标识和密钥")
    @PostMapping("/validate")
    public R<Boolean> validateAppCredential(
            @RequestParam String appKey,
            @RequestParam String appSecret) {
        boolean valid = umpAppCredentialService.validateAppCredential(appKey, appSecret);
        return R.ok(valid);
    }

    @Operation(summary = "获取应用统计", description = "获取应用的统计信息")
    @GetMapping("/statistics")
    public R<AppTotalStatisticsVO> getAppStatistics() {
        AppTotalStatisticsVO statistics = umpAppCredentialService.getAppStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "检查IP白名单", description = "检查IP是否在应用白名单中")
    @GetMapping("/check-ip")
    public R<Boolean> isIpInWhitelist(
            @RequestParam String appKey,
            @RequestParam String ipAddress) {
        boolean inWhitelist = umpAppCredentialService.isIpInWhitelist(appKey, ipAddress);
        return R.ok(inWhitelist);
    }

    @Operation(summary = "检查密钥是否过期", description = "检查应用密钥是否已过期")
    @GetMapping("/check-expired/{appKey}")
    public R<Boolean> isAppSecretExpired(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey) {
        boolean expired = umpAppCredentialService.isAppSecretExpired(appKey);
        return R.ok(expired);
    }

    @Operation(summary = "获取应用类型统计", description = "获取应用类型的分布统计")
    @GetMapping("/type-statistics")
    public R<Map<String, Long>> getAppTypeStatistics() {
        Map<String, Long> typeStats = umpAppCredentialService.getAppTypeStatistics();
        return R.ok(typeStats);
    }

    @Operation(summary = "删除应用", description = "删除应用凭证")
    @DeleteMapping("/{appId}")
    @SysLog("删除应用")
    @HasPermission("ump_app_del")
    public R<Boolean> deleteApp(
            @Parameter(description = "应用ID", required = true)
            @PathVariable String appId) {
        boolean success = umpAppCredentialService.deleteApp(appId);
        return success ? R.ok(true, "应用删除成功") : R.failed(false, "删除失败（应用不存在）");
    }

    @Operation(summary = "批量删除应用", description = "批量删除应用凭证")
    @DeleteMapping("/batch")
    @SysLog("批量删除应用")
    @HasPermission("ump_app_del")
    public R<Boolean> batchDeleteApps(@RequestBody List<String> appIds) {
        boolean success = umpAppCredentialService.batchDeleteApps(appIds);
        return success ? R.ok(true, "批量删除成功") : R.failed(false, "批量删除失败（没有记录被删除）");
    }
}