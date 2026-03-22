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
@Schema(description = "广播接收记录统计VO")
public class BroadcastReceiveRecordStatisticsVO {
    
    @Schema(description = "广播ID")
    private String broadcastId;
    
    @Schema(description = "总记录数")
    private Long totalCount;
    
    @Schema(description = "待送达数量")
    private Long pendingCount;
    
    @Schema(description = "已送达数量")
    private Long deliveredCount;
    
    @Schema(description = "送达失败数量")
    private Long failedCount;
    
    @Schema(description = "未读数量")
    private Long unreadCount;
    
    @Schema(description = "已读数量")
    private Long readCount;
    
    @Schema(description = "送达率")
    private Double deliveredRate;
    
    @Schema(description = "阅读率")
    private Double readRate;
}