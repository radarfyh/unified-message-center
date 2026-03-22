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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "系统配置统计VO")
public class SystemConfigStatisticsVO {
    
    @Schema(description = "总配置数")
    private Long totalCount;
    
    @Schema(description = "已启用配置数")
    private Long enabledCount;
    
    @Schema(description = "已禁用配置数")
    private Long disabledCount;
    
    @Schema(description = "分类统计")
    private Map<String, Long> categoryStats;
    
    @Schema(description = "类型统计")
    private Map<String, Long> typeStats;
    
    @Schema(description = "启用率")
    private Double enableRate;
}