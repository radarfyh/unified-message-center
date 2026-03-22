package ltd.huntinginfo.feng.admin.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ltd.huntinginfo.feng.admin.api.dto.UniqueUserDTO;
import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueUserInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueUserInfoVO;
import ltd.huntinginfo.feng.admin.service.dict.UniqueUserService;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import ltd.huntinginfo.feng.common.security.annotation.Inner;

/**
 * 统一用户代码管理控制器
 *
 * @author Edison.Feng
 * @date 2025/12/30
 */
@RestController
@AllArgsConstructor
@RequestMapping("/unique-user")
@Tag(name = "统一用户（员工）管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class UniqueUserController {

    private final UniqueUserService uniqueUserService;

    /**
     * 分页查询员工
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询员工")
    public R<IPage<UniqueUserInfoVO>> page(
            Page<UniqueUser> page,
            UniqueUserInfoDTO dto) {
        return R.ok(uniqueUserService.page(page, dto));
    }

    /**
     * 查询员工详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "员工详情")
    public R<UniqueUserInfoVO> getById(@PathVariable String id) {
        return R.ok(uniqueUserService.getUserById(id));
    }
    
    /**
     * 根据条件查询用户信息（Feign）
     */
    @Inner
    @GetMapping("/info")
    public UniqueUser info(@SpringQueryMap UniqueUser user) {
    	if (BeanUtil.isEmpty(user)) {
    		return null;
    	}
    	UniqueUserDTO dto = new UniqueUserDTO();
    	BeanUtil.copyProperties(user, dto);
    	UniqueUser userInfo = uniqueUserService.getUserInfo(dto);
        return userInfo;
    }

    /**
     * 根据ID查询用户详情（Feign，返回Map）
     */
    @Inner
    @GetMapping("/detail/{id}")
    public Map<String, Object> getDetailById(@PathVariable String id) {
        UniqueUser user = uniqueUserService.getById(id);
        if (user == null) {
            return new HashMap<>();
        }
        return convertUserToMap(user);
    }

    /**
     * 根据查询条件获取单个用户（Feign）
     */
    @Inner
    @PostMapping("/detail")
    public Map<String, Object> getDetailByQuery(@RequestBody Map<String, Object> query) {
    	UniqueUserDTO dto = new UniqueUserDTO();
    	Object loginId = query.get("loginId");
    	Object id = query.get("id");
    	Object idCard = query.get("idCard");
    	if (loginId != null) {
    		dto.setLoginId(loginId.toString());
    	}
    	if (id != null) {
    		dto.setId(id.toString());
    	}
    	if (idCard != null) {
    		dto.setIdCard(idCard.toString());
    	}
        UniqueUser user = uniqueUserService.getUserInfo(dto);
        if (user == null) {
            return new HashMap<>();
        }
        return convertUserToMap(user);
    }
    
    /**
     * 新增员工
     */
    @SysLog("新增员工")
    @PostMapping
    @HasPermission("unique_user_add")
    public R<Boolean> save(@RequestBody @Valid UniqueUserInfoDTO dto) {
        return R.ok(uniqueUserService.save(dto));
    }

    /**
     * 修改员工
     */
    @SysLog("修改员工")
    @PutMapping
    @HasPermission("unique_user_edit")
    public R<Boolean> update(@RequestBody @Valid UniqueUserInfoDTO dto) {
        return R.ok(uniqueUserService.updateById(dto));
    }

    /**
     * 删除员工
     */
    @SysLog("删除员工")
    @DeleteMapping("/{id}")
    @HasPermission("unique_user_del")
    public R<Boolean> delete(@PathVariable String id) {
        return R.ok(uniqueUserService.removeById(id));
    }

    /**
     * ⭐ 为员工开通系统登录账号
     */
    @SysLog("开通员工登录账号")
    @PostMapping("/{id}/enable-login")
    @HasPermission("unique_user_enable_login")
    @Operation(summary = "开通登录账号")
    public R<Boolean> enableLogin(@PathVariable String id) {
        return R.ok(uniqueUserService.enableLoginUser(id));
    }
    
    // ================================================
    
    /**
     * 根据角色代码列表查询用户列表（Feign）
     */
    @Inner
    @PostMapping("/list-by-role-codes")
    public List<Map<String, Object>> listByRoleCodes(@RequestBody List<String> roleCodes) {
        if (CollectionUtils.isEmpty(roleCodes)) {
            return Collections.emptyList();
        }
        Set<String> userIdSet = new HashSet<>();
        List<UniqueUser> resultUsers = new ArrayList<>();
        for (String roleCode : roleCodes) {
            LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
            wrapper.like(UniqueUser::getUniqueRoles, roleCode)
            	.last("LIMIT " + CommonConstants.PAGE_LIST_QUERY_LIMIT);
            List<UniqueUser> users = uniqueUserService.list(wrapper);
            for (UniqueUser user : users) {
                if (userIdSet.add(user.getId())) {
                    resultUsers.add(user);
                }
            }
        }
        List<Map<String, Object>> result = resultUsers.stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
        return result;
    }
    
    /**
     * 根据角色代码列表查询用户数量（Feign）
     */
    @Inner
    @PostMapping("/count-by-role-codes")
    public Integer countByRoleCodes(@RequestBody List<String> roleCodes) {
        if (CollectionUtils.isEmpty(roleCodes)) {
            return 0;
        }
        
        Integer total = 0;
        for (String roleCode : roleCodes) {
            LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
            wrapper.like(UniqueUser::getUniqueRoles, roleCode);
            Integer count = Convert.toInt(uniqueUserService.count(wrapper), 0);
            total += count;
        }
        return total;
    }

    /**
     * 根据行政区划代码列表查询用户列表（Feign）
     */
    @Inner
    @PostMapping("/list-by-division-codes")
    public List<Map<String, Object>> listByDivisionCodes(@RequestBody List<String> divisionCodes) {
        if (CollectionUtils.isEmpty(divisionCodes)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UniqueUser::getDivisionCode, divisionCodes)
               .last("LIMIT " + CommonConstants.PAGE_LIST_QUERY_LIMIT);
        List<UniqueUser> users = uniqueUserService.list(wrapper);
        List<Map<String, Object>> result = users.stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
        return result;
    }
    
    /**
     * 根据行政区划代码列表查询用户数量（Feign）
     */
    @Inner
    @PostMapping("/count-by-division-codes")
    public Integer countByDivisionCodes(@RequestBody List<String> divisionCodes) {
        if (CollectionUtils.isEmpty(divisionCodes)) {
            return 0;
        }
        LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UniqueUser::getDivisionCode, divisionCodes);
        return Convert.toInt(uniqueUserService.count(wrapper), 0);
    }
    
    /**
     * 根据机构代码列表查询用户列表（Feign）
     */
    @Inner
    @PostMapping("/list-by-org-codes")
    public List<Map<String, Object>> listByOrgCodes(@RequestBody List<String> orgCodes) {
        if (CollectionUtils.isEmpty(orgCodes)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UniqueUser::getUniqueOrgCode, orgCodes)               
        	.last("LIMIT " + CommonConstants.PAGE_LIST_QUERY_LIMIT);
        List<UniqueUser> users = uniqueUserService.list(wrapper);
        List<Map<String, Object>> result = users.stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
        return result;
    }
    
    /**
     * 根据机构代码列表查询用户数量（Feign）
     */
    @Inner
    @PostMapping("/count-by-org-codes")
    public Integer countByOrgCodes(@RequestBody List<String> orgCodes) {
        if (CollectionUtils.isEmpty(orgCodes)) {
            return 0;
        }
        LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UniqueUser::getUniqueOrgCode, orgCodes);
        return Convert.toInt(uniqueUserService.count(wrapper), 0);
    }
    
