/*
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
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
 *  Author: lengleng
 *
 *  Modified by radarfyh(Edison.Feng) on 2025-12-30.
 *  Copyright (c) 2026 radarfyh(Edison.Feng). All rights reserved.
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */
package ltd.huntinginfo.feng.auth.support.core;

import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.security.app.AppKeyAuthenticationToken;
import ltd.huntinginfo.feng.common.security.constants.GrantTypeConstants;
import ltd.huntinginfo.feng.common.security.service.FengUser;

import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 Token 自定义增强实现类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Slf4j
public class CustomeOAuth2TokenCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {

	/**
	 * 自定义OAuth 2.0 Token属性
	 * @param context 包含OAuth 2.0 Token属性的上下文
	 */
	@Override
	public void customize(OAuth2TokenClaimsContext context) {
		OAuth2TokenClaimsSet.Builder claims = context.getClaims();
		claims.claim(SecurityConstants.DETAILS_LICENSE, SecurityConstants.PROJECT_LICENSE);
		String clientId = context.getAuthorizationGrant().getName();
		
		log.debug("CustomeOAuth2TokenCustomizer.customize context.getAuthorizationGrant().getName(): {}", clientId);
		
		claims.claim(SecurityConstants.CLIENT_ID, clientId);
		// 客户端模式不返回具体用户信息
		String grantType = context.getAuthorizationGrantType().getValue();
		if (SecurityConstants.CLIENT_CREDENTIALS.equals(grantType) ||
				GrantTypeConstants.APP_KEY.getValue().equals(grantType)) {
	        // 对于 app_key 模式，可以添加应用特有信息（如 appKey）
	        if (GrantTypeConstants.APP_KEY.getValue().equals(grantType)) {
	            // 从 principal 中获取 appKey 信息（需确认 principal 类型）
	            Object principal = context.getPrincipal().getPrincipal();
	            if (principal instanceof AppKeyAuthenticationToken) {
	            	AppKeyAuthenticationToken appKeyAuthenticationToken = (AppKeyAuthenticationToken) principal;
	                String appKey = appKeyAuthenticationToken.getAppKey();
	                claims.claim(SecurityConstants.APP_KEY, appKey);
	                OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken = ((OAuth2ClientAuthenticationToken)appKeyAuthenticationToken.getPrincipal());
	                clientId = oAuth2ClientAuthenticationToken.getRegisteredClient().getClientId();
	                claims.claim(SecurityConstants.CLIENT_ID, clientId);
	                
	                log.debug("CustomeOAuth2TokenCustomizer.customize appKeyAuthenticationToken.getAppKey(): {} "
	                		+ "oAuth2ClientAuthenticationToken.getRegisteredClient().getClientId(): {}", appKey, clientId);
	            }
	        }
	        
			return;
		}

		FengUser fengUser = (FengUser) context.getPrincipal().getPrincipal();
		log.debug("CustomeOAuth2TokenCustomizer.customize fengUser: {}", JSONUtil.toJsonStr(fengUser));
		
		claims.claim(SecurityConstants.DETAILS_USER, fengUser);
		claims.claim(SecurityConstants.DETAILS_USER_ID, fengUser.getId());
		claims.claim(SecurityConstants.USERNAME, fengUser.getUsername());
	}

}
