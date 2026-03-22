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

package ltd.huntinginfo.feng.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueOrgInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueOrg;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueOrgInfoVO;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueOrgTreeVO;
import ltd.huntinginfo.feng.admin.service.dict.UniqueOrgService;
import ltd.huntinginfo.feng.admin.service.dict.UniqueUserService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;
import ltd.huntinginfo.feng.common.security.annotation.Inner;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.hutool.core.bean.BeanUtil;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统一机构（UniqueOrg）代码管理控制器
 * @author Edison.Feng
 * @date 2025/12/30
 */
@RestController
@AllArgsConstructor
@RequestMapping("/unique-org")
@Tag(name = "统一机构管理模块", description = "UniqueOrg")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class UniqueOrgController {

    private final UniqueOrgService uniqueOrgService;
    private final UniqueUserService uniqueUserService;

    /**
     * 根据 ID 查询机构详情
     *
     * @param id 主键ID
     * @return 机构详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询机构", description = "根据主键ID查询统一机构详情")
    public R<UniqueOrgInfoVO> getById(@PathVariable String id) {
        return R.ok(uniqueOrgService.getById(id));
    }
    
    /**
     * 根据机构编码查询机构（带缓存）
     *
     * @param code 机构编码
     * @return 机构实体
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "根据机构编码查询", description = "根据机构编码查询统一机构信息")
    public R<UniqueOrg> getByCode(@PathVariable String code) {
        return R.ok(uniqueOrgService.getByCode(code));
    }

    /**
     * 分页查询统一机构
     *
     * @param page 分页参数
     * @param uniqueOrgInfo 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询机构", description = "分页查询统一机构信息")
    public R<IPage<UniqueOrgInfoVO>> page(
            Page<UniqueOrg> page,
            UniqueOrgInfoDTO uniqueOrgInfo) {
        return R.ok(uniqueOrgService.page(page, uniqueOrgInfo));
    }

    /**
     * 查询统一机构列表（不分页）
     *
     * @param uniqueOrgInfo 查询条件
     * @return 机构列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询机构列表", description = "根据条件查询统一机构列表")
    public R<List<UniqueOrgInfoVO>> list(UniqueOrgInfoDTO uniqueOrgInfo) {
        return R.ok(uniqueOrgService.list(uniqueOrgInfo));
    }

    /**
     * 查询全部机构（字典用途，带缓存）
     *
     * @return 全部机构列表
     */
    @GetMapping("/all")
    @Operation(summary = "查询全部机构", description = "查询全部统一机构（字典缓存）")
    public R<List<UniqueOrg>> all() {
        return R.ok(uniqueOrgService.getAllValidItems());
    }
    
    /**
     * 获取统一机构树
     *
     * @return 机构树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取统一机构树", description = "返回统一机构的树形结构")
    public R<List<UniqueOrgTreeVO>> getTree() {
        return R.ok(uniqueOrgService.getOrgTree());
    }

    /**
     * 新增统一机构
     */
    @SysLog("新增统一机构")
    @PostMapping
    @HasPermission("unique_org_add")
    @Operation(summary = "新增机构", description = "新增统一机构信息")
    public R<Boolean> save(@Valid @RequestBody UniqueOrgInfoDTO uniqueOrgInfo) {
        return R.ok(uniqueOrgService.save(uniqueOrgInfo));
    }

    /**
     * 批量新增统一机构
     */
    @SysLog("批量新增统一机构")
    @PostMapping("/batch")
    @HasPermission("unique_org_add")
    @Operation(summary = "批量新增机构", description = "批量新增统一机构信息")
    public R<Boolean> saveBatch(@Valid @RequestBody List<UniqueOrgInfoDTO> uniqueOrgInfos) {
        return R.ok(uniqueOrgService.saveBatch(uniqueOrgInfos));
    }

    /**
     * 修改统一机构
     */
    @SysLog("修改统一机构")
    @PutMapping
    @HasPermission("unique_org_edit")
    @Operation(summary = "修改机构", description = "修改统一机构信息")
    public R<Boolean> update(@Valid @RequestBody UniqueOrgInfoDTO uniqueOrgInfo) {
        return R.ok(uniqueOrgService.updateById(uniqueOrgInfo));
    }

    /**
     * 删除统一机构
     *
     * @param id 主键ID
     */
    @SysLog("删除统一机构")
    @DeleteMapping("/{id}")
    @HasPermission("unique_org_del")
    @Operation(summary = "删除机构", description = "根据ID删除统一机构")
    public R<Boolean> delete(@PathVariable String id) {
        return R.ok(uniqueOrgService.removeById(id));
    }

    /**
     * 刷新统一机构缓存
     */
    @SysLog("刷新统一机构缓存")
    @PostMapping("/refresh-cache")
    @HasPermission("unique_org_cache_refresh")
    @Operation(summary = "刷新缓存", description = "刷新统一机构缓存")
    public R<Void> refreshCache() {
        uniqueOrgService.refreshCache();
        return R.ok();
    }    
    
    /**
     * 根据ID查询机构（Feign）
     */
    @Inner
    @GetMapping("/getByIdForFeign/{id}")
    public Map<String, Object> getByIdForFeign(@PathVariable String id) {
    	UniqueOrgInfoVO org = uniqueOrgService.getById(id);
        if (org == null) {
            return new HashMap<>();
        }
        return BeanUtil.beanToMap(org);
    }

    /**
     * 根据ID集合查询机构列表（Feign）
     */
    @Inner
    @PostMapping("/listByIdsForFeign")
    public List<Map<String, Object>> listByIdsForFeign(
            @RequestBody List<String> orgIds) {

        List<UniqueOrg> list = uniqueOrgService.listByIds(orgIds);

        List<Map<String, Object>> result = list.stream()
                .map(BeanUtil::beanToMap)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 根据机构查询用户（Feign 调用）
     */
    @Inner
    @PostMapping("/listUsersByOrgForFeign")
    public List<Map<String, Object>> listUsersByOrgForFeign(
            @RequestBody Map<String, Object> query) {

        // 1. 从请求参数获取 orgId
        Object orgIdObj = query.get("orgId");
        if (orgIdObj == null) {
            return new ArrayList<>();
        }
        String orgId = orgIdObj.toString();
        UniqueOrgInfoVO org = uniqueOrgService.getById(orgId);

        // 2. 查询机构下的 UniqueUser 列表
        List<UniqueUser> users = uniqueUserService.list(
                Wrappers.<UniqueUser>lambdaQuery()
                        .eq(UniqueUser::getUniqueOrgCode, org.getOrgCode())
        );

        // 3. 转换成 List<Map<String, Object>>
        List<Map<String, Object>> result = users.stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("name", user.getName());
                    map.put("username", user.getLoginId());
                    map.put("phone", user.getMobile());
                    map.put("email", user.getEmail());
                    map.put("orgId", orgId);
                    map.put("uniqueOrgCode", user.getUniqueOrgCode());
                    
                    map.put("divisionCode", user.getDivisionCode());
                    map.put("uniqueRoles", user.getUniqueRoles());
                    
                    return map;
                })
                .toList();

        return result;
    }
}
