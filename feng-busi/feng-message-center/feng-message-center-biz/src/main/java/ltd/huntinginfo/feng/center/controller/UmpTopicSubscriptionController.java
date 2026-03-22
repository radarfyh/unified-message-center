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

import ltd.huntinginfo.feng.center.api.dto.SubscriptionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionPageVO;
import ltd.huntinginfo.feng.center.api.vo.SubscriptionStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpTopicSubscriptionService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 主题订阅表控制器
 * 提供主题订阅的创建、查询、更新等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/message/subscription")
@RequiredArgsConstructor
@Tag(name = "主题订阅管理", description = "消息主题订阅的增删改查和状态管理")
public class UmpTopicSubscriptionController {

    private final UmpTopicSubscriptionService umpTopicSubscriptionService;

    @Operation(summary = "创建订阅", description = "创建新的主题订阅")
    @PostMapping("/create")
    @SysLog("创建主题订阅")
    @HasPermission("ump_subscription_add")
    public R<String> createSubscription(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestBody(required = false) Map<String, Object> subscriptionConfig,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String pushMode) {
        String subscriptionId = umpTopicSubscriptionService.createSubscription(
                topicCode, appKey, subscriptionConfig, callbackUrl, pushMode);
        return R.ok(subscriptionId, "订阅创建成功");
    }

    @Operation(summary = "更新订阅", description = "更新主题订阅信息")
    @PutMapping("/{subscriptionId}")
    @SysLog("更新主题订阅")
    @HasPermission("ump_subscription_edit")
    public R<Boolean> updateSubscription(
            @Parameter(description = "订阅ID", required = true)
            @PathVariable String subscriptionId,
            @RequestBody(required = false) Map<String, Object> subscriptionConfig,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String pushMode) {
        boolean success = umpTopicSubscriptionService.updateSubscription(
                subscriptionId, subscriptionConfig, callbackUrl, pushMode);
        return success ? R.ok(true, "订阅更新成功") : R.failed(false, "订阅更新失败（无记录更新）");
    }

    @Operation(summary = "订阅主题", description = "订阅消息主题")
    @PostMapping("/subscribe")
    @SysLog("订阅主题")
    @HasPermission("ump_subscription_add")
    public R<Boolean> subscribeTopic(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestBody(required = false) Map<String, Object> subscriptionConfig,
            @RequestParam(required = false) String callbackUrl,
            @RequestParam(required = false) String pushMode) {
        boolean success = umpTopicSubscriptionService.subscribeTopic(
                topicCode, appKey, subscriptionConfig, callbackUrl, pushMode);
        return success ? R.ok(true, "订阅成功") : R.failed(false, "订阅失败");
    }

    @Operation(summary = "取消订阅", description = "取消主题订阅")
    @PostMapping("/unsubscribe")
    @SysLog("取消主题订阅")
    @HasPermission("ump_subscription_del")
    public R<Boolean> unsubscribeTopic(
            @RequestParam String topicCode,
            @RequestParam String appKey) {
        boolean success = umpTopicSubscriptionService.unsubscribeTopic(topicCode, appKey);
        return success ? R.ok(true, "取消订阅成功") : R.failed(false, "取消订阅失败（订阅不存在）");
    }

    @Operation(summary = "查询订阅", description = "根据主题代码和应用标识查询订阅详情")
    @GetMapping("/detail")
    public R<SubscriptionDetailVO> getSubscription(
            @RequestParam String topicCode,
            @RequestParam String appKey) {
        SubscriptionDetailVO subscription = umpTopicSubscriptionService.getSubscription(topicCode, appKey);
        return R.ok(subscription);
    }

