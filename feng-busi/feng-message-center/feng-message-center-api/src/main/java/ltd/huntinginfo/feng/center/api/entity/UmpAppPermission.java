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
package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

/**
 * 应用权限表实体类
 * 对应表：ump_app_permission
 * 作用：管理应用的API访问权限，实现细粒度的权限控制
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "ump_app_permission")
@Schema(description = "应用权限表实体")
public class UmpAppPermission extends BaseEntity<UmpAppPermission> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "权限类型: API-接口权限, RECEIVE-接收范围")
    private String type;

    @Schema(description = "资源标识符")
    private String resourceCode;

    @Schema(description = "资源描述")
    private String resourceName;
    
    @Schema(description = "接收者类型:USER-个人(receiver_scope.include.loginIds) DEPT-部门(receiver_scope.include.deptIds) CUSTOM-自定义 ALL-全体(receiver_scope)")
    private String receiverType;

    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
}