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

import ltd.huntinginfo.feng.center.api.dto.TemplateQueryDTO;
import ltd.huntinginfo.feng.center.api.json.TemplateVariableDefinition;
import ltd.huntinginfo.feng.center.api.vo.TemplateDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TemplatePageVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateRenderResult;
import ltd.huntinginfo.feng.center.api.vo.TemplateStatisticsVO;
import ltd.huntinginfo.feng.center.service.UmpMsgTemplateService;
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
 * 消息模板表控制器
 * 提供消息模板的创建、查询、更新等接口
 * 记录日志到数据库（@SysLog），但不负责打印日志（不使用@Slf4j）
 * 限制访问权限（@HasPermission）
 */
@Validated
@RestController
@RequestMapping("/message/template")
@RequiredArgsConstructor
@Tag(name = "消息模板管理", description = "消息模板的增删改查和状态管理")
public class UmpMsgTemplateController {

    private final UmpMsgTemplateService umpMsgTemplateService;

    @Operation(summary = "创建模板", description = "创建新的消息模板")
    @PostMapping("/create")
    @SysLog("创建消息模板")
    @HasPermission("ump_template_add")
    public R<String> createTemplate(
            @RequestParam String templateCode,
            @RequestParam String templateName,
            @RequestParam String templateType,
            @RequestParam String titleTemplate,
            @RequestParam String contentTemplate,
            @RequestBody(required = false) Map<String, TemplateVariableDefinition> variables,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestParam(required = false) String defaultPushMode,
            @RequestParam(required = false) String defaultCallbackUrl) {
        String templateId = umpMsgTemplateService.createTemplate(
                templateCode, templateName, templateType, titleTemplate, contentTemplate,
                variables, defaultPriority, defaultPushMode, defaultCallbackUrl);
        return R.ok(templateId, "模板创建成功");
    }

