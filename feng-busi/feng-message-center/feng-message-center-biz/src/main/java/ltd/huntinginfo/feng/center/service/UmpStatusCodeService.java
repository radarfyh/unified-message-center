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
import ltd.huntinginfo.feng.center.api.entity.UmpStatusCode;
import ltd.huntinginfo.feng.center.api.dto.StatusCodeQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeDetailVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodePageVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeStatisticsVO;
import ltd.huntinginfo.feng.center.api.vo.StatusCodeTreeVO;

import java.util.List;
import java.util.Map;

/**
 * 消息状态码表服务接口
 */
public interface UmpStatusCodeService extends IService<UmpStatusCode> {

    /**
     * 创建状态码
     *
     * @param statusCode 状态码
     * @param statusName 状态名称
     * @param statusDesc 状态描述
     * @param category 分类
     * @param parentCode 父状态码
     * @param sortOrder 排序
     * @param isFinal 是否为最终状态
     * @param canRetry 是否可重试
     * @return 状态码ID
     */
    String createStatusCode(String statusCode, String statusName, String statusDesc,
                           String category, String parentCode, Integer sortOrder,
                           Integer isFinal, Integer canRetry);

    /**
     * 更新状态码
     *
     * @param id 状态码ID
     * @param statusName 状态名称
     * @param statusDesc 状态描述
     * @param sortOrder 排序
     * @param isFinal 是否为最终状态
     * @param canRetry 是否可重试
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatusCode(String id, String statusName, String statusDesc,
                            Integer sortOrder, Integer isFinal, Integer canRetry,
                            Integer status);

    /**
     * 根据状态码查询
     *
     * @param statusCode 状态码
     * @return 状态码详情VO
     */
    StatusCodeDetailVO getByStatusCode(String statusCode);

    /**
     * 分页查询状态码
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<StatusCodePageVO> queryStatusCodePage(StatusCodeQueryDTO queryDTO);

    /**
     * 根据分类查询状态码列表
     *
     * @param category 分类
     * @param enabledOnly 是否只查询启用的
     * @return 状态码列表
     */
    List<StatusCodeDetailVO> getByCategory(String category, boolean enabledOnly);

    /**
     * 根据父状态码查询子状态码列表
     *
     * @param parentCode 父状态码
     * @param enabledOnly 是否只查询启用的
     * @return 子状态码列表
     */
    List<StatusCodeDetailVO> getByParentCode(String parentCode, boolean enabledOnly);

    /**
     * 获取所有启用的状态码
     *
     * @return 启用的状态码列表
     */
    List<StatusCodeDetailVO> getAllEnabled();

    /**
     * 启用状态码
     *
     * @param id 状态码ID
     * @return 是否成功
     */
    boolean enableStatusCode(String id);

    /**
     * 禁用状态码
     *
     * @param id 状态码ID
     * @return 是否成功
     */
    boolean disableStatusCode(String id);

    /**
     * 批量启用状态码
     *
     * @param ids 状态码ID列表
     * @return 成功启用数量
     */
    int batchEnableStatusCodes(List<String> ids);

    /**
     * 批量禁用状态码
     *
     * @param ids 状态码ID列表
     * @return 成功禁用数量
     */
    int batchDisableStatusCodes(List<String> ids);

    /**
     * 检查状态码是否存在
     *
     * @param statusCode 状态码
     * @return 是否存在
     */
    boolean existsByStatusCode(String statusCode);

    /**
     * 获取状态码统计信息
     *
     * @return 统计信息VO
     */
    StatusCodeStatisticsVO getStatusCodeStatistics();

    /**
     * 获取状态码层级树
     *
     * @param category 分类（可选）
     * @param enabledOnly 是否只查询启用的
     * @return 状态码层级树
     */
    List<StatusCodeTreeVO> getStatusCodeTree(String category, boolean enabledOnly);

    /**
     * 获取状态码映射
     *
     * @param category 分类
     * @param enabledOnly 是否只查询启用的
     * @return 状态码映射
     */
    Map<String, String> getStatusCodeMap(String category, boolean enabledOnly);

    /**
     * 检查状态码是否为最终状态
     *
     * @param statusCode 状态码
     * @return 是否为最终状态
     */
    boolean isFinalStatusCode(String statusCode);

    /**
     * 检查状态码是否可重试
     *
     * @param statusCode 状态码
     * @return 是否可重试
     */
    boolean canRetryStatusCode(String statusCode);

    /**
     * 获取状态码的有效流转目标
     *
     * @param currentStatusCode 当前状态码
     * @return 可流转的目标状态码列表
     */
    List<StatusCodeDetailVO> getValidTransitionStatusCodes(String currentStatusCode);

    /**
     * 验证状态流转
     *
     * @param currentStatusCode 当前状态码
     * @param targetStatusCode 目标状态码
     * @return 是否允许流转
     */
    boolean validateStatusTransition(String currentStatusCode, String targetStatusCode);
}