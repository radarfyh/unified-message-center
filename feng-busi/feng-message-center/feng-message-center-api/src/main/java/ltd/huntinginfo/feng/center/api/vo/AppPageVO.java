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
@Schema(description = "应用分页VO")
public class AppPageVO {
    
    @Schema(description = "应用ID")
    private String id;
    
    @Schema(description = "应用标识")
    private String appKey;
    
    @Schema(description = "应用名称")
    private String appName;
    
    @Schema(description = "应用类型")
    private String appType;
    
    @Schema(description = "应用描述")
    private String appDesc;
    
    @Schema(description = "默认推送方式")
    private String defaultPushMode;
    
    @Schema(description = "API调用速率限制(次/分钟)")
    private Integer rateLimit;
    
    @Schema(description = "最大消息大小(字节)")
    private Integer maxMsgSize;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "密钥剩余天数")
    private Long secretRemainingDays;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "密钥过期时间")
    private LocalDateTime secretExpireTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}