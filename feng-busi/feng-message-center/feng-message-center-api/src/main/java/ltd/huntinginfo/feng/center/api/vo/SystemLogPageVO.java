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
@Schema(description = "系统日志分页VO")
public class SystemLogPageVO {
    
    @Schema(description = "日志ID")
    private String id;
    
    @Schema(description = "日志类型")
    private String logType;
    
    @Schema(description = "日志级别")
    private String logLevel;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "操作者")
    private String operator;
    
    @Schema(description = "操作名称")
    private String operation;
    
    @Schema(description = "请求ID")
    private String requestId;
    
    @Schema(description = "API路径")
    private String apiPath;
    
    @Schema(description = "HTTP方法")
    private String httpMethod;
    
    @Schema(description = "响应代码")
    private String responseCode;
    
    @Schema(description = "响应消息")
    private String responseMessage;
    
    @Schema(description = "IP地址")
    private String ipAddress;
    
    @Schema(description = "耗时(ms)")
    private Integer costTime;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}