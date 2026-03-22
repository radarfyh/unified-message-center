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
package ltd.huntinginfo.feng.admin.api.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 统一用户信息视图对象
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@Schema(name = "统一用户信息VO", 
       description = "统一用户信息视图对象，包含用户基础信息和关联信息")
public class UniqueUserInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", 
           example = "1")
    private String id;

    @Schema(description = "应用代码")
    private String appKey;
   
    @Schema(description = "统一登录名",  example = "admin")
    private String loginId;

    @Schema(description = "用户昵称", 
           example = "系统管理员")
    private String nickname;
    
    @Schema(description = "用户姓名", 
            example = "系统管理员")
    private String name;

    @Schema(description = "用户类型(0-个人用户 1-单位用户 2-其他)", 
           example = "1")
    private String type;
    
    @Schema(description = "用户拥有的公共角色以及在当前应用下拥有的角色", 
            example = "app_admin,common")
    private String uniqueRoles;

    @Schema(description = "身份证号", 
           example = "330102199001011234")
    private String idCard;

    @Schema(description = "省份编号", 
           example = "33")
    private String proId;
    
    @Schema(description = "用户所属省份代码", example = "FJ")
    private String proCode;

    @Schema(description = "省份名称", 
           example = "浙江省")
    private String proName;

    @Schema(description = "城市编号", 
           example = "3301")
    private String cityId;
    
    @Schema(description = "用户所属城市代码", example = "QZ")
    private String cityCode;

    @Schema(description = "城市名称", 
           example = "杭州市")
    private String cityName;

    @Schema(description = "区域编号", 
           example = "330106")
    private String regId;
    
    @Schema(description = "用户所属区域代码", example = "BA")
    private String regCode;

    @Schema(description = "区域名称", 
           example = "西湖区")
    private String regName;
    
    @Schema(description = "用户所属单位代码", example = "ZXJD")
    private String unitNo;

    @Schema(description = "机构编号", 
           example = "ORG001")
    private String orgId;
    
    @Schema(description = "用户所属机构代码", example = "FZ313")
    private String orgCode;

    @Schema(description = "机构名称", 
           example = "XX消防支队")
    private String orgName;

    @Schema(description = "用户邮箱", 
           example = "user@example.com")
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
    
    @Schema(description = "响应参数-访问令牌")
    private String accessToken;
     
    @Schema(description = "响应参数-刷新令牌")
    private String refreshToken;
    
    @Schema(description = "过期时间", 
            example = "2023-08-15T10:00:00")
    private Date expiresTime;

    @Schema(description = "最后登录时间", 
           example = "2023-08-15T10:00:00")
    private Date loginDate;
}

