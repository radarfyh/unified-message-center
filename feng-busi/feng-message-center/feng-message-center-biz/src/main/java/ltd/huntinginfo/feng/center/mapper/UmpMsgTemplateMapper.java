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
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 消息模板表Mapper接口
 */
@Mapper
public interface UmpMsgTemplateMapper extends BaseMapper<UmpMsgTemplate> {

    /**
     * 根据模板代码查询模板
     *
     * @param templateCode 模板代码
     * @return 模板实体
     */
    @Select("SELECT * FROM ump_msg_template WHERE template_code = #{templateCode} AND del_flag = '0'")
    UmpMsgTemplate selectByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 分页查询模板列表
     *
     * @param page         分页参数
     * @param templateName 模板名称（可选）
     * @param templateType 模板类型（可选）
     * @param templateCode 模板代码（可选）
     * @param status       状态（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_template WHERE del_flag = '0' " +
            "<if test='templateName != null and templateName != \"\"'> AND template_name LIKE CONCAT('%', #{templateName}, '%') </if>" +
            "<if test='templateType != null and templateType != \"\"'> AND template_type = #{templateType} </if>" +
            "<if test='templateCode != null and templateCode != \"\"'> AND template_code = #{templateCode} </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    IPage<UmpMsgTemplate> selectTemplatePage(IPage<UmpMsgTemplate> page,
                                            @Param("templateName") String templateName,
                                            @Param("templateType") String templateType,
                                            @Param("templateCode") String templateCode,
                                            @Param("status") Integer status);

    /**
     * 根据模板类型查询模板列表
     *
     * @param templateType 模板类型
     * @param status       状态（可选）
     * @return 模板列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_template WHERE template_type = #{templateType} AND del_flag = '0' " +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<UmpMsgTemplate> selectByTemplateType(@Param("templateType") String templateType,
                                             @Param("status") Integer status);

    /**
     * 查询所有启用的模板
     *
     * @return 启用的模板列表
     */
    @Select("SELECT * FROM ump_msg_template WHERE status = 1 AND del_flag = '0' ORDER BY create_time DESC")
    List<UmpMsgTemplate> selectAllEnabled();

    /**
     * 批量更新模板状态
     *
     * @param ids    模板ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_template SET status = #{status} " +
            "WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "AND del_flag = '0'" +
            "</script>")
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 更新模板信息
     *
     * @param id                  模板ID
     * @param templateName        模板名称
     * @param titleTemplate       标题模板
     * @param contentTemplate     内容模板
     * @param variables           模板变量
     * @param defaultPriority     默认优先级
     * @param defaultPushMode     默认推送方式
     * @param defaultCallbackUrl  默认回调地址
     * @param status              状态
     * @return 更新条数
     */
    @Update("<script>" +
            "UPDATE ump_msg_template " +
            "<set>" +
            "<if test='templateName != null'>template_name = #{templateName}, </if>" +
            "<if test='titleTemplate != null'>title_template = #{titleTemplate}, </if>" +
            "<if test='contentTemplate != null'>content_template = #{contentTemplate}, </if>" +
            "<if test='variables != null'>variables = #{variables, jdbcType=OTHER, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}, </if>" +
            "<if test='defaultPriority != null'>default_priority = #{defaultPriority}, </if>" +
            "<if test='defaultPushMode != null'>default_push_mode = #{defaultPushMode}, </if>" +
            "<if test='defaultCallbackUrl != null'>default_callback_url = #{defaultCallbackUrl}, </if>" +
            "<if test='status != null'>status = #{status}, </if>" +
            "</set>" +
            "WHERE id = #{id} AND del_flag = '0'" +
            "</script>")
    int updateTemplate(@Param("id") String id,
                      @Param("templateName") String templateName,
                      @Param("titleTemplate") String titleTemplate,
                      @Param("contentTemplate") String contentTemplate,
                      @Param("variables") Map<String, Object> variables,
                      @Param("defaultPriority") Integer defaultPriority,
                      @Param("defaultPushMode") String defaultPushMode,
                      @Param("defaultCallbackUrl") String defaultCallbackUrl,
                      @Param("status") Integer status);

    /**
     * 获取模板统计信息
     *
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) AS total_count, " +
            "SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS enabled_count, " +
            "SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS disabled_count, " +
            "JSON_OBJECTAGG(template_type, cnt) AS type_stats " +
            "FROM ( " +
            "  SELECT template_type, COUNT(*) AS cnt " +
            "  FROM ump_msg_template " +
            "  WHERE del_flag = '0' " +
            "  GROUP BY template_type " +
            ") AS stats")
    Map<String, Object> selectTemplateStatistics();

    /**
     * 检查模板代码是否存在
     *
     * @param templateCode 模板代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) > 0 FROM ump_msg_template WHERE template_code = #{templateCode} AND del_flag = '0'")
    boolean existsByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 根据模板代码列表查询模板
     *
     * @param templateCodes 模板代码列表
     * @param status        状态（可选）
     * @return 模板列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_template WHERE template_code IN " +
            "<foreach collection='templateCodes' item='code' open='(' separator=',' close=')'>#{code}</foreach> " +
            "AND del_flag = '0' " +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<UmpMsgTemplate> selectByTemplateCodes(@Param("templateCodes") List<String> templateCodes,
                                              @Param("status") Integer status);

    /**
     * 根据关键词搜索模板
     *
     * @param keyword 关键词
     * @param status  状态（可选）
     * @return 模板列表
     */
    @Select("<script>" +
            "SELECT * FROM ump_msg_template WHERE del_flag = '0' " +
            "AND (template_code LIKE CONCAT('%', #{keyword}, '%') " +
            "     OR template_name LIKE CONCAT('%', #{keyword}, '%') " +
            "     OR template_type LIKE CONCAT('%', #{keyword}, '%')) " +
            "<if test='status != null'> AND status = #{status} </if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<UmpMsgTemplate> searchTemplates(@Param("keyword") String keyword,
                                        @Param("status") Integer status);

}