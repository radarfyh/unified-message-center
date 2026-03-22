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

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;
import ltd.huntinginfo.feng.common.core.util.SignatureUtils;
import ltd.huntinginfo.feng.center.api.dto.AppKeyAuthRequest;
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import ltd.huntinginfo.feng.center.api.entity.UmpSystemLog;
import ltd.huntinginfo.feng.center.api.utils.JwtUtil;
import ltd.huntinginfo.feng.center.api.vo.AppKeyAuthResponse;
import ltd.huntinginfo.feng.center.mapper.UmpAppCredentialMapper;
import ltd.huntinginfo.feng.center.mapper.UmpAppPermissionMapper;
import ltd.huntinginfo.feng.center.service.AppAuthService;
import ltd.huntinginfo.feng.center.service.UmpSystemLogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Autowired
    private UmpAppCredentialMapper appCredentialMapper;
    @Autowired
    private UmpAppPermissionMapper appPermissionMapper;
    @Autowired
    private UmpSystemLogService systemLogService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public AppKeyAuthResponse authenticateByAppKey(AppKeyAuthRequest request) {
        AppKeyAuthResponse response = new AppKeyAuthResponse();
        try {
            // 1. 基础参数校验
            if (StrUtil.hasBlank(request.getAppKey(), request.getSignature(), request.getNonce()) || request.getTimestamp() == null) {
                return buildFailedResponse(response, BusinessEnum.PARAM_MISSING, "必填参数缺失");
            }

            // 2. 时间窗口校验
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - request.getTimestamp()) > CommonConstants.NONCE_TIME_WINDOW) {
                return buildFailedResponse(response, BusinessEnum.TIMESTAMP_EXPIRED, "时间戳超时");
            }

            // 3. Nonce 防重放
            String nonceKey = CommonConstants.NONCE_CACHE_PREFIX + request.getNonce();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(nonceKey))) {
                return buildFailedResponse(response, BusinessEnum.REPLAY_ATTACK, "重复的Nonce");
            }

            // 4. 查询应用凭证
            UmpAppCredential credential = appCredentialMapper.selectByAppKey(request.getAppKey());
            log.debug("app credential: {}", JSONUtil.toJsonPrettyStr(credential));
            
            if (credential == null) {
                return buildFailedResponse(response, BusinessEnum.APPKEY_INVALID, "应用标识无效");
            }
            if (credential.getStatus() != 1) {
                return buildFailedResponse(response, BusinessEnum.APP_DISABLED, "应用已被禁用");
            }
            if (credential.getSecretExpireTime() != null && credential.getSecretExpireTime().isBefore(LocalDateTime.now())) {
                return buildFailedResponse(response, BusinessEnum.APP_EXPIRED, "应用凭证已过期");
            }
            
            // 4.1 校验 bodyMd5 格式（如果存在），确保其符合 MD5 规范
            if (StrUtil.isNotBlank(request.getBodyMd5()) && !SignatureUtils.isValidMd5(request.getBodyMd5())) {
                return buildFailedResponse(response, BusinessEnum.PARAM_MISSING, "bodyMd5 值错误");
            }

            // 5. 验证签名
            if (!SignatureUtils.verifySignature(request.getAppKey(), 
            		credential.getAppSecret(), 
            		request.getTimestamp(), 
            		request.getNonce(), 
            		request.getBodyMd5(), 
            		request.getSignature())) {
                return buildFailedResponse(response, BusinessEnum.SIGNATURE_INVALID, "签名验证失败");
            }

            // 6. 缓存 Nonce
            redisTemplate.opsForValue().set(nonceKey, "1", CommonConstants.NONCE_TIME_WINDOW, TimeUnit.MILLISECONDS);

            // 7. 获取应用权限
            List<UmpAppPermission> permissions = appPermissionMapper.selectAvailablePermissions(request.getAppKey());
            List<String> permissionCodes = permissions.stream().map(UmpAppPermission::getResourceCode).collect(Collectors.toList());

            // 8. 生成 Token 注意：此处和AppKeyAuthenticationProvider.authenticate生成令牌方式不一致
            String token = jwtUtil.generateToken(credential.getId(), permissionCodes);

            // 9. 构建成功响应
            response.setSuccess(true);
            response.setAppId(credential.getId());
            response.setAppName(credential.getAppName());
            response.setAppKey(credential.getAppKey());
            response.setAgencyCode(credential.getAgencyCode());
            response.setPermissions(permissionCodes);
            response.setExpiresTime(jwtUtil.getExpireInSeconds());
            response.setToken(token);
            
            // 10. 写token到数据库
            appCredentialMapper.updateAppToken(credential.getAppKey(), token, LocalDateTime.now().plusSeconds(jwtUtil.getExpireInSeconds()));

            log.info("应用认证成功 - appKey: {}", request.getAppKey());
            return response;
        } catch (Exception e) {
            log.error("应用认证异常 - appKey: {}", request.getAppKey(), e);
            return buildFailedResponse(response, BusinessEnum.SYSTEM_ERROR, "系统异常");
        }
    }
    
    @Override
    public String refreshAppSecret(String appKey) {
        UmpAppCredential credential = appCredentialMapper.selectByAppKey(appKey);
        if (credential == null) {
            throw new BusinessException(Integer.valueOf(BusinessEnum.APPKEY_INVALID.getCode()), BusinessEnum.APPKEY_INVALID.getMsg());
        }

        String newSecret = UUID.randomUUID().toString().replace("-", "");
        
        // 存密文无法获取原始密钥，所以改为直接存 20250802
        //credential.setAppSecret(passwordEncoder.encode(newSecret));
        credential.setAppSecret(newSecret);
        appCredentialMapper.updateById(credential);
        
        return newSecret;
    }

    private AppKeyAuthResponse buildFailedResponse(AppKeyAuthResponse response, 
                                                 BusinessEnum errorCode, 
                                                 String errorMsg) {
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        
        // 记录认证失败日志
        UmpSystemLog authLog = new UmpSystemLog();
        authLog.setLogType(MqMessageEventConstants.SystemLogType.AUTH);
        authLog.setAppKey(response.getAppKey());
        authLog.setAuthType("APPKEY");
        authLog.setAuthStatus(0);
        authLog.setAuthErrorCode(errorCode.getCode().toString());
        systemLogService.save(authLog);
        
        return response;
    }
}