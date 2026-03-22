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
package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 应用权限表Mapper接口
 */
@Mapper
public interface UmpAppPermissionMapper extends BaseMapper<UmpAppPermission> {

    /**
     * 根据应用标识和资源代码查询权限（仅限API类型）
     *
     * @param appKey       应用标识
     * @param resourceCode 资源代码
     * @return 权限实体
     */
    @Select("SELECT * FROM ump_app_permission WHERE app_key = #{appKey} AND resource_code = #{resourceCode} " +
            "AND type = 'API' AND del_flag = '0'")
    UmpAppPermission selectByAppKeyAndResourceCode(@Param("appKey") String appKey,
                                                   @Param("resourceCode") String resourceCode);

    /**
     * 分页查询应用权限列表（支持动态条件）
     *
     * @param page         分页参数
     * @param appKey       应用标识（可选）
     * @param resourceCode 资源代码（可选）
     * @param resourceName 资源名称（可选）
     * @param status       状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_app_permission WHERE del_flag = '0' " +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey} </if>" +
            "<if test='resourceCode != null and resourceCode != \"\"'> AND resource_code LIKE CONCAT('%', #{resourceCode}, '%') </if>" +
            "<if test='resourceName != null and resourceName != \"\"'> AND resource_name LIKE CONCAT('%', #{resourceName}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpAppPermission> selectPermissionPage(IPage<UmpAppPermission> page,
                                                @Param("appKey") String appKey,
                                                @Param("resourceCode") String resourceCode,
                                                @Param("resourceName") String resourceName,
                                                @Param("status") Integer status);

    /**
     * 根据应用标识查询权限列表（含@Select注解，已存在）
     */
    @Select("SELECT * FROM ump_app_permission WHERE app_key = #{appKey} AND status = 1 AND del_flag = '0'")
    List<UmpAppPermission> selectByAppKey(@Param("appKey") String appKey);

    /**
     * 查询可用的应用权限列表（状态为启用且未删除）
     *
     * @param appKey 应用标识
     * @return 可用的权限列表
     */
    @Select("SELECT * FROM ump_app_permission WHERE app_key = #{appKey} AND status = 1 AND del_flag = '0'")
    List<UmpAppPermission> selectAvailablePermissions(@Param("appKey") String appKey);

    /**
     * 批量更新权限状态
     *
     * @param ids    权限ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_app_permission SET status = #{status} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                          @Param("status") Integer status);

    /**
     * 获取应用权限统计信息
     *
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS enabled_count, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS disabled_count " +
            "FROM ump_app_permission WHERE del_flag = '0'")
    Map<String, Object> selectPermissionStatistics();

    /**
     * 检查权限是否存在（仅限API类型）
     *
     * @param appKey       应用标识
     * @param resourceCode 资源代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) FROM ump_app_permission WHERE app_key = #{appKey} AND resource_code = #{resourceCode} " +
            "AND type = 'API' AND del_flag = '0'")
    boolean existsByAppKeyAndResourceCode(@Param("appKey") String appKey,
                                          @Param("resourceCode") String resourceCode);

    /**
     * 获取应用所有资源代码（仅限API类型）
     *
     * @param appKey 应用标识
     * @return 资源代码列表
     */
    @Select("SELECT resource_code FROM ump_app_permission WHERE app_key = #{appKey} AND type = 'API' " +
            "AND del_flag = '0'")
    List<String> selectResourceCodesByAppKey(@Param("appKey") String appKey);
}