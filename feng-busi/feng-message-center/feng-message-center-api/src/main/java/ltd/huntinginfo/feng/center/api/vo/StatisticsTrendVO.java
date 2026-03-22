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
@Schema(description = "统计趋势VO")
public class StatisticsTrendVO {
    
    @Schema(description = "时间区间")
    private String timePeriod;
    
    @Schema(description = "总发送数量")
    private Long totalSendCount;
    
    @Schema(description = "总发送成功数量")
    private Long totalSendSuccessCount;
    
    @Schema(description = "总发送失败数量")
    private Long totalSendFailedCount;
    
    @Schema(description = "总接收数量")
    private Long totalReceiveCount;
    
    @Schema(description = "总阅读数量")
    private Long totalReadCount;
    
    @Schema(description = "总错误数量")
    private Long totalErrorCount;
    
    @Schema(description = "总重试数量")
    private Long totalRetryCount;
    
    @Schema(description = "平均处理时间(ms)")
    private Double avgProcessTime;
    
    @Schema(description = "平均接收时间(ms)")
    private Double avgReceiveTime;
    
    @Schema(description = "平均阅读时间(ms)")
    private Double avgReadTime;
    
    @Schema(description = "发送成功率")
    private Double sendSuccessRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
    
    @Schema(description = "错误率")
    private Double errorRate;
}