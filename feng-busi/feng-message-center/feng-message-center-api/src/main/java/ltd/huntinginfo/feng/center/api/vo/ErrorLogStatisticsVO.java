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
@Schema(description = "错误日志统计VO")
public class ErrorLogStatisticsVO {
    
    @Schema(description = "错误类型")
    private String errorType;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @Schema(description = "错误次数")
    private Long errorCount;
    
    @Schema(description = "首次发生时间")
    private String firstOccurrence;
    
    @Schema(description = "最后发生时间")
    private String lastOccurrence;
    
    @Schema(description = "影响API")
    private String affectedApi;
    
    @Schema(description = "影响应用")
    private String affectedApp;
}