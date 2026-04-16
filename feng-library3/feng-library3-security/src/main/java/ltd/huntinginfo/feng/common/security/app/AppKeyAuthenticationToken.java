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
package ltd.huntinginfo.feng.common.security.app;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

/**
 * APPKEY认证模式认证令牌管理
 * @author radarfyh
 * @date 2026/01/30
 */
public class AppKeyAuthenticationToken extends AbstractAuthenticationToken {
	private static final long serialVersionUID = 1L;
	
	private final OAuth2ClientAuthenticationToken clientPrincipal;
    private final Set<String> scopes;
    private final Map<String, Object> additionalParameters;
    
    private String appKey;
    private String appSecret;
    private Long timestamp;
    private String nonce;
    private String bodyMd5;
    private String signature;

    public AppKeyAuthenticationToken(OAuth2ClientAuthenticationToken clientPrincipal,
                                      Set<String> scopes,
                                      Map<String, Object> additionalParameters) {
        super(Collections.emptyList());
        this.clientPrincipal = clientPrincipal;
        this.scopes = scopes;
        this.additionalParameters = additionalParameters;
        setAuthenticated(true);
        
        this.appKey = null;
        this.appSecret = null;
        this.timestamp = Long.parseLong((String) additionalParameters.getOrDefault("timestamp", ""));
        this.nonce = (String) additionalParameters.getOrDefault("nonce", "");
        this.bodyMd5 = (String) additionalParameters.getOrDefault("bodyMd5", "");
        this.signature = (String) additionalParameters.getOrDefault("signature", "");
    }

    @Override
    public Object getPrincipal() {
        return this.clientPrincipal;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    // getters...
    public Set<String> getScopes() {
    	return scopes;
    }
    
    public Map<String, Object> getAdditionalParameters() {
    	return additionalParameters;
    }
    
    public String getAppKey() {
    	return appKey;
    }
    
    public String getAppSecret() {
    	return appSecret;
    }
    
    public Long getTimestamp() {
    	return timestamp;
    }
    
    public String getNonce() {
    	return nonce;
    }
    
    public String getBodyMd5() {
    	return bodyMd5;
    }
    
    public String getSignature() {
    	return signature;
    }
}
