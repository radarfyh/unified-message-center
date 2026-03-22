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
 * 消息状态码表实体类
 * 对应表：ump_status_code
 * 作用：统一管理系统中所有状态码，便于状态的管理和国际化
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("ump_status_code")
@Schema(description = "消息状态码表实体")
public class UmpStatusCode extends BaseEntity<UmpStatusCode> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一标识UUID")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "状态码")
    private String statusCode;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "分类:MESSAGE-消息 CALLBACK-回调 QUEUE-队列")
    private String category;

    @Schema(description = "父状态码")
    private String parentCode;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "是否为最终状态:0-否 1-是")
    private Integer isFinal;

    @Schema(description = "是否可重试:0-否 1-是")
    private Integer canRetry;

    @Schema(description = "状态:0-禁用 1-启用")
    private Integer status;
}