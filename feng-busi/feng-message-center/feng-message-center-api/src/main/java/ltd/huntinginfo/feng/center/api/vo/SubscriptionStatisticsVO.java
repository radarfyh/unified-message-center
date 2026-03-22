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
@Schema(description = "订阅统计VO")
public class SubscriptionStatisticsVO {
    
    @Schema(description = "总订阅数")
    private Long totalCount;
    
    @Schema(description = "活跃订阅数")
    private Long activeCount;
    
    @Schema(description = "非活跃订阅数")
    private Long inactiveCount;
    
    @Schema(description = "推送模式订阅数")
    private Long pushModeCount;
    
    @Schema(description = "轮询模式订阅数")
    private Long pollModeCount;
    
    @Schema(description = "总消息数")
    private Long totalMessages;
    
    @Schema(description = "平均消息数")
    private Double avgMessages;
    
    @Schema(description = "活跃率")
    private Double activeRate;
}