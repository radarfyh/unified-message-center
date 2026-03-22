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
@Schema(description = "应用统计VO")
public class AppStatisticsVO {
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "请求次数")
    private Long requestCount;
    
    @Schema(description = "成功次数")
    private Long successCount;
    
    @Schema(description = "错误次数")
    private Long errorCount;
    
    @Schema(description = "平均耗时(ms)")
    private Double avgCostTime;
    
    @Schema(description = "最后请求时间")
    private String lastRequestTime;
    
    @Schema(description = "频繁API")
    private String frequentApi;
    
    @Schema(description = "成功率")
    private Double successRate;
}