    @Operation(summary = "更新模板", description = "更新消息模板信息")
    @PutMapping("/{id}")
    @SysLog("更新消息模板")
    @HasPermission("ump_template_edit")
    public R<Boolean> updateTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable String id,
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) String titleTemplate,
            @RequestParam(required = false) String contentTemplate,
            @RequestBody(required = false) Map<String, TemplateVariableDefinition> variables,
            @RequestParam(required = false) Integer defaultPriority,
            @RequestParam(required = false) String defaultPushMode,
            @RequestParam(required = false) String defaultCallbackUrl,
            @RequestParam(required = false) Integer status) {
        boolean success = umpMsgTemplateService.updateTemplate(
                id, templateName, titleTemplate, contentTemplate, variables,
                defaultPriority, defaultPushMode, defaultCallbackUrl, status);
        return success ? R.ok(true, "模板更新成功") : R.failed(false, "模板更新失败（无记录更新）");
    }

    @Operation(summary = "根据模板代码查询模板", description = "根据模板代码查询模板详情")
    @GetMapping("/code/{templateCode}")
    public R<TemplateDetailVO> getByTemplateCode(
            @Parameter(description = "模板代码", required = true)
            @PathVariable String templateCode) {
        TemplateDetailVO template = umpMsgTemplateService.getByTemplateCode(templateCode);
        return R.ok(template);
    }

    @Operation(summary = "分页查询模板", description = "根据条件分页查询模板列表")
    @PostMapping("/page")
    public R<Page<TemplatePageVO>> queryTemplatePage(@Valid @RequestBody TemplateQueryDTO queryDTO) {
        Page<TemplatePageVO> page = umpMsgTemplateService.queryTemplatePage(queryDTO);
        return R.ok(page);
    }

    @Operation(summary = "根据模板类型查询模板列表", description = "根据模板类型查询模板列表")
    @GetMapping("/type/{templateType}")
    public R<List<TemplateDetailVO>> getByTemplateType(
            @Parameter(description = "模板类型", required = true)
            @PathVariable String templateType,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<TemplateDetailVO> templates = umpMsgTemplateService.getByTemplateType(templateType, enabledOnly);
        return R.ok(templates);
    }

    @Operation(summary = "查询所有启用的模板", description = "查询所有启用的模板列表")
    @GetMapping("/enabled")
    public R<List<TemplateDetailVO>> getAllEnabled() {
        List<TemplateDetailVO> templates = umpMsgTemplateService.getAllEnabled();
        return R.ok(templates);
    }

    @Operation(summary = "启用模板", description = "启用消息模板")
    @PutMapping("/enable/{id}")
    @SysLog("启用模板")
    @HasPermission("ump_template_enable")
    public R<Boolean> enableTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable String id) {
        boolean success = umpMsgTemplateService.enableTemplate(id);
        return success ? R.ok(true, "模板已启用") : R.failed(false, "启用失败（模板不存在或已启用）");
    }

    @Operation(summary = "禁用模板", description = "禁用消息模板")
    @PutMapping("/disable/{id}")
    @SysLog("禁用模板")
    @HasPermission("ump_template_disable")
    public R<Boolean> disableTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable String id) {
        boolean success = umpMsgTemplateService.disableTemplate(id);
        return success ? R.ok(true, "模板已禁用") : R.failed(false, "禁用失败（模板不存在或已禁用）");
    }

    @Operation(summary = "批量启用模板", description = "批量启用消息模板")
    @PutMapping("/enable/batch")
    @SysLog("批量启用模板")
    @HasPermission("ump_template_enable")
    public R<Integer> batchEnableTemplates(@RequestBody List<String> ids) {
        int updatedCount = umpMsgTemplateService.batchEnableTemplates(ids);
        String msg = updatedCount > 0 ? "成功启用 " + updatedCount + " 个模板" : "没有模板被启用（可能已启用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "批量禁用模板", description = "批量禁用消息模板")
    @PutMapping("/disable/batch")
    @SysLog("批量禁用模板")
    @HasPermission("ump_template_disable")
    public R<Integer> batchDisableTemplates(@RequestBody List<String> ids) {
        int updatedCount = umpMsgTemplateService.batchDisableTemplates(ids);
        String msg = updatedCount > 0 ? "成功禁用 " + updatedCount + " 个模板" : "没有模板被禁用（可能已禁用或不存在）";
        return R.ok(updatedCount, msg);
    }

    @Operation(summary = "检查模板代码是否存在", description = "检查模板代码是否存在")
    @GetMapping("/exists/{templateCode}")
    public R<Boolean> existsByTemplateCode(
            @Parameter(description = "模板代码", required = true)
            @PathVariable String templateCode) {
        boolean exists = umpMsgTemplateService.existsByTemplateCode(templateCode);
        return R.ok(exists);
    }

    @Operation(summary = "获取模板统计", description = "获取消息模板的统计信息")
    @GetMapping("/statistics")
    public R<TemplateStatisticsVO> getTemplateStatistics() {
        TemplateStatisticsVO statistics = umpMsgTemplateService.getTemplateStatistics();
        return R.ok(statistics);
    }

    @Operation(summary = "根据模板代码列表查询模板", description = "根据模板代码列表查询模板映射")
    @PostMapping("/batch/codes")
    public R<Map<String, TemplateDetailVO>> getTemplatesByCodes(
            @RequestBody List<String> templateCodes,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        Map<String, TemplateDetailVO> templates = umpMsgTemplateService.getTemplatesByCodes(templateCodes, enabledOnly);
        return R.ok(templates);
    }

    @Operation(summary = "搜索模板", description = "根据关键词搜索模板")
    @GetMapping("/search")
    public R<List<TemplateDetailVO>> searchTemplates(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "true") boolean enabledOnly) {
        List<TemplateDetailVO> templates = umpMsgTemplateService.searchTemplates(keyword, enabledOnly);
        return R.ok(templates);
    }

    @Operation(summary = "渲染模板", description = "根据模板代码和变量渲染模板")
    @PostMapping("/render/{templateCode}")
    @HasPermission("ump_template_use")
    public R<TemplateRenderResult> renderTemplate(
            @Parameter(description = "模板代码", required = true)
            @PathVariable String templateCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        TemplateRenderResult result = umpMsgTemplateService.renderTemplate(templateCode, variables);
        return R.ok(result);
    }

    @Operation(summary = "验证模板变量", description = "验证模板变量是否符合要求")
    @PostMapping("/validate/variables/{templateCode}")
    public R<Map<String, Object>> validateTemplateVariables(
            @Parameter(description = "模板代码", required = true)
            @PathVariable String templateCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        Map<String, Object> result = umpMsgTemplateService.validateTemplateVariables(templateCode, variables);
        return R.ok(result);
    }

    @Operation(summary = "复制模板", description = "复制现有模板创建新模板")
    @PostMapping("/copy")
    @SysLog("复制模板")
    @HasPermission("ump_template_add")
    public R<String> copyTemplate(
            @RequestParam String sourceTemplateCode,
            @RequestParam String targetTemplateCode,
            @RequestParam String targetTemplateName) {
        String newTemplateId = umpMsgTemplateService.copyTemplate(
                sourceTemplateCode, targetTemplateCode, targetTemplateName);
        return R.ok(newTemplateId, "模板复制成功");
    }

    @Operation(summary = "删除模板", description = "删除消息模板")
    @DeleteMapping("/{id}")
    @SysLog("删除模板")
    @HasPermission("ump_template_del")
    public R<Boolean> deleteTemplate(
            @Parameter(description = "模板ID", required = true)
            @PathVariable String id) {
        boolean success = umpMsgTemplateService.deleteTemplate(id);
        return success ? R.ok(true, "模板删除成功") : R.failed(false, "删除失败（模板不存在）");
    }

    @Operation(summary = "批量删除模板", description = "批量删除消息模板")
    @DeleteMapping("/batch")
    @SysLog("批量删除模板")
    @HasPermission("ump_template_del")
    public R<Boolean> batchDeleteTemplates(@RequestBody List<String> ids) {
        boolean success = umpMsgTemplateService.batchDeleteTemplates(ids);
        return success ? R.ok(true, "批量删除成功") : R.failed(false, "批量删除失败（没有记录被删除）");
    }
}