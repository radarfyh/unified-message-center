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

import ltd.huntinginfo.feng.center.api.dto.AppKeyAuthRequest;
import ltd.huntinginfo.feng.center.api.vo.AppKeyAuthResponse;

public interface AppAuthService {
    
    /**
     * 应用认证
     * 此处和AppKeyAuthenticationProvider.authenticate生成令牌方式不一致
     * 因此，若要使用本方法认证应用系统，必须把开放式API全部开放，让OAUTH2不验证JWT令牌，而是在开放式API内部验证JWT令牌
     */
    AppKeyAuthResponse authenticateByAppKey(AppKeyAuthRequest request);
    
    /**
     * 刷新应用密钥
     */
    String refreshAppSecret(String appKey);
}