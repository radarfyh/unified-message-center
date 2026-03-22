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
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用认证凭证表Mapper接口
 */
@Mapper
public interface UmpAppCredentialMapper extends BaseMapper<UmpAppCredential> {

    /**
     * 根据应用标识查询应用凭证
     *
     * @param appKey 应用标识
     * @return 应用凭证实体
     */
    @Select("SELECT * FROM ump_app_credential WHERE app_key = #{appKey} AND del_flag = '0'")
    UmpAppCredential selectByAppKey(@Param("appKey") String appKey);

    /**
     * 分页查询应用凭证列表
     *
     * @param page    分页参数
     * @param appName 应用名称（可选）
     * @param appType 应用类型（可选）
     * @param status  状态（可选）
     * @param appKey  应用标识（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_app_credential WHERE del_flag = '0'" +
            "<if test='appName != null and appName != \"\"'> AND app_name LIKE CONCAT('%', #{appName}, '%')</if>" +
            "<if test='appType != null and appType != \"\"'> AND app_type = #{appType}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='appKey != null and appKey != \"\"'> AND app_key = #{appKey}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpAppCredential> selectAppPage(IPage<UmpAppCredential> page,
                                          @Param("appName") String appName,
                                          @Param("appType") String appType,
                                          @Param("status") Integer status,
                                          @Param("appKey") String appKey);

    /**
     * 根据状态查询应用凭证列表
     *
     * @param status 状态
     * @return 应用凭证列表
     */
    @Select("SELECT * FROM ump_app_credential WHERE status = #{status} AND del_flag = '0'")
    List<UmpAppCredential> selectByStatus(@Param("status") Integer status);

    /**
     * 查询可用的应用凭证列表（状态为启用且密钥未过期）
     *
     * @return 可用的应用凭证列表
     */
    @Select("SELECT * FROM ump_app_credential WHERE status = 1 AND del_flag = '0' AND (secret_expire_time IS NULL OR secret_expire_time > NOW())")
    List<UmpAppCredential> selectAvailableApps();

    /**
     * 批量更新应用状态
     *
     * @param ids    应用ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_app_credential SET status = #{status} WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                          @Param("status") Integer status);

    /**
     * 更新应用密钥
     *
     * @param id                应用ID
     * @param appSecret         新的应用密钥
     * @param secretExpireTime  密钥过期时间
     * @return 更新条数
     */
    @Update("UPDATE ump_app_credential SET app_secret = #{appSecret}, secret_expire_time = #{secretExpireTime} WHERE id = #{id} AND del_flag = '0'")
    int updateAppSecret(@Param("id") String id,
                        @Param("appSecret") String appSecret,
                        @Param("secretExpireTime") LocalDateTime secretExpireTime);
    
    /**
     * 更新应用TOKEN
     *
     * @param appKey                应用Key
     * @param appToken         		应用token
     * @param appTokenExpireTime  	app token密钥过期时间
     * @return 更新条数
     */
    @Update("UPDATE ump_app_credential SET app_token = #{appToken}, app_token_expire_time = #{appTokenExpireTime} WHERE app_key = #{appKey} AND del_flag = '0'")
    int updateAppToken(@Param("appKey") String appKey,
                        @Param("appToken") String appToken,
                        @Param("appTokenExpireTime") LocalDateTime appTokenExpireTime);

    /**
     * 获取应用统计信息（总数、各类型数量、各状态数量）
     *
     * @return 统计结果
     */
    @Select("SELECT " +
            "(SELECT COUNT(*) FROM ump_app_credential WHERE del_flag = '0') AS total_count, " +
            "(SELECT COUNT(*) FROM ump_app_credential WHERE app_type = 'DIRECT' AND del_flag = '0') AS direct_count, " +
            "(SELECT COUNT(*) FROM ump_app_credential WHERE app_type = 'AGENT' AND del_flag = '0') AS agent_count, " +
            "(SELECT COUNT(*) FROM ump_app_credential WHERE status = 1 AND del_flag = '0') AS enabled_count, " +
            "(SELECT COUNT(*) FROM ump_app_credential WHERE status = 0 AND del_flag = '0') AS disabled_count")
    Map<String, Object> selectAppStatistics();

    /**
     * 检查应用标识是否存在
     *
     * @param appKey 应用标识
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) FROM ump_app_credential WHERE app_key = #{appKey} AND del_flag = '0'")
    boolean existsByAppKey(@Param("appKey") String appKey);
    
    /**
     * 根据应用类型分组统计数量
     * 
     * @return 分组统计结果
     */
    @Select("SELECT app_type, COUNT(*) as count FROM ump_app_credential WHERE del_flag = '0' GROUP BY app_type")
    List<Map<String, Object>> selectAppTypeCount();
}