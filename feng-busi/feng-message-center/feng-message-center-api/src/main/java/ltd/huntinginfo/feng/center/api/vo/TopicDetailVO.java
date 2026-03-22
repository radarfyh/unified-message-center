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
@Schema(description = "主题详情VO")
public class TopicDetailVO {
    
    @Schema(description = "主题ID")
    private String id;
    
    @Schema(description = "主题代码")
    private String topicCode;
    
    @Schema(description = "主题名称")
    private String topicName;
    
    @Schema(description = "主题类型")
    private String topicType;
    
    @Schema(description = "主题描述")
    private String description;
    
    @Schema(description = "默认消息类型")
    private String defaultMsgType;
    
    @Schema(description = "默认优先级")
    private Integer defaultPriority;
    
    @Schema(description = "路由规则配置")
    private Map<String, Object> routingRules;
    
    @Schema(description = "订阅者数量")
    private Integer subscriberCount;
    
    @Schema(description = "最大订阅者数量")
    private Integer maxSubscribers;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "订阅率")
    private Double subscribeRate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}