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
package ltd.huntinginfo.feng.admin.api.dto.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 统一角色信息数据传输对象
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@Schema(name = "统一角色信息DTO", 
       description = "统一角色信息数据传输对象，包含角色基础信息和状态信息")
public class UniqueRoleInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "统一角色编号", 
           example = "ROLE001")
    private String id;

    @Schema(description = "角色名称", 
           example = "系统管理员")
    private String name;

    @Schema(description = "角色代码", 
           example = "admin")
    private String code;

    @Schema(description = "显示顺序", 
           example = "1")
    private Integer sort;

    @Schema(description = "状态(0-禁用 1-启用)", 
           example = "1")
    private Integer status;

    @Schema(description = "角色类型(参见RoleTypeEnum枚举类)", 
           example = "1")
    private Integer type;

    @Schema(description = "备注", 
           example = "系统最高权限角色")
    private String remark;

}


