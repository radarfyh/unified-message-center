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
import ltd.huntinginfo.feng.center.api.entity.UmpSystemConfig;
import ltd.huntinginfo.feng.center.api.dto.SystemConfigQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigDetailVO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigPageVO;
import ltd.huntinginfo.feng.center.api.vo.SystemConfigStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 系统配置表服务接口
 */
public interface UmpSystemConfigService extends IService<UmpSystemConfig> {

    /**
     * 创建配置
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @param configType 配置类型
     * @param configDesc 配置描述
     * @param category 配置类别
     * @return 配置ID
     */
    String createConfig(String configKey, String configValue, String configType,
                       String configDesc, String category);

    /**
     * 更新配置
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @param configDesc 配置描述
     * @param category 配置类别
     * @param configType 配置类型
     * @param status 状态
     * @return 是否成功
     */
    Boolean updateConfig(String configKey, String configValue, String configDesc,
                        String category, String configType, Integer status);

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置详情VO
     */
    SystemConfigDetailVO getByConfigKey(String configKey);

    /**
     * 分页查询配置
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<SystemConfigPageVO> queryConfigPage(SystemConfigQueryDTO queryDTO);

    /**
     * 根据类别查询配置列表
     *
     * @param category 配置类别
     * @param enabledOnly 是否只查询启用的
     * @return 配置列表
     */
    List<SystemConfigDetailVO> getByCategory(String category, boolean enabledOnly);

    /**
     * 根据配置类型查询配置列表
     *
     * @param configType 配置类型
     * @param enabledOnly 是否只查询启用的
     * @return 配置列表
     */
    List<SystemConfigDetailVO> getByConfigType(String configType, boolean enabledOnly);

    /**
     * 获取所有启用的配置
     *
     * @return 启用的配置列表
     */
    List<SystemConfigDetailVO> getAllEnabled();

    /**
     * 启用配置
     *
     * @param configKey 配置键
     * @return 是否成功
     */
    Boolean enableConfig(String configKey);

    /**
     * 禁用配置
     *
     * @param configKey 配置键
     * @return 是否成功
     */
    Boolean disableConfig(String configKey);

    /**
     * 批量启用配置
     *
     * @param configKeys 配置键列表
     * @return 成功启用数量
     */
    Integer batchEnableConfigs(List<String> configKeys);

    /**
     * 批量禁用配置
     *
     * @param configKeys 配置键列表
     * @return 成功禁用数量
     */
    Integer batchDisableConfigs(List<String> configKeys);

    /**
     * 更新配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     */
    Boolean updateConfigValue(String configKey, String configValue);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    Boolean existsByConfigKey(String configKey);

    /**
     * 获取配置统计信息
     *
     * @return 统计信息VO
     */
    SystemConfigStatisticsVO getConfigStatistics();

    /**
     * 根据配置键列表查询配置
     *
     * @param configKeys 配置键列表
     * @param enabledOnly 是否只查询启用的
     * @return 配置映射
     */
    Map<String, String> getConfigsByKeys(List<String> configKeys, boolean enabledOnly);

    /**
     * 获取配置键值映射
     *
     * @param category 类别（可选）
     * @param enabledOnly 是否只查询启用的
     * @return 配置键值映射
     */
    Map<String, String> getConfigMap(String category, boolean enabledOnly);

    /**
     * 获取配置值（根据配置键）
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 获取配置值（根据配置键，带默认值）
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取整数配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 整数配置值
     */
    Integer getIntConfigValue(String configKey, Integer defaultValue);

    /**
     * 获取长整数配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 长整数配置值
     */
    Long getLongConfigValue(String configKey, Long defaultValue);

    /**
     * 获取布尔配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 布尔配置值
     */
    Boolean getBooleanConfigValue(String configKey, Boolean defaultValue);

    /**
     * 获取浮点数配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 浮点数配置值
     */
    Double getDoubleConfigValue(String configKey, Double defaultValue);

    /**
     * 删除配置
     *
     * @param configKey 配置键
     * @return 是否成功
     */
    Boolean deleteConfig(String configKey);

    /**
     * 批量删除配置
     *
     * @param configKeys 配置键列表
     * @return 成功删除数量
     */
    Boolean batchDeleteConfigs(List<String> configKeys);
}