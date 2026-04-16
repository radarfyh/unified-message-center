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
package ltd.huntinginfo.feng.auth.support.base.app;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import ltd.huntinginfo.feng.common.security.app.AppKeyAuthenticationToken;
import ltd.huntinginfo.feng.common.security.constants.GrantTypeConstants;
import ltd.huntinginfo.feng.common.security.util.OAuth2EndpointUtils;

/**
 * APPKEY认证模式转换器
 * @author radarfyh
 * @date 2026/01/30
 */
public class AppKeyAuthenticationConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!GrantTypeConstants.APP_KEY.getValue().equals(grantType)) {
            return null;
        }

        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

        // 解析 scope
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        Set<String> requestedScopes = null;
        if (StringUtils.hasText(scope)) {
            requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
        }

        // 获取客户端认证信息（确保已认证）
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        
        if (!(clientPrincipal instanceof OAuth2ClientAuthenticationToken) || !clientPrincipal.isAuthenticated()) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_CLIENT, OAuth2ParameterNames.CLIENT_ID,
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 提取附加参数（timestamp, nonce, signature, bodyMd5）
        Map<String, Object> additionalParameters = parameters.entrySet().stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE)
                        && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

        // 必填参数校验
        if (!additionalParameters.containsKey("timestamp") || !additionalParameters.containsKey("nonce")
                || !additionalParameters.containsKey("signature")) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, "timestamp, nonce, signature are required",
                    OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        return new AppKeyAuthenticationToken((OAuth2ClientAuthenticationToken) clientPrincipal,
                requestedScopes, additionalParameters);
    }
}
