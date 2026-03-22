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
package ltd.huntinginfo.feng.center.api.feign;

import ltd.huntinginfo.feng.center.api.vo.AppDetailVO;
import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程应用认证服务接口
 */
@FeignClient(contextId = "remoteAppCredentialService", value = ServiceNameConstants.MESSAGE_CENTER_SERVICE)
public interface RemoteAppCredentialService {

	/**
	 * 通过clientId 查询客户端信息 (未登录，需要无token 内部调用)
	 * @param clientId 用户名
	 * @return R
	 */
	@NoToken
	@GetMapping("/app/credential/detail/{appKey}")
	AppDetailVO getAppCredentialByAppKey(@PathVariable("appKey") String appKey);
	
	/**
	 * 通过clientId 查询客户端信息 (未登录，需要无token 内部调用)
	 * @param clientId 用户名
	 * @return R
	 */
	@NoToken
	@GetMapping("/app/credential/secret/{appKey}")
	String getAppSecretByAppKey(@PathVariable("appKey") String appKey);
}
