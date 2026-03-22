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
import ltd.huntinginfo.feng.center.api.entity.UmpStatusCode;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 消息状态码表Mapper接口
 */
@Mapper
public interface UmpStatusCodeMapper extends BaseMapper<UmpStatusCode> {

    /**
     * 根据状态码查询
     *
     * @param statusCode 状态码
     * @return 状态码实体
     */
    @Select("SELECT * FROM ump_status_code WHERE status_code = #{statusCode} AND del_flag = '0'")
    UmpStatusCode selectByStatusCode(@Param("statusCode") String statusCode);

    /**
     * 分页查询状态码列表
     *
     * @param page       分页参数
     * @param statusCode 状态码（可选）
     * @param statusName 状态名称（可选）
     * @param category   分类（可选）
     * @param status     状态（可选）
     * @param parentCode 父状态码（可选）
     * @param isFinal    是否为最终状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_status_code WHERE 1=1 " +
            "<if test='statusCode != null and statusCode != \"\"'> AND status_code LIKE CONCAT('%', #{statusCode}, '%') </if>" +
            "<if test='statusName != null and statusName != \"\"'> AND status_name LIKE CONCAT('%', #{statusName}, '%') </if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "<if test='parentCode != null and parentCode != \"\"'> AND parent_code = #{parentCode} </if>" +
            "<if test='isFinal != null'> AND is_final = #{isFinal} </if> AND del_flag = '0'" +
            " ORDER BY category, sort_order, status_code" +
            "</script>")
    IPage<UmpStatusCode> selectStatusCodePage(IPage<UmpStatusCode> page,
                                             @Param("statusCode") String statusCode,
                                             @Param("statusName") String statusName,
                                             @Param("category") String category,
                                             @Param("status") Integer status,
                                             @Param("parentCode") String parentCode,
                                             @Param("isFinal") Integer isFinal);

    /**
     * 根据分类查询状态码列表
     *
     * @param category 分类
     * @param status   状态（可选）
     * @return 状态码列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_status_code WHERE category = #{category} " +
            "<if test='status != null'> AND status = #{status} </if> AND del_flag = '0'" +
            " ORDER BY sort_order, status_code" +
            "</script>")
    List<UmpStatusCode> selectByCategory(@Param("category") String category,
                                        @Param("status") Integer status);

    /**
     * 根据父状态码查询子状态码列表
     *
     * @param parentCode 父状态码
     * @param status     状态（可选）
     * @return 子状态码列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_status_code WHERE parent_code = #{parentCode} " +
            "<if test='status != null'> AND status = #{status} </if> AND del_flag = '0'" +
            " ORDER BY sort_order, status_code" +
            "</script>")
    List<UmpStatusCode> selectByParentCode(@Param("parentCode") String parentCode,
                                          @Param("status") Integer status);

    /**
     * 查询所有启用的状态码
     *
     * @return 启用的状态码列表
     */
    @Select("SELECT * FROM ump_status_code WHERE status = 1 AND del_flag = '0' ORDER BY category, sort_order, status_code")
    List<UmpStatusCode> selectAllEnabled();

    /**
     * 批量更新状态码状态
     *
     * @param ids    状态码ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_status_code SET status = #{status} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 更新状态码信息
     *
     * @param id         状态码ID
     * @param statusName 状态名称
     * @param statusDesc 状态描述
     * @param sortOrder  排序
     * @param isFinal    是否为最终状态
     * @param canRetry   是否可重试
     * @param status     状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_status_code " +
            "<set>" +
            "<if test='statusName != null'>status_name = #{statusName}, </if>" +
            "<if test='statusDesc != null'>status_desc = #{statusDesc}, </if>" +
            "<if test='sortOrder != null'>sort_order = #{sortOrder}, </if>" +
            "<if test='isFinal != null'>is_final = #{isFinal}, </if>" +
            "<if test='canRetry != null'>can_retry = #{canRetry}, </if>" +
            "<if test='status != null'>status = #{status}, </if>" +
            "</set>" +
            "WHERE id = #{id} AND del_flag = '0'" +
            "</script>")
    int updateStatusCode(@Param("id") String id,
                        @Param("statusName") String statusName,
                        @Param("statusDesc") String statusDesc,
                        @Param("sortOrder") Integer sortOrder,
                        @Param("isFinal") Integer isFinal,
                        @Param("canRetry") Integer canRetry,
                        @Param("status") Integer status);

    /**
     * 获取状态码统计信息
     *
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS enabled_count, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS disabled_count, " +
            "JSON_OBJECTAGG(category, cnt) AS category_stats, " +
            "SUM(CASE WHEN is_final = 1 THEN 1 ELSE 0 END) AS final_count, " +
            "SUM(CASE WHEN is_final = 0 THEN 1 ELSE 0 END) AS non_final_count, " +
            "SUM(CASE WHEN can_retry = 1 THEN 1 ELSE 0 END) AS retryable_count, " +
            "SUM(CASE WHEN can_retry = 0 THEN 1 ELSE 0 END) AS non_retryable_count " +
            "FROM ( " +
            "  SELECT category, COUNT(*) AS cnt " +
            "  FROM ump_status_code WHERE del_flag = '0' " +  // 修正了这里：将 AND 改为 WHERE
            "  GROUP BY category " +
            ") AS stats")
    Map<String, Object> selectStatusCodeStatistics();

    /**
     * 检查状态码是否存在
     *
     * @param statusCode 状态码
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) > 0 FROM ump_status_code WHERE status_code = #{statusCode} AND del_flag = '0'")
    boolean existsByStatusCode(@Param("statusCode") String statusCode);

    /**
     * 获取状态码层级树
     *
     * @param category 分类（可选）
     * @param status   状态（可选）
     * @return 状态码层级树
     */
    @Select("<script>" +
            "SELECT id, status_code, status_name, category, parent_code, sort_order, is_final, can_retry, status " +
            "FROM ump_status_code " +
            "WHERE del_flag = '0' " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY category, sort_order, status_code" +
            "</script>")
    List<Map<String, Object>> selectStatusCodeTree(@Param("category") String category,
                                                  @Param("status") Integer status);

    /**
     * 根据分类和状态获取状态码映射
     *
     * @param category 分类
     * @param status   状态
     * @return 状态码映射
     */
    @Select("<script>" +
            "SELECT status_code, status_name FROM ump_status_code " +
            "WHERE category = #{category} " +
            "<if test='status != null'> AND status = #{status} </if> AND del_flag = '0'" +
            " ORDER BY sort_order, status_code" +
            "</script>")
    @MapKey("status_code")
    Map<String, String> selectStatusCodeMap(@Param("category") String category,
                                           @Param("status") Integer status);
}