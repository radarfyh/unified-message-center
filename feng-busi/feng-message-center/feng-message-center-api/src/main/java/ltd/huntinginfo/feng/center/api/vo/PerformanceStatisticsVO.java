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
 * 用于ump_msg_statistics统计服务
 */
@Data
@Schema(description = "性能统计VO")
public class PerformanceStatisticsVO {
    
    @Schema(description = "总发送数量")
    private Long totalSendCount;
    
    @Schema(description = "平均处理时间(ms)")
    private Double avgProcessTime;
    
    @Schema(description = "平均接收时间(ms)")
    private Double avgReceiveTime;
    
    @Schema(description = "平均阅读时间(ms)")
    private Double avgReadTime;
    
    @Schema(description = "最大处理时间(ms)")
    private Integer maxProcessTime;
    
    @Schema(description = "最大接收时间(ms)")
    private Integer maxReceiveTime;
    
    @Schema(description = "最大阅读时间(ms)")
    private Integer maxReadTime;
    
    @Schema(description = "P95处理时间(ms)")
    private Double p95ProcessTime;
    
    @Schema(description = "P95接收时间(ms)")
    private Double p95ReceiveTime;
    
    @Schema(description = "P95阅读时间(ms)")
    private Double p95ReadTime;
    
    @Schema(description = "慢消息比例")
    private Double slowMessageRate;
}