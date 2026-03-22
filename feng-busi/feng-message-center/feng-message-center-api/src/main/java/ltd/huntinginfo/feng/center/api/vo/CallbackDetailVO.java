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
import java.util.Map;

@Data
@Schema(description = "回调记录详情VO")
public class CallbackDetailVO {
    
    @Schema(description = "回调记录ID")
    private String id;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "接收者ID")
    private String receiverId;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "回调方法")
    private String callbackMethod;
    
    @Schema(description = "回调数据")
    private Map<String, Object> callbackData;
    
    @Schema(description = "回调签名")
    private String signature;
    
    @Schema(description = "回调ID")
    private String callbackId;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "HTTP状态码")
    private Integer httpStatus;
    
    @Schema(description = "响应内容")
    private String responseBody;
    
    @Schema(description = "错误信息")
    private String errorMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "响应时间")
    private LocalDateTime responseTime;
    
    @Schema(description = "耗时(ms)")
    private Integer costTime;
    
    @Schema(description = "重试次数")
    private Integer retryCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "下次重试时间")
    private LocalDateTime nextRetryTime;
}