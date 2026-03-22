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

import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;
import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 部门服务Feign客户端
 * @author Edison.Feng
 * @date 2025/12/30
 */
@FeignClient(contextId = "remoteUniqueDeptService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteUniqueDeptService {

    @NoToken
    @GetMapping("/gov-agency/feign/{id}")
    Map<String, Object> getAgencyById(@PathVariable String id);

    @NoToken
    @PostMapping("/gov-agency/feign/listByIds")
    List<Map<String, Object>> listAgenciesByIds(@RequestBody List<String> ids);

    @NoToken
    @GetMapping("/gov-agency/feign/tree")
    List<Map<String, Object>> getAgenciesTree(@RequestParam(required = false) String name);

    @NoToken
    @GetMapping("/gov-agency/feign/getDescendantList/{id}")
    List<Map<String, Object>> getAgencyDescendantList(@PathVariable String id);
    
    @NoToken
    @GetMapping("/gov-agency/feign/code/{code}")
    GovAgency getAgencyByCode(@PathVariable String code);
}
