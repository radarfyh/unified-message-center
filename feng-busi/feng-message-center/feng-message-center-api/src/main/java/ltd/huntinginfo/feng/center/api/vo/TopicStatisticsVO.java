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

@Data
@Schema(description = "主题统计VO")
public class TopicStatisticsVO {
    
    @Schema(description = "总主题数")
    private Long totalCount;
    
    @Schema(description = "系统主题数")
    private Long systemCount;
    
    @Schema(description = "自定义主题数")
    private Long customCount;
    
    @Schema(description = "已启用主题数")
    private Long enabledCount;
    
    @Schema(description = "已禁用主题数")
    private Long disabledCount;
    
    @Schema(description = "总订阅者数")
    private Long totalSubscribers;
    
    @Schema(description = "平均订阅者数")
    private Double avgSubscribers;
    
    @Schema(description = "启用率")
    private Double enableRate;
}