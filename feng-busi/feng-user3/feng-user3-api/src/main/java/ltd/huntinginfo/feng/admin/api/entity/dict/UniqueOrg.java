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
package ltd.huntinginfo.feng.admin.api.entity.dict;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 统一机构信息表实体类 （民间组织，企业机构）
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@TableName("unique_org")
@EqualsAndHashCode(callSuper = true)
@Schema(name = "机构信息", 
       description = "机构信息实体，包含机构基础信息和关联信息，用于对接统一认证系统")
public class UniqueOrg extends Model<UniqueOrg> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "主键UUID", example = "1", requiredMode = RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "机构编号", example = "JY330100", requiredMode = RequiredMode.REQUIRED)
    private String orgId;

    @Schema(description = "机构名称",  example = "XX救援大队", requiredMode = RequiredMode.REQUIRED)
    private String orgName;

    @Schema(description = "机构代码",  example = "JY330100000000", requiredMode = RequiredMode.REQUIRED)
    private String orgCode;

    @Schema(description = "简称", example = "滨海救援")
    private String sname;

    @Schema(description = "全称",  example = "滨海新区救援大队")
    private String fname;
    
    @Schema(description = "行政区划代码",  example = "330100")
    private String divisionCode;

    @Schema(description = "所属区域信息",  example = "天津市滨海新区")
    @TableField(exist = false)
    private DictAdministrativeDivision uniqueDivision;

    @Schema(description = "上级机构编号",  example = "330100000000")
    private String parentId;

    @Schema(description = "地址",  example = "天津市滨海新区文三路359号")
    private String address;

    @Schema(description = "办公电话", example = "022-87000000")
    private String officeTel;

    @Schema(description = "电子邮件", example = "office@test.gov.cn")
    private String email;

    @Schema(description = "排序ID", example = "1")
    private String orderId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", example = "2023-08-15T10:00:00")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新者", example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", example = "2023-08-15T10:00:00")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标记（0-正常 1-删除）", example = "0", allowableValues = {"0", "1"})
    private String delFlag;
}
