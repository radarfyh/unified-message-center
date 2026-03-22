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
package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTemplate;
import ltd.huntinginfo.feng.center.api.json.TemplateVariableDefinition;
import ltd.huntinginfo.feng.center.api.dto.TemplateQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.TemplateDetailVO;
import ltd.huntinginfo.feng.center.api.vo.TemplatePageVO;
import ltd.huntinginfo.feng.center.api.vo.TemplateRenderResult;
import ltd.huntinginfo.feng.center.api.vo.TemplateStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 消息模板表服务接口
 */
public interface UmpMsgTemplateService extends IService<UmpMsgTemplate> {

    /**
     * 创建模板
     *
     * @param templateCode 模板代码
     * @param templateName 模板名称
     * @param templateType 模板类型
     * @param titleTemplate 标题模板
     * @param contentTemplate 内容模板
     * @param variables 模板变量
     * @param defaultPriority 默认优先级
     * @param defaultPushMode 默认推送方式
     * @param defaultCallbackUrl 默认回调地址
     * @return 模板ID
     */
    String createTemplate(String templateCode, String templateName, String templateType,
                         String titleTemplate, String contentTemplate, Map<String, TemplateVariableDefinition> variableDefinitions,
                         Integer defaultPriority, String defaultPushMode, String defaultCallbackUrl);

    /**
     * 更新模板
     *
     * @param id 模板ID
     * @param templateName 模板名称
     * @param titleTemplate 标题模板
     * @param contentTemplate 内容模板
     * @param variables 模板变量
     * @param defaultPriority 默认优先级
     * @param defaultPushMode 默认推送方式
     * @param defaultCallbackUrl 默认回调地址
     * @param status 状态
     * @return 是否成功
     */
    boolean updateTemplate(String id, String templateName, String titleTemplate,
                          String contentTemplate, Map<String, TemplateVariableDefinition> variableDefinitions,
                          Integer defaultPriority, String defaultPushMode,
                          String defaultCallbackUrl, Integer status);

    /**
     * 根据模板代码查询模板
     *
     * @param templateCode 模板代码
     * @return 模板详情VO
     */
    TemplateDetailVO getByTemplateCode(String templateCode);

    /**
     * 分页查询模板
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<TemplatePageVO> queryTemplatePage(TemplateQueryDTO queryDTO);

    /**
     * 根据模板类型查询模板列表
     *
     * @param templateType 模板类型
     * @param enabledOnly 是否只查询启用的
     * @return 模板列表
     */
    List<TemplateDetailVO> getByTemplateType(String templateType, boolean enabledOnly);

    /**
     * 获取所有启用的模板
     *
     * @return 启用的模板列表
     */
    List<TemplateDetailVO> getAllEnabled();

    /**
     * 启用模板
     *
     * @param id 模板ID
     * @return 是否成功
     */
    boolean enableTemplate(String id);

    /**
     * 禁用模板
     *
     * @param id 模板ID
     * @return 是否成功
     */
    boolean disableTemplate(String id);

    /**
     * 批量启用模板
     *
     * @param ids 模板ID列表
     * @return 成功启用数量
     */
    int batchEnableTemplates(List<String> ids);

    /**
     * 批量禁用模板
     *
     * @param ids 模板ID列表
     * @return 成功禁用数量
     */
    int batchDisableTemplates(List<String> ids);

    /**
     * 检查模板代码是否存在
     *
     * @param templateCode 模板代码
     * @return 是否存在
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * 获取模板统计信息
     *
     * @return 统计信息VO
     */
    TemplateStatisticsVO getTemplateStatistics();

    /**
     * 根据模板代码列表查询模板
     *
     * @param templateCodes 模板代码列表
     * @param enabledOnly 是否只查询启用的
     * @return 模板映射
     */
    Map<String, TemplateDetailVO> getTemplatesByCodes(List<String> templateCodes, boolean enabledOnly);

    /**
     * 搜索模板
     *
     * @param keyword 关键词
     * @param enabledOnly 是否只查询启用的
     * @return 模板列表
     */
    List<TemplateDetailVO> searchTemplates(String keyword, boolean enabledOnly);

    /**
     * 渲染模板
     *
     * @param templateCode 模板代码
     * @param variables 模板变量值
     * @return 渲染结果
     */
    TemplateRenderResult renderTemplate(String templateCode, Map<String, Object> variableValues);

    /**
     * 验证模板变量
     *
     * @param templateCode 模板代码
     * @param variables 模板变量值
     * @return 验证结果
     */
    Map<String, Object> validateTemplateVariables(String templateCode, Map<String, Object> variableValues);

    /**
     * 复制模板
     *
     * @param sourceTemplateCode 源模板代码
     * @param targetTemplateCode 目标模板代码
     * @param targetTemplateName 目标模板名称
     * @return 新模板ID
     */
    String copyTemplate(String sourceTemplateCode, String targetTemplateCode, String targetTemplateName);

    /**
     * 删除模板
     *
     * @param id 模板ID
     * @return 是否成功
     */
    boolean deleteTemplate(String id);

    /**
     * 批量删除模板
     *
     * @param ids 模板ID列表
     * @return 成功删除数量
     */
    Boolean batchDeleteTemplates(List<String> ids);
}