    @Operation(summary = "分页查询订阅", description = "根据条件分页查询订阅列表")
    @PostMapping("/page")
    public R<Page<SubscriptionPageVO>> querySubscriptionPage(@Valid @RequestBody SubscriptionQueryDTO queryDTO) {
        Page<SubscriptionPageVO> page = umpTopicSubscriptionService.querySubscriptionPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询主题订阅列表", description = "根据主题代码查询订阅列表")
    @GetMapping("/topic/{topicCode}")
    public R<List<SubscriptionDetailVO>> getSubscriptionsByTopic(
            @Parameter(description = "主题代码", required = true)
            @PathVariable String topicCode,
            @RequestParam(required = false) Integer status) {
        List<SubscriptionDetailVO> subscriptions = umpTopicSubscriptionService.getSubscriptionsByTopic(topicCode, status);
        return R.ok(subscriptions);
    }

    @Operation(summary = "查询应用订阅列表", description = "根据应用标识查询订阅列表")
    @GetMapping("/app/{appKey}")
    public R<List<SubscriptionDetailVO>> getSubscriptionsByApp(
            @Parameter(description = "应用标识", required = true)
            @PathVariable String appKey,
            @RequestParam(required = false) Integer status) {
        List<SubscriptionDetailVO> subscriptions = umpTopicSubscriptionService.getSubscriptionsByApp(appKey, status);
        return R.ok(subscriptions);
    }

    @Operation(summary = "激活订阅", description = "激活主题订阅")
    @PutMapping("/activate/{subscriptionId}")
    @SysLog("激活主题订阅")
    @HasPermission("ump_subscription_enable")
    public R<Boolean> activateSubscription(
            @Parameter(description = "订阅ID", required = true)
            @PathVariable String subscriptionId) {
        boolean success = umpTopicSubscriptionService.activateSubscription(subscriptionId);
        return success ? R.ok(true, "订阅已激活") : R.failed(false, "激活失败（订阅不存在或已激活）");
    }

    @Operation(summary = "停用订阅", description = "停用主题订阅")
    @PutMapping("/deactivate/{subscriptionId}")
    @SysLog("停用主题订阅")
    @HasPermission("ump_subscription_disable")
    public R<Boolean> deactivateSubscription(
            @Parameter(description = "订阅ID", required = true)
            @PathVariable String subscriptionId) {
        boolean success = umpTopicSubscriptionService.deactivateSubscription(subscriptionId);
        return success ? R.ok(true, "订阅已停用") : R.failed(false, "停用失败（订阅不存在或已停用）");
    }

    @Operation(summary = "批量激活订阅", description = "批量激活主题订阅")
    @PutMapping("/activate/batch")
    @SysLog("批量激活主题订阅")
    @HasPermission("ump_subscription_enable")
    public R<Integer> batchActivateSubscriptions(@RequestBody List<String> subscriptionIds) {
        int updatedCount = umpTopicSubscriptionService.batchActivateSubscriptions(subscriptionIds);
        String msg = updatedCount > 0 ? "成功激活 " + updatedCount + " 条订阅" : "没有订阅被激活";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "批量停用订阅", description = "批量停用主题订阅")
    @PutMapping("/deactivate/batch")
    @SysLog("批量停用主题订阅")
    @HasPermission("ump_subscription_disable")
    public R<Integer> batchDeactivateSubscriptions(@RequestBody List<String> subscriptionIds) {
        int updatedCount = umpTopicSubscriptionService.batchDeactivateSubscriptions(subscriptionIds);
        String msg = updatedCount > 0 ? "成功停用 " + updatedCount + " 条订阅" : "没有订阅被停用";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "更新订阅统计", description = "更新订阅的消息统计信息")
    @PutMapping("/stats")
    @SysLog("更新订阅统计")
    @HasPermission("ump_subscription_edit")
    public R<Integer> updateSubscriptionStats(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestParam int increment,
            @RequestParam(required = false) LocalDateTime lastMessageTime) {
        Integer newCount = umpTopicSubscriptionService.updateSubscriptionStats(
                topicCode, appKey, increment, lastMessageTime);
        if (newCount == null) {
            return R.failed("订阅不存在");
        }
        return R.ok(newCount, "统计更新成功");
    }

    @Operation(summary = "检查订阅是否存在", description = "检查主题订阅是否存在")
    @GetMapping("/exists")
    public R<Boolean> existsSubscription(
            @RequestParam String topicCode,
            @RequestParam String appKey,
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        Boolean exists = umpTopicSubscriptionService.existsSubscription(topicCode, appKey, activeOnly);
        return exists != null ? R.ok(exists) : R.failed();
    }

    @Operation(summary = "获取订阅统计", description = "获取主题订阅的统计信息")
    @GetMapping("/statistics")
    public R<SubscriptionStatisticsVO> getSubscriptionStatistics(
            @RequestParam(required = false) String topicCode,
            @RequestParam(required = false) String appKey) {
        SubscriptionStatisticsVO statistics = umpTopicSubscriptionService.getSubscriptionStatistics(topicCode, appKey);
        return R.ok(statistics);
    }

    @Operation(summary = "获取活跃订阅数量", description = "获取主题的活跃订阅数量")
    @GetMapping("/count/active/{topicCode}")
    public R<Integer> countActiveSubscriptions(
            @Parameter(description = "主题代码", required = true)
            @PathVariable String topicCode) {
        Integer count = umpTopicSubscriptionService.countActiveSubscriptions(topicCode);
        return count != null ? R.ok(count) : R.failed();
    }

    @Operation(summary = "删除订阅", description = "删除主题订阅")
    @DeleteMapping("/{subscriptionId}")
    @SysLog("删除主题订阅")
    @HasPermission("ump_subscription_del")
    public R<Boolean> deleteSubscription(
            @Parameter(description = "订阅ID", required = true)
            @PathVariable String subscriptionId) {
        boolean success = umpTopicSubscriptionService.deleteSubscription(subscriptionId);
        return success ? R.ok(true, "订阅删除成功") : R.failed(false, "删除失败（订阅不存在）");
    }

    @Operation(summary = "批量删除订阅", description = "批量删除主题订阅")
    @DeleteMapping("/batch")
    @SysLog("批量删除主题订阅")
    @HasPermission("ump_subscription_del")
    public R<Boolean> batchDeleteSubscriptions(@RequestBody List<String> subscriptionIds) {
        boolean success = umpTopicSubscriptionService.batchDeleteSubscriptions(subscriptionIds);
        return success ? R.ok(true, "批量删除成功") : R.failed(false, "批量删除失败（没有记录被删除）");
    }
}