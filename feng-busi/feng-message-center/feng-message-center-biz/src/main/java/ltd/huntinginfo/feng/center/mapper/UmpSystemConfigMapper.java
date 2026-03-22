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
import ltd.huntinginfo.feng.center.api.entity.UmpSystemConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置表Mapper接口
 */
@Mapper
public interface UmpSystemConfigMapper extends BaseMapper<UmpSystemConfig> {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置实体
     */
    @Select("SELECT * FROM ump_system_config WHERE config_key = #{configKey} AND del_flag = '0'")
    UmpSystemConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 分页查询配置列表
     *
     * @param page       分页参数
     * @param configKey  配置键（可选）
     * @param configType 配置类型（可选）
     * @param category   配置类别（可选）
     * @param status     状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_config WHERE del_flag = '0' " +
            "<if test='configKey != null and configKey != \"\"'> AND config_key LIKE CONCAT('%', #{configKey}, '%') </if>" +
            "<if test='configType != null and configType != \"\"'> AND config_type = #{configType} </if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY category ASC, config_key ASC" +
            "</script>")
    IPage<UmpSystemConfig> selectConfigPage(IPage<UmpSystemConfig> page,
                                           @Param("configKey") String configKey,
                                           @Param("configType") String configType,
                                           @Param("category") String category,
                                           @Param("status") Integer status);

    /**
     * 根据类别查询配置列表
     *
     * @param category 配置类别
     * @param status   状态（可选）
     * @return 配置列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_config WHERE category = #{category} " +
            "<if test='status != null'> AND status = #{status} </if> AND del_flag = '0'" +
            " ORDER BY config_key ASC" +
            "</script>")
    List<UmpSystemConfig> selectByCategory(@Param("category") String category,
                                          @Param("status") Integer status);

    /**
     * 根据配置类型查询配置列表
     *
     * @param configType 配置类型
     * @param status     状态（可选）
     * @return 配置列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_config WHERE config_type = #{configType} " +
            "<if test='status != null'> AND status = #{status} </if> AND del_flag = '0'" +
            " ORDER BY category ASC, config_key ASC" +
            "</script>")
    List<UmpSystemConfig> selectByConfigType(@Param("configType") String configType,
                                            @Param("status") Integer status);

    /**
     * 更新配置值
     *
     * @param configKey   配置键
     * @param configValue 配置值
     * @return 更新条数
     */
    @Update("UPDATE ump_system_config SET config_value = #{configValue} WHERE config_key = #{configKey} AND del_flag = '0'")
    int updateConfigValue(@Param("configKey") String configKey,
                         @Param("configValue") String configValue);

    /**
     * 批量更新配置状态
     *
     * @param ids    配置ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_system_config SET status = #{status} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 获取配置统计信息
     *
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS enabled_count, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS disabled_count, " +
            "(SELECT JSON_OBJECTAGG(category, cnt) FROM (SELECT category, COUNT(*) AS cnt FROM ump_system_config WHERE del_flag = '0' GROUP BY category) t) AS category_stats, " +
            "(SELECT JSON_OBJECTAGG(config_type, cnt) FROM (SELECT config_type, COUNT(*) AS cnt FROM ump_system_config WHERE del_flag = '0' GROUP BY config_type) t) AS type_stats " +
            "FROM ump_system_config WHERE del_flag = '0'")
    Map<String, Object> selectConfigStatistics();

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) > 0 FROM ump_system_config WHERE config_key = #{configKey} AND del_flag = '0'")
    boolean existsByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置键列表查询配置
     *
     * @param configKeys 配置键列表
     * @param status     状态（可选）
     * @return 配置列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_system_config WHERE config_key IN " +
            "<foreach collection='configKeys' item='key' open='(' separator=',' close=')'>#{key}</foreach> " +
            "<if test='status != null'> AND status = #{status} </if> AND del_flag = '0'" +
            " ORDER BY category ASC, config_key ASC" +
            "</script>")
    List<UmpSystemConfig> selectByConfigKeys(@Param("configKeys") List<String> configKeys,
                                            @Param("status") Integer status);

    /**
     * 获取配置键值映射
     *
     * @param category 类别（可选）
     * @param status   状态（可选）
     * @return 配置键值映射
     */
    @Select("<script>" +
            "SELECT config_key, config_value FROM ump_system_config WHERE del_flag = '0' " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "</script>")
    @MapKey("config_key")
    Map<String, String> selectConfigMap(@Param("category") String category,
                                       @Param("status") Integer status);
}