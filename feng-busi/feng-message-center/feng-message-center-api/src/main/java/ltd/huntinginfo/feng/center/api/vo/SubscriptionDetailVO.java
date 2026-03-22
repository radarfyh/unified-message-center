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
@Schema(description = "订阅详情VO")
public class SubscriptionDetailVO {
    
    @Schema(description = "订阅ID")
    private String id;
    
    @Schema(description = "主题代码")
    private String topicCode;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "订阅配置")
    private Map<String, Object> callbackConfig;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "推送方式")
    private String pushMode;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "接收消息数量")
    private Integer messageCount;
    
    @Schema(description = "订阅时长（天）")
    private Long subscriptionDays;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "订阅时间")
    private LocalDateTime subscribeTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "取消订阅时间")
    private LocalDateTime unsubscribeTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;
}