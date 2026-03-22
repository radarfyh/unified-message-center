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
/*
 * 用于ump_system_log日志服务
 */
@Data
@Schema(description = "性能统计VO")
public class LogPerformanceStatisticsVO {
    
    @Schema(description = "总请求数")
    private Long totalRequests;
    
    @Schema(description = "平均耗时(ms)")
    private Double avgCostTime;
    
    @Schema(description = "最大耗时(ms)")
    private Integer maxCostTime;
    
    @Schema(description = "最小耗时(ms)")
    private Integer minCostTime;
    
    @Schema(description = "P95耗时(ms)")
    private Integer p95CostTime;
    
    @Schema(description = "P99耗时(ms)")
    private Integer p99CostTime;
    
    @Schema(description = "平均内存使用(KB)")
    private Double avgMemoryUsage;
    
    @Schema(description = "最大内存使用(KB)")
    private Integer maxMemoryUsage;
    
    @Schema(description = "慢请求数量")
    private Integer slowRequests;
    
    @Schema(description = "慢请求比例")
    private Double slowRequestRate;
}