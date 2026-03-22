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
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
import ltd.huntinginfo.feng.center.api.dto.AppQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.api.vo.AppPageVO;
import ltd.huntinginfo.feng.center.api.vo.AppTotalStatisticsVO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用认证凭证表服务接口
 */
public interface UmpAppCredentialService extends IService<UmpAppCredential> {

    /**
     * 创建应用凭证
     *
     * @param appKey 应用标识
     * @param appSecret 应用密钥
     * @param appName 应用名称
     * @param appType 应用类型
     * @param appDesc 应用描述
     * @param appIcon 应用图标
     * @param homeUrl 应用首页地址
     * @param defaultPushMode 默认推送方式
     * @param callbackUrl 回调地址
     * @param callbackAuthMode 回调认证模式
     * @param rateLimit 速率限制
     * @param maxMsgSize 最大消息大小
     * @param ipWhitelist IP白名单
     * @param secretExpireTime 密钥过期时间
     * @return 应用ID
     */
    String createApp(String appKey, String appSecret, String appName, String appType,
                    String appDesc, String appIcon, String homeUrl, String defaultPushMode,
                    String callbackUrl, String callbackAuthMode, Integer rateLimit,
                    Integer maxMsgSize, List<String> ipWhitelist, LocalDateTime secretExpireTime);

    /**
     * 更新应用凭证
     *
     * @param appId 应用ID
     * @param appName 应用名称
     * @param appDesc 应用描述
     * @param appIcon 应用图标
     * @param homeUrl 应用首页地址
     * @param defaultPushMode 默认推送方式
     * @param callbackUrl 回调地址
     * @param callbackAuthMode 回调认证模式
     * @param rateLimit 速率限制
     * @param maxMsgSize 最大消息大小
     * @param ipWhitelist IP白名单
     * @param status 状态
     * @return 是否成功
     */
    Boolean updateApp(String appId, String appName, String appDesc, String appIcon,
                     String homeUrl, String defaultPushMode, String callbackUrl,
                     String callbackAuthMode, Integer rateLimit, Integer maxMsgSize,
                     List<String> ipWhitelist, Integer status);

    /**
     * 重置应用密钥
     *
     * @param appId 应用ID
     * @param newSecret 新密钥
     * @param expireDays 过期天数
     * @return 是否成功
     */
    Boolean resetAppSecret(String appId, String newSecret, Integer expireDays);
    
    /**
     * 单独获取应用密钥
     *
     * @param appId 应用ID
     * @return 密钥
     */
    String getAppSecret(String appId);

    /**
     * 根据应用标识查询应用
     *
     * @param appKey 应用标识
     * @return 应用详情VO
     */
    AppDetailVO getAppByKey(String appKey);
    
    /**
     * 根据应用标识查询他的token
     *
     * @param appKey 应用标识
     * @return token
     */
    String getTokenByKey(String appKey);

    /**
     * 分页查询应用
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<AppPageVO> queryAppPage(AppQueryDTO queryDTO);

    /**
     * 查询可用应用列表
     *
     * @return 应用列表
     */
    List<AppDetailVO> getAvailableApps();

    /**
     * 启用应用
     *
     * @param appId 应用ID
     * @return 是否成功
     */
    Boolean enableApp(String appId);

    /**
     * 禁用应用
     *
     * @param appId 应用ID
     * @return 是否成功
     */
    Boolean disableApp(String appId);

    /**
     * 批量启用应用
     *
     * @param appIds 应用ID列表
     * @return 成功标志
     */
    Integer batchEnableApps(List<String> appIds);

    /**
     * 批量禁用应用
     *
     * @param appIds 应用ID列表
     * @return 成功禁用数量
     */
    Integer batchDisableApps(List<String> appIds);

    /**
     * 检查应用是否可用
     *
     * @param appKey 应用标识
     * @return 是否可用
     */
    Boolean isAppAvailable(String appKey);

    /**
     * 验证应用凭证
     *
     * @param appKey 应用标识
     * @param appSecret 应用密钥
     * @return 是否验证通过
     */
    Boolean validateAppCredential(String appKey, String appSecret);

    /**
     * 获取应用统计信息
     *
     * @return 统计信息VO
     */
    AppTotalStatisticsVO getAppStatistics();

    /**
     * 逻辑删除应用
     *
     * @param appId 应用ID
     * @return 是否成功
     */
    Boolean deleteApp(String appId);

    /**
     * 批量删除应用
     *
     * @param appIds 应用ID列表
     * @return 成功删除数量
     */
    Boolean batchDeleteApps(List<String> appIds);

    /**
     * 检查IP是否在白名单中
     *
     * @param appKey 应用标识
     * @param ipAddress IP地址
     * @return 是否在白名单中
     */
    Boolean isIpInWhitelist(String appKey, String ipAddress);

    /**
     * 检查应用密钥是否过期
     *
     * @param appKey 应用标识
     * @return 是否过期
     */
    Boolean isAppSecretExpired(String appKey);

    /**
     * 获取应用类型统计
     *
     * @return 应用类型统计
     */
    Map<String, Long> getAppTypeStatistics();

    /**
     * 保存app token
     * 
     * @param appKey 应用KEY
     * @param token  应用token
     * @param expireDays 过期天数
     * @return 是否成功
     */
    Boolean updateAppToken(String appKey, String token, Integer expireDays);
}