//    /**
//     * 根据组织查询用户列表（Feign）
//     */
//    @Inner
//    @PostMapping("/list-by-org")
//    public List<Map<String, Object>> listByOrg(@RequestBody Map<String, Object> org) {
//        LambdaQueryWrapper<UniqueUser> wrapper = new LambdaQueryWrapper<>();
//        // 示例：org 包含 orgCode
//        if (org.containsKey("orgCode")) {
//            wrapper.eq(UniqueUser::getUniqueOrgCode, org.get("orgCode").toString());
//        }
//        List<UniqueUser> users = uniqueUserService.list(wrapper);
//        List<Map<String, Object>> result = users.stream()
//                .map(this::convertUserToMap)
//                .collect(Collectors.toList());
//        return result;
//    }
    
//    /**
//     * 根据部门查询用户列表（Feign）
//     */
//    @Inner
//    @PostMapping("/list-by-dept")
//    public List<Map<String, Object>> listByDept(@RequestBody Map<String, Object> dept) {
//        LambdaQueryWrapper<UniqueUser> wrapper = new LambdaQueryWrapper<>();
//        // 根据 dept 参数构建查询条件，例如 deptId 对应 agencyCode 或 orgCode
//        if (dept.containsKey("deptId")) {
//            wrapper.eq(UniqueUser::getAgencyCode, dept.get("deptId").toString());
//        }
//        List<UniqueUser> users = uniqueUserService.list(wrapper);
//        List<Map<String, Object>> result = users.stream()
//                .map(this::convertUserToMap)
//                .collect(Collectors.toList());
//        return result;
//    }

    /**
     * 根据部门ID（机关单位代码）列表查询用户列表（Feign）
     */
    @Inner
    @PostMapping("/list-by-dept-ids")
    public List<Map<String, Object>> listByDeptIds(@RequestBody List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UniqueUser::getAgencyCode, deptIds)               
        	.last("LIMIT " + CommonConstants.PAGE_LIST_QUERY_LIMIT);
        List<UniqueUser> users = uniqueUserService.list(wrapper);
        List<Map<String, Object>> result = users.stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
        return result;
    }
    
    /**
     * 根据部门ID（机关单位代码）列表查询用户数量（Feign）
     */
    @Inner
    @PostMapping("/count-by-dept-ids")
    public Integer countByDeptIds(@RequestBody List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return 0;
        }
        LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.in(UniqueUser::getAgencyCode, deptIds);
        return Convert.toInt(uniqueUserService.count(wrapper), 0);
    }

    /**
     * 根据用户ID列表查询用户列表（Feign）
     */
    @Inner
    @PostMapping("/list-by-ids")
    public List<Map<String, Object>> listByIds(@RequestBody List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        if (userIds.size() > CommonConstants.PAGE_LIST_QUERY_LIMIT) {
            // 截断至前1000条
            userIds = new ArrayList<>(userIds.subList(0, CommonConstants.PAGE_LIST_QUERY_LIMIT));
        }
        List<UniqueUser> users = uniqueUserService.listByIds(userIds);
        List<Map<String, Object>> result = users.stream()
                .map(this::convertUserToMap)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * 将 UniqueUser 转换为 Map，包含必要字段
     */
    private Map<String, Object> convertUserToMap(UniqueUser user) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("id", user.getId());
//        map.put("name", user.getName());
//        map.put("username", user.getLoginId());
//        map.put("phone", user.getMobile());
//        map.put("email", user.getEmail());
//        map.put("uniqueOrgCode", user.getUniqueOrgCode());
//        map.put("divisionCode", user.getDivisionCode());
//        map.put("agencyCode", user.getAgencyCode()); // 对应部门代码
//        map.put("uniqueRoles", user.getUniqueRoles()); // 逗号分隔的角色代码
//        return map;
    	return BeanUtil.beanToMap(user);
    }
}
