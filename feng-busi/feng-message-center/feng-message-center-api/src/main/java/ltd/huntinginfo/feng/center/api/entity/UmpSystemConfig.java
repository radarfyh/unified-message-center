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
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

/**
 * 系统配置表实体类
 * 对应表：ump_system_config
 * 作用：管理系统运行参数，支持动态配置和热更新
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("ump_system_config")
@Schema(description = "系统配置表实体")
public class UmpSystemConfig extends BaseEntity<UmpSystemConfig> {
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "配置键")
    @TableField("config_key")
    private String configKey;

    @Schema(description = "配置值")
    @TableField("config_value")
    private String configValue;

    @Schema(description = "配置类型:STRING/NUMBER/BOOLEAN/JSON")
    @TableField("config_type")
    private String configType;

    @Schema(description = "配置描述")
    @TableField("config_desc")
    private String configDesc;

    @Schema(description = "配置类别")
    @TableField("category")
    private String category;

    @Schema(description = "状态:0-禁用 1-启用")
    @TableField("status")
    private Integer status;
}