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
@Schema(description = "广播统计VO")
public class BroadcastStatisticsVO {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "广播类型")
    private String broadcastType;
    
    @Schema(description = "广播总数")
    private Long totalCount;
    
    @Schema(description = "分发中数量")
    private Long distributingCount;
    
    @Schema(description = "已完成数量")
    private Long completedCount;
    
    @Schema(description = "总接收人数")
    private Long totalReceivers;
    
    @Schema(description = "已分发人数")
    private Long distributedReceivers;
    
    @Schema(description = "已接收人数")
    private Long receivedReceivers;
    
    @Schema(description = "已读人数")
    private Long readReceivers;
    
    @Schema(description = "分发率")
    private Double distributeRate;
    
    @Schema(description = "接收率")
    private Double receiveRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
}