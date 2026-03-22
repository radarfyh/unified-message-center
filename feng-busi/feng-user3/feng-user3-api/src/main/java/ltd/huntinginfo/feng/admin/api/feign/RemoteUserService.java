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
package ltd.huntinginfo.feng.admin.api.feign;

import ltd.huntinginfo.feng.admin.api.dto.UserDTO;
import ltd.huntinginfo.feng.admin.api.dto.UserInfo;
import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户服务Feign客户端
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteUserService {
	/**
	 * (未登录状态调用，需要加 @NoToken) 通过用户名查询用户、角色信息
	 * @param user 用户查询对象
	 * @return R
	 */
	@NoToken
	@GetMapping("/user/info/query")
	R<UserInfo> info(@SpringQueryMap UserDTO user);
	
    @NoToken
    @GetMapping("/user/details/{id}")
    R<Map<String, Object>> getUserById(@PathVariable String id);

    @NoToken
    @GetMapping("/user/details")
    R<Map<String, Object>> getUserByQuery(@RequestBody Map<String, Object> query);

    @NoToken
    @PostMapping("/user/list-by-ids")
    R<List<Map<String, Object>>> listUsersByIds(@RequestBody List<String> userIds);

    @NoToken
    @PostMapping("/user/list-by-dept")
    R<List<Map<String, Object>>> listUsersByDept(@RequestBody Map<String, Object> query);

    @NoToken
    @PostMapping("/user/list-by-org")
    R<List<Map<String, Object>>> listUsersByOrg(@RequestBody Map<String, Object> query);

    @NoToken
    @PostMapping("/user/list-by-area")
    R<List<Map<String, Object>>> listUsersByArea(@RequestBody Map<String, Object> query);
}
