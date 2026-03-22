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
@Schema(description = "实时统计概览VO")
public class RealTimeStatisticsOverviewVO {
    
    @Schema(description = "今日发送数量")
    private Long todaySendCount;
    
    @Schema(description = "今日接收数量")
    private Long todayReceiveCount;
    
    @Schema(description = "今日阅读数量")
    private Long todayReadCount;
    
    @Schema(description = "今日错误数量")
    private Long todayErrorCount;
    
    @Schema(description = "昨日发送数量")
    private Long yesterdaySendCount;
    
    @Schema(description = "昨日接收数量")
    private Long yesterdayReceiveCount;
    
    @Schema(description = "昨日阅读数量")
    private Long yesterdayReadCount;
    
    @Schema(description = "昨日错误数量")
    private Long yesterdayErrorCount;
    
    @Schema(description = "本周发送数量")
    private Long weekSendCount;
    
    @Schema(description = "本周接收数量")
    private Long weekReceiveCount;
    
    @Schema(description = "本周阅读数量")
    private Long weekReadCount;
    
    @Schema(description = "本周错误数量")
    private Long weekErrorCount;
    
    @Schema(description = "本月发送数量")
    private Long monthSendCount;
    
    @Schema(description = "本月接收数量")
    private Long monthReceiveCount;
    
    @Schema(description = "本月阅读数量")
    private Long monthReadCount;
    
    @Schema(description = "本月错误数量")
    private Long monthErrorCount;
    
    @Schema(description = "发送增长率")
    private Double sendGrowthRate;
    
    @Schema(description = "接收增长率")
    private Double receiveGrowthRate;
    
    @Schema(description = "阅读增长率")
    private Double readGrowthRate;
    
    @Schema(description = "错误增长率")
    private Double errorGrowthRate;
    
    @Schema(description = "在线用户数")
    private Long onlineUserCount;
    
    @Schema(description = "消息队列大小")
    private Long queueSize;
}