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

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 统一用户信息表实体类
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@TableName("unique_user")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@Schema(name = "统一用户信息", 
       description = "统一用户信息实体，包含用户基础信息和关联信息，用于对接统一认证系统")
public class UniqueUser extends Model<UniqueUser> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "主键UUID", 
           example = "1",
           requiredMode = RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用代码")
    private String appKey;
    
    @Schema(description = "用户账号，统一认证系统中的账号", 
           example = "admin",
           requiredMode = RequiredMode.REQUIRED)
    private String loginId;
    
    @Schema(description = "用户记录ID，统一消息中心账号的ID，开通后台账户时回写", 
            example = "1")
    private String sysUserId;

    @Schema(description = "用户昵称", 
           example = "系统管理员")
    private String nickname;
    
    @Schema(description = "用户姓名", 
            example = "系统管理员")
    private String name;

    @Schema(description = "用户类型(0-个人用户 1-单位用户 2-其他)", 
           example = "1",
           requiredMode = RequiredMode.REQUIRED)
    private String type;
    
    @Schema(description = "统一认证系统用户拥有的公共角色以及在当前应用下拥有的角色，多个角色逗号隔开", 
            example = "app_admin,common")
    private String uniqueRoles;
    
    @Schema(description = "统一认证系统用户角色列表")
    @TableField(exist = false)
    private List<UniqueRole> uniqueRoleLists;
    
    @Schema(description = "用户角色记录ID，统一消息中心角色的ID，开通后台账户时回写", 
            example = "1")
    private String sysRoleId;

    @Schema(description = "身份证号", 
           example = "330102199001011234")
    private String idCard;

    @Schema(description = "所属区域代码", 
            example = "100000")
    private String divisionCode;

    @Schema(description = "所属区域信息", 
           example = "西湖区")
    @TableField(exist = false)
    private DictAdministrativeDivision uniqueDivision;
    
    @Schema(description = "所属单位代码", 
            example = "100000")
    private String agencyCode;

    @Schema(description = "所属单位单位信息", 
           example = "西湖区机关")
    @TableField(exist = false)
    private GovAgency govAgency;

    @Schema(description = "所属机构代码", 
            example = "100000")
    private String uniqueOrgCode;
    
    @Schema(description = "所属机构信息", 
           example = "XX消防大队")
    @TableField(exist = false)
    private UniqueOrg uniqueOrg;

    @Schema(description = "用户邮箱", 
           example = "user@test.com")
    private String email;

    @Schema(description = "手机号码", 
           example = "13800138000")
    private String mobile;

    @Schema(description = "用户性别(0-未知 1-男 2-女)", 
           example = "1")
    private Integer sex;

    @Schema(description = "用户头像", 
           example = "/avatar/default.jpg")
    private String avatar;

    @Schema(description = "最后登录IP",  example = "192.168.1.1")
    private String loginIp;
    
    @Schema(description = "访问令牌")
    private String accessToken;
     
    @Schema(description = "刷新令牌")
    private String refreshToken;
    
    @Schema(description = "过期时间", 
            example = "2023-08-15T10:00:00")
    private Date expiresTime;

    @Schema(description = "最后登录时间", 
           example = "2023-08-15T10:00:00")
    private Date loginDate;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建者", 
           example = "admin")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", 
           example = "2023-08-15T10:00:00")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新者", 
           example = "admin")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "更新时间", 
           example = "2023-08-15T10:00:00")
    private Date updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标记（0-正常 1-删除）", 
           example = "0",
           allowableValues = {"0", "1"})
    private String delFlag;
}
