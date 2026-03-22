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
package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "状态码分页VO")
public class StatusCodePageVO {
    
    @Schema(description = "状态码ID")
    private String id;
    
    @Schema(description = "状态码")
    private String statusCode;
    
    @Schema(description = "状态名称")
    private String statusName;
    
    @Schema(description = "状态描述")
    private String statusDesc;
    
    @Schema(description = "分类")
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
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}