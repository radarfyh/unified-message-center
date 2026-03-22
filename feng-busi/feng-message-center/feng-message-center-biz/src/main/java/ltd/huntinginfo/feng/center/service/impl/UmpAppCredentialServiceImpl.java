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
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
import ltd.huntinginfo.feng.center.mapper.UmpAppCredentialMapper;
import ltd.huntinginfo.feng.center.service.UmpAppCredentialService;
import ltd.huntinginfo.feng.center.api.dto.AppQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.center.api.vo.AppPageVO;
import ltd.huntinginfo.feng.center.api.vo.AppTotalStatisticsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用认证凭证表服务实现类
 * 打印日志（@Slf4j），异常日志使用log.error,一般错误使用log.warn
 * 各个方法返回有效数据，一般不返回错误代码，错误代码（BusinessEnum）使用异常（BusinessException）来控制
 * 使用baseMapper访问自身数据库映射接口（xxxMapper）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UmpAppCredentialServiceImpl extends ServiceImpl<UmpAppCredentialMapper, UmpAppCredential> implements UmpAppCredentialService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createApp(String appKey, String appSecret, String appName, String appType,
                           String appDesc, String appIcon, String homeUrl, String defaultPushMode,
                           String callbackUrl, String callbackAuthMode, Integer rateLimit,
                           Integer maxMsgSize, List<String> ipWhitelist, LocalDateTime secretExpireTime) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(appSecret) || 
            !StringUtils.hasText(appName) || !StringUtils.hasText(appType)) {
            throw new IllegalArgumentException("应用标识、密钥、名称和类型不能为空");
        }

        // 检查应用标识是否已存在
        boolean exists = baseMapper.existsByAppKey(appKey);
        if (exists) {
            log.warn("应用已存在，应用标识: {}", appKey);
            throw new RuntimeException("应用标识已存在");
        }

        // 创建应用
        UmpAppCredential app = new UmpAppCredential();
        app.setAppKey(appKey);
        app.setAppSecret(appSecret);
        app.setAppName(appName);
        app.setAppType(appType);
        app.setAppDesc(appDesc);
        app.setAppIcon(appIcon);
        app.setHomeUrl(homeUrl);
        app.setDefaultPushMode(defaultPushMode != null ? defaultPushMode : "PUSH");
        app.setCallbackUrl(callbackUrl);
        app.setCallbackAuthMode(callbackAuthMode != null ? callbackAuthMode : "SIGNATURE");
        app.setRateLimit(rateLimit != null ? rateLimit : 1000);
        app.setMaxMsgSize(maxMsgSize != null ? maxMsgSize : 1048576);
        app.setIpWhitelist(ipWhitelist);
        app.setStatus(1); // 默认启用
        app.setSecretExpireTime(secretExpireTime);

        if (save(app)) {
            log.info("应用创建成功，应用标识: {}, 应用名称: {}", appKey, appName);
            return app.getId();
        } else {
            log.error("应用创建失败，应用标识: {}", appKey);
            throw new RuntimeException("应用创建失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateApp(String appId, String appName, String appDesc, String appIcon,
                            String homeUrl, String defaultPushMode, String callbackUrl,
                            String callbackAuthMode, Integer rateLimit, Integer maxMsgSize,
                            List<String> ipWhitelist, Integer status) {
        if (!StringUtils.hasText(appId)) {
            throw new IllegalArgumentException("应用ID不能为空");
        }

        UmpAppCredential app = getById(appId);
        if (app == null) {
            log.warn("应用不存在，应用ID: {}", appId);
            return false;
        }

        if (StringUtils.hasText(appName)) {
            app.setAppName(appName);
        }
        if (appDesc != null) {
            app.setAppDesc(appDesc);
        }
        if (appIcon != null) {
            app.setAppIcon(appIcon);
        }
        if (homeUrl != null) {
            app.setHomeUrl(homeUrl);
        }
        if (StringUtils.hasText(defaultPushMode)) {
            app.setDefaultPushMode(defaultPushMode);
        }
        if (callbackUrl != null) {
            app.setCallbackUrl(callbackUrl);
        }
        if (StringUtils.hasText(callbackAuthMode)) {
            app.setCallbackAuthMode(callbackAuthMode);
        }
        if (rateLimit != null) {
            app.setRateLimit(rateLimit);
        }
        if (maxMsgSize != null) {
            app.setMaxMsgSize(maxMsgSize);
        }
        if (ipWhitelist != null) {
            app.setIpWhitelist(ipWhitelist);
        }
        if (status != null) {
            app.setStatus(status);
        }

        boolean success = updateById(app);
        if (success) {
            log.info("应用更新成功，应用ID: {}, 应用标识: {}", appId, app.getAppKey());
        }
        
        return success;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAppToken(String appKey, String token, Integer expireDays) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("appKey不能为空");
        }

        LocalDateTime expireTime = null;
        if (expireDays != null && expireDays > 0) {
            expireTime = LocalDateTime.now().plusDays(expireDays);
        }

        int updated = baseMapper.updateAppToken(appKey, token, expireTime);
        if (updated > 0) {
            log.info("应用token更新成功，应用标识: {}", appKey);
            return true;
        }
        
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetAppSecret(String appId, String newSecret, Integer expireDays) {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(newSecret)) {
            throw new IllegalArgumentException("应用ID和新密钥不能为空");
        }

        UmpAppCredential app = getById(appId);
        if (app == null) {
            log.warn("应用不存在，应用ID: {}", appId);
            return false;
        }

        LocalDateTime expireTime = null;
        if (expireDays != null && expireDays > 0) {
            expireTime = LocalDateTime.now().plusDays(expireDays);
        }

        int updated = baseMapper.updateAppSecret(appId, newSecret, expireTime);
        if (updated > 0) {
            log.info("应用密钥重置成功，应用ID: {}, 应用标识: {}", appId, app.getAppKey());
            return true;
        }
        
        return false;
    }

    @Override
    public AppDetailVO getAppByKey(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            throw new IllegalArgumentException("应用标识不能为空");
        }

        UmpAppCredential app = baseMapper.selectByAppKey(appKey);
        if (app == null) {
            log.warn("应用不存在，应用标识: {}", appKey);
            return null;
        }

        return convertToDetailVO(app);
    }

    @Override
    public Page<AppPageVO> queryAppPage(AppQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<UmpAppCredential> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getAppName())) {
            queryWrapper.like(UmpAppCredential::getAppName, queryDTO.getAppName());
        }
        
        if (StringUtils.hasText(queryDTO.getAppType())) {
            queryWrapper.eq(UmpAppCredential::getAppType, queryDTO.getAppType());
        }
        
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(UmpAppCredential::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getAppKey())) {
            queryWrapper.eq(UmpAppCredential::getAppKey, queryDTO.getAppKey());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            applySort(queryWrapper, queryDTO.getSortField(), asc);
        } else {
            queryWrapper.orderByDesc(UmpAppCredential::getCreateTime);
        }

        // 执行分页查询
        Page<UmpAppCredential> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<UmpAppCredential> resultPage = page(page, queryWrapper);

        // 转换为VO
        Page<AppPageVO> voPage = new Page<>();
        BeanUtils.copyProperties(resultPage, voPage);
        
        List<AppPageVO> voList = resultPage.getRecords().stream()
                .map(this::convertToPageVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<AppDetailVO> getAvailableApps() {
        List<UmpAppCredential> apps = baseMapper.selectAvailableApps();
        return apps.stream()
                .map(this::convertToDetailVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean enableApp(String appId) {
        return updateAppStatus(appId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean disableApp(String appId) {
        return updateAppStatus(appId, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchEnableApps(List<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }

        int updatedCount = baseMapper.batchUpdateStatus(appIds, 1);
        if (updatedCount > 0) {
            log.info("批量启用应用成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDisableApps(List<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return 0;
        }

        int updatedCount = baseMapper.batchUpdateStatus(appIds, 0);
        if (updatedCount > 0) {
            log.info("批量禁用应用成功，数量: {}", updatedCount);
        }
        
        return updatedCount;
    }

    @Override
    public Boolean isAppAvailable(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return null;
        }

        UmpAppCredential app = baseMapper.selectByAppKey(appKey);
        return app != null && app.getStatus() == 1;
    }

    @Override
    public Boolean validateAppCredential(String appKey, String appSecret) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(appSecret)) {
            return null;
        }

        UmpAppCredential app = baseMapper.selectByAppKey(appKey);
        if (app == null) {
            log.warn("应用不存在，应用标识: {}", appKey);
            return null;
        }

        // 检查应用状态
        if (app.getStatus() != 1) {
            log.warn("应用不可用，应用标识: {}, 状态: {}", 
                    appKey, app.getStatus());
            return null;
        }

        // 检查密钥是否过期
        if (app.getSecretExpireTime() != null && 
            app.getSecretExpireTime().isBefore(LocalDateTime.now())) {
            log.warn("应用密钥已过期，应用标识: {}, 过期时间: {}", 
                    appKey, app.getSecretExpireTime());
            return null;
        }

        // 验证密钥
        boolean valid = appSecret.equals(app.getAppSecret());
        if (!valid) {
            log.warn("应用密钥验证失败，应用标识: {}", appKey);
        }
        
        return valid;
    }

    @Override
    public AppTotalStatisticsVO getAppStatistics() {
        Map<String, Object> statsMap = baseMapper.selectAppStatistics();
        
        AppTotalStatisticsVO statisticsVO = new AppTotalStatisticsVO();
        
        if (statsMap != null) {
            statisticsVO.setTotalCount(((Number) statsMap.getOrDefault("total_count", 0)).longValue());
            statisticsVO.setDirectCount(((Number) statsMap.getOrDefault("direct_count", 0)).longValue());
            statisticsVO.setAgentCount(((Number) statsMap.getOrDefault("agent_count", 0)).longValue());
            statisticsVO.setEnabledCount(((Number) statsMap.getOrDefault("enabled_count", 0)).longValue());
            statisticsVO.setDisabledCount(((Number) statsMap.getOrDefault("disabled_count", 0)).longValue());
            
            // 计算启用率
            if (statisticsVO.getTotalCount() > 0) {
                statisticsVO.setEnableRate((double) statisticsVO.getEnabledCount() / statisticsVO.getTotalCount() * 100);
            }
        }
        
        return statisticsVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteApp(String appId) {
        if (!StringUtils.hasText(appId)) {
            throw new IllegalArgumentException("应用ID不能为空");
        }

        boolean success = this.removeById(appId);
        if (success) {
            log.info("应用删除成功，应用ID: {}", appId);
        }
        
        return success;
    }

    @Override
    public Boolean batchDeleteApps(List<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return false;
        }

        boolean success = this.removeByIds(appIds);
        if (success) {
            log.info("批量删除应用成功");
            return true;
        }
        
        return false;
    }

    @Override
    public Boolean isIpInWhitelist(String appKey, String ipAddress) {
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(ipAddress)) {
            return null;
        }

        UmpAppCredential app = baseMapper.selectByAppKey(appKey);
        if (app == null || app.getIpWhitelist() == null) {
            return null;
        }

        // 如果白名单为空，表示允许所有IP访问
        if (app.getIpWhitelist().isEmpty()) {
            return true;
        }

        return app.getIpWhitelist().contains(ipAddress);
    }

    @Override
    public Boolean isAppSecretExpired(String appKey) {
        if (!StringUtils.hasText(appKey)) {
            return null;
        }

        UmpAppCredential app = baseMapper.selectByAppKey(appKey);
        if (app == null || app.getSecretExpireTime() == null) {
            return null;
        }

        return app.getSecretExpireTime().isBefore(LocalDateTime.now());
    }

    @Override
    public Map<String, Long> getAppTypeStatistics() {
        List<Map<String, Object>> statsList = baseMapper.selectAppTypeCount();
        return statsList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("app_type"),
                        m -> ((Number) m.get("count")).longValue()
                ));
    }

    // ============ 私有方法 ============

    private void applySort(LambdaQueryWrapper<UmpAppCredential> queryWrapper, String sortField, boolean asc) {
        switch (sortField) {
            case "createTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppCredential::getCreateTime);
                } else {
                    queryWrapper.orderByDesc(UmpAppCredential::getCreateTime);
                }
                break;
            case "updateTime":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppCredential::getUpdateTime);
                } else {
                    queryWrapper.orderByDesc(UmpAppCredential::getUpdateTime);
                }
                break;
            case "appName":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppCredential::getAppName);
                } else {
                    queryWrapper.orderByDesc(UmpAppCredential::getAppName);
                }
                break;
            case "rateLimit":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppCredential::getRateLimit);
                } else {
                    queryWrapper.orderByDesc(UmpAppCredential::getRateLimit);
                }
                break;
            case "status":
                if (asc) {
                    queryWrapper.orderByAsc(UmpAppCredential::getStatus);
                } else {
                    queryWrapper.orderByDesc(UmpAppCredential::getStatus);
                }
                break;
            default:
                queryWrapper.orderByDesc(UmpAppCredential::getCreateTime);
                break;
        }
    }

    private boolean updateAppStatus(String appId, Integer status) {
        if (!StringUtils.hasText(appId) || status == null) {
            throw new IllegalArgumentException("应用ID和状态不能为空");
        }

        UmpAppCredential app = getById(appId);
        if (app == null) {
            log.warn("应用不存在，应用ID: {}", appId);
            return false;
        }

        if (app.getStatus().equals(status)) {
            log.debug("应用状态未改变，应用ID: {}, 状态: {}", appId, status);
            return true;
        }

        app.setStatus(status);
        
        boolean success = updateById(app);
        if (success) {
            String action = status == 1 ? "启用" : "禁用";
            log.info("应用{}成功，应用ID: {}, 应用标识: {}", action, appId, app.getAppKey());
        }
        
        return success;
    }

    private AppDetailVO convertToDetailVO(UmpAppCredential app) {
        AppDetailVO vo = new AppDetailVO();
        BeanUtils.copyProperties(app, vo);
        
        // 计算密钥剩余天数
        if (app.getSecretExpireTime() != null) {
            long days = java.time.Duration.between(LocalDateTime.now(), app.getSecretExpireTime()).toDays();
            vo.setSecretRemainingDays(days > 0 ? days : 0);
        }
        
        return vo;
    }

    private AppPageVO convertToPageVO(UmpAppCredential app) {
        AppPageVO vo = new AppPageVO();
        BeanUtils.copyProperties(app, vo);
        
        // 计算密钥剩余天数
        if (app.getSecretExpireTime() != null) {
            long days = java.time.Duration.between(LocalDateTime.now(), app.getSecretExpireTime()).toDays();
            vo.setSecretRemainingDays(days > 0 ? days : 0);
        }
        
        return vo;
    }

	@Override
	public String getAppSecret(String appId) {
		UmpAppCredential app = this.getById(appId);
		return app != null ? app.getAppSecret() : null;
	}

	@Override
	public String getTokenByKey(String appKey) {
		AppDetailVO vo = getAppByKey(appKey);
		
		if (vo == null) return null;
		
		if (isExpired(vo.getAppTokenExpireTime())) {
			return null;
		} else {
			return vo.getAppToken();
		}
	}
	
    private boolean isExpired(LocalDateTime expiredTime) {
        if (expiredTime == null) {
            return false;
        }

        return expiredTime.isBefore(LocalDateTime.now());
    }
}