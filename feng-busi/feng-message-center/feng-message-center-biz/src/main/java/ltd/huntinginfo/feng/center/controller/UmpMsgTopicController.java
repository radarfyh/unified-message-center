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

import ltd.huntinginfo.feng.center.api.dto.TopicQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TopicDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TopicPageVO;
import ltd.huntinginfo.feng.center.api.vo.TopicStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgTopicService;
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
 * 消息主题表控制器
 * 提供消息主题的创建、查询、更新等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/message/topic")
@RequiredArgsConstructor
@Tag(name = "消息主题管理", description = "消息主题的增删改查和状态管理")
public class UmpMsgTopicController {

    private final UmpMsgTopicService umpMsgTopicService;

    @Operation(summary = "创建主题", description = "创建新的消息主题")
    @PostMapping("/create")
    @SysLog("创建消息主题")
    @HasPermission("ump_topic_add")
    public R<String> createTopic(
            @RequestParam String topicCode,
            @RequestParam String topicName,
            @RequestParam String topicType,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String defaultMsgType,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestBody(required = false) Map<String, Object> routingRules,
            @RequestParam(required = false) Integer maxSubscribers) {
        String topicId = umpMsgTopicService.createTopic(
                topicCode, topicName, topicType, description, defaultMsgType,
                defaultPriority, routingRules, maxSubscribers);
        return R.ok(topicId, "主题创建成功");
    }

    @Operation(summary = "更新主题", description = "更新消息主题信息")
    @PutMapping("/{topicId}")
    @SysLog("更新消息主题")
    @HasPermission("ump_topic_edit")
    public R<Boolean> updateTopic(
            @Parameter(description = "主题ID", required = true)
            @PathVariable String topicId,
            @RequestParam(required = false) String topicName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String defaultMsgType,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestBody(required = false) Map<String, Object> routingRules,
            @RequestParam(required = false) Integer maxSubscribers,
            @RequestParam(required = false) Integer status) {
        boolean success = umpMsgTopicService.updateTopic(
                topicId, topicName, description, defaultMsgType, defaultPriority,
                routingRules, maxSubscribers, status);
        return success ? R.ok(true, "主题更新成功") : R.failed(false, "主题更新失败（无记录更新）");
    }

    @Operation(summary = "根据主题代码查询主题", description = "根据主题代码查询主题详情")
    @GetMapping("/code/{topicCode}")
    public R<TopicDetailVO> getTopicByCode(
            @Parameter(description = "主题代码", required = true)
            @PathVariable String topicCode) {
        TopicDetailVO topic = umpMsgTopicService.getTopicByCode(topicCode);
        return R.ok(topic);
    }

    @Operation(summary = "分页查询主题", description = "根据条件分页查询主题列表")
    @PostMapping("/page")
    public R<Page<TopicPageVO>> queryTopicPage(@Valid @RequestBody TopicQueryDTO queryDTO) {
        Page<TopicPageVO> page = umpMsgTopicService.queryTopicPage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "查询可用主题列表", description = "查询所有可用的消息主题")
    @GetMapping("/available")
    public R<List<TopicDetailVO>> getAvailableTopics() {
        List<TopicDetailVO> topics = umpMsgTopicService.getAvailableTopics();
        return R.ok(topics);
    }

    @Operation(summary = "启用主题", description = "启用消息主题")
    @PutMapping("/enable/{topicId}")
    @SysLog("启用主题")
    @HasPermission("ump_topic_enable")
    public R<Boolean> enableTopic(
            @Parameter(description = "主题ID", required = true)
            @PathVariable String topicId) {
        boolean success = umpMsgTopicService.enableTopic(topicId);
        return success ? R.ok(true, "主题已启用") : R.failed(false, "启用失败（主题不存在或已启用）");
    }

    @Operation(summary = "禁用主题", description = "禁用消息主题")
    @PutMapping("/disable/{topicId}")
    @SysLog("禁用主题")
    @HasPermission("ump_topic_disable")
    public R<Boolean> disableTopic(
            @Parameter(description = "主题ID", required = true)
            @PathVariable String topicId) {
        boolean success = umpMsgTopicService.disableTopic(topicId);
        return success ? R.ok(true, "主题已禁用") : R.failed(false, "禁用失败（主题不存在或已禁用）");
    }

    @Operation(summary = "批量启用主题", description = "批量启用消息主题")
    @PutMapping("/enable/batch")
    @SysLog("批量启用主题")
    @HasPermission("ump_topic_enable")
    public R<Integer> batchEnableTopics(@RequestBody List<String> topicIds) {
        int updatedCount = umpMsgTopicService.batchEnableTopics(topicIds);
        String msg = updatedCount > 0 ? "成功启用 " + updatedCount + " 个主题" : "没有主题被启用（可能已启用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "批量禁用主题", description = "批量禁用消息主题")
    @PutMapping("/disable/batch")
    @SysLog("批量禁用主题")
    @HasPermission("ump_topic_disable")
    public R<Integer> batchDisableTopics(@RequestBody List<String> topicIds) {
        int updatedCount = umpMsgTopicService.batchDisableTopics(topicIds);
        String msg = updatedCount > 0 ? "成功禁用 " + updatedCount + " 个主题" : "没有主题被禁用（可能已禁用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "更新主题订阅者数量", description = "更新主题的订阅者数量")
    @PutMapping("/subscriber-count/{topicId}")
    @SysLog("更新主题订阅者数量")
    @HasPermission("ump_topic_edit")
    public R<Integer> updateTopicSubscriberCount(
            @Parameter(description = "主题ID", required = true)
            @PathVariable String topicId,
            @Parameter(description = "增量", required = true)
            @RequestParam int increment) {
        Integer newCount = umpMsgTopicService.updateTopicSubscriberCount(topicId, increment);
        if (newCount == null) {
            return R.failed("主题不存在");
        }
        return R.ok(newCount, "订阅者数量更新成功");
    }

    @Operation(summary = "检查主题可用性", description = "检查主题是否可用")
    @GetMapping("/available/{topicCode}")
    public R<Boolean> isTopicAvailable(
            @Parameter(description = "主题代码", required = true)
            @PathVariable String topicCode) {
        boolean available = umpMsgTopicService.isTopicAvailable(topicCode);
        return R.ok(available);
    }

    @Operation(summary = "获取主题统计", description = "获取主题的统计信息")
    @GetMapping("/statistics")
    public R<TopicStatisticsVO> getTopicStatistics() {
        TopicStatisticsVO statistics = umpMsgTopicService.getTopicStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "删除主题", description = "删除消息主题")
    @DeleteMapping("/{topicId}")
    @SysLog("删除主题")
    @HasPermission("ump_topic_del")
    public R<Boolean> deleteTopic(
            @Parameter(description = "主题ID", required = true)
            @PathVariable String topicId) {
        boolean success = umpMsgTopicService.deleteTopic(topicId);
        return success ? R.ok(true, "主题删除成功") : R.failed(false, "删除失败（主题不存在）");
    }

    @Operation(summary = "批量删除主题", description = "批量删除消息主题")
    @DeleteMapping("/batch")
    @SysLog("批量删除主题")
    @HasPermission("ump_topic_del")
    public R<Boolean> batchDeleteTopics(@RequestBody List<String> topicIds) {
        boolean success = umpMsgTopicService.batchDeleteTopics(topicIds);
        return success ? R.ok(true, "批量删除成功") : R.failed(false, "批量删除失败（没有记录被删除）");
    }
}