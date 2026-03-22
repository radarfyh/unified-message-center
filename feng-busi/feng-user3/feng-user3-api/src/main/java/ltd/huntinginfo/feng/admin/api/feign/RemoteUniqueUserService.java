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
package ltd.huntinginfo.feng.admin.api.feign;

import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
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
 * @author Edison.Feng
 * @date 2025/12/30
 */
@FeignClient(contextId = "remoteUniqueUserService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteUniqueUserService {
	/**
	 * (未登录状态调用，需要加 @NoToken) 通过用户名查询用户、角色信息
	 * @param user 用户查询对象
	 * @return R
	 */
	@NoToken
	@GetMapping("/unique-user/info")
	UniqueUser info(@SpringQueryMap UniqueUser user);
	
    @NoToken
    @GetMapping("/unique-user/detail/{id}")
    Map<String, Object> getDetailById(@PathVariable String id);

    @NoToken
    @PostMapping("/unique-user/detail")
    Map<String, Object> getDetailByQuery(@RequestBody Map<String, Object> user);

    @NoToken
    @PostMapping("/unique-user/list-by-ids")
    List<Map<String, Object>> listByIds(@RequestBody List<String> userIds);

//    @NoToken
//    @PostMapping("/unique-user/list-by-dept")
//    List<Map<String, Object>> listByDept(@RequestBody Map<String, Object> dept);
    
    @NoToken
    @PostMapping("/unique-user/list-by-dept-ids")
    List<Map<String, Object>> listByDeptIds(@RequestBody List<String> deptIds);
    
    @NoToken
    @PostMapping("/unique-user/count-by-dept-ids")
    Integer countByDeptIds(@RequestBody List<String> deptIds);
    
//    @NoToken
//    @PostMapping("/unique-user/list-by-org")
//    List<Map<String, Object>> listByOrg(@RequestBody Map<String, Object> org);

    @NoToken
    @PostMapping("/unique-user/list-by-org-codes")
    List<Map<String, Object>> listByOrgCodes(@RequestBody List<String> orgCodes);
    
    @NoToken
    @PostMapping("/unique-user/count-by-org-codes")
    Integer countByOrgCodes(@RequestBody List<String> orgCodes);
    
    @NoToken
    @PostMapping("/unique-user/list-by-division-codes")
    List<Map<String, Object>> listByDivisionCodes(@RequestBody List<String> divisionCodes);
    
    @NoToken
    @PostMapping("/unique-user/count-by-division-codes")
    Integer countByDivisionCodes(@RequestBody List<String> divisionCodes);
    
    @NoToken
    @PostMapping("/unique-user/list-by-role-codes")
    List<Map<String, Object>> listByRoleCodes(@RequestBody List<String> roleCodes);
    
    @NoToken
    @PostMapping("/unique-user/count-by-role-codes")
    Integer countByRoleCodes(@RequestBody List<String> roleCodes);
}
