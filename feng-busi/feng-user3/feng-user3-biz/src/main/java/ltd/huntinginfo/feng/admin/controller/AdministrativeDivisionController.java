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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import ltd.huntinginfo.feng.admin.api.entity.dict.DictAdministrativeDivision;
import ltd.huntinginfo.feng.admin.service.dict.DictAdministrativeDivisionService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.security.annotation.Inner;

import org.springframework.web.bind.annotation.*;

import cn.hutool.core.bean.BeanUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行政区划代码 Controller
 * @author Edison.Feng
 * @date 2025/12/30
 */
@RestController
@RequestMapping("/dict/administrative-division")
@Tag(name = "行政区划管理", description = "行政区划字典相关接口")
@RequiredArgsConstructor
public class AdministrativeDivisionController {

    private final DictAdministrativeDivisionService administrativeDivisionService;

    /**
     * 根据行政区划 code 查询
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "根据 code 查询行政区划")
    public R<DictAdministrativeDivision> getByCode(@PathVariable String code) {
        return R.ok(administrativeDivisionService.getByCode(code));
    }

    /**
     * 根据父级 code 查询下级行政区划
     */
    @GetMapping("/parent/{parentCode}")
    @Operation(summary = "根据父级 code 查询下级行政区划")
    public R<List<DictAdministrativeDivision>> getByParentCode(
            @PathVariable String parentCode) {
        return R.ok(administrativeDivisionService.getByParentCode(parentCode));
    }

    /**
     * 根据行政级别查询
     * level: 1-省 2-市 3-县
     */
    @GetMapping("/level/{level}")
    @Operation(summary = "按行政级别查询行政区划")
    public R<List<DictAdministrativeDivision>> getByLevel(
            @PathVariable Integer level) {
        return R.ok(administrativeDivisionService.getByLevel(level));
    }

    /**
     * 查询所有行政区划
     */
    @GetMapping("/all")
    @Operation(summary = "查询全部行政区划")
    public R<List<DictAdministrativeDivision>> getAll() {
        return R.ok(administrativeDivisionService.getAllValidItems());
    }
    
    /**
     * 获取行政区划树（省-市-县）
     */
    @GetMapping("/tree")
    @Operation(summary = "获取行政区划树结构")
    public R<List<DictAdministrativeDivision>> getDivisionTree() {
        return R.ok(administrativeDivisionService.getDivisionTree());
    }
    
    /**
     * 根据区域编码获取行政区划信息
     */
    @Inner
    @GetMapping("/{areaCode}")
    public Map<String, Object> getAreaByCode(@PathVariable String areaCode) {
    	DictAdministrativeDivision area = administrativeDivisionService.getByCode(areaCode);
        if (area == null) {
            return new HashMap<>();
        }
        return BeanUtil.beanToMap(area);
    }
    
    /**
     * 根据多个区域编码批量查询行政区划信息
     */
    @Inner
    @PostMapping("/list-by-codes")
    public List<Map<String, Object>> listAreasByCodes(@RequestBody List<String> areaCodes) {
    	return administrativeDivisionService.listAreasByCodes(areaCodes);
    }
    
    /**
     * 根据区域查询下属用户信息
     */
    @Inner
    @PostMapping("/users")
    public List<Map<String, Object>> listUsersByArea(@RequestBody Map<String, Object> query) {
        return administrativeDivisionService.listUsersByArea(query);
    }

    /**
     * 手动刷新行政区划缓存
     */
    @PostMapping("/cache/refresh")
    @Operation(summary = "刷新行政区划缓存")
    public R<Void> refreshCache() {
        administrativeDivisionService.refreshCache();
        return R.ok();
    }
}
