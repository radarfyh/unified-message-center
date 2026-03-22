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
package ltd.huntinginfo.feng.center.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "AppKey认证请求")
public class AppKeyAuthRequest {
//    @NotBlank(message = "appType不能为空")
    @Schema(description = "应用类型 01-设备管理（子）平台...07-签摁终端", 
           example = "01",
           allowableValues = {"01","02","03","04","05","06","07"}) // 明确枚举值
    private String appType;
    
    @NotBlank(message = "appKey不能为空")
    @Schema(description = "应用标识", requiredMode = RequiredMode.REQUIRED, example = "APP-002")
    private String appKey;
    
//    @NotBlank(message = "appSecret不能为空")
    @Schema(description = "应用密钥(BCRYPT算法加密传输)", requiredMode = RequiredMode.REQUIRED, example = "AbCdEfG123...")
    private String appSecret;
    
    @Schema(description = "设备标识(设备类应用必填)", example = "DEV-001")
    private String deviceId;
    
//    @NotBlank(message = "caller不能为空")
    @Schema(description = "调用者AppKey", requiredMode = RequiredMode.REQUIRED, example = "APP-001")
    private String caller;
    
    @Schema(description = "请求签名(HMAC-SHA-256(appSecret+timestamp+nonce))", 
           example = "a1b2c3...",
           requiredMode = RequiredMode.AUTO) // 根据authMode决定是否必填
    private String signature;
    
    @Schema(description = "时间戳(ms)", example = "1689300000000")
    private Long timestamp;
    
    @Schema(description = "随机字符串(防重放)", example = "x1y2z3")
    private String nonce;
    /**
     * 标准模式流程：
     *   客户端->>服务端: 请求（含signature/timestamp/nonce）
     *   服务端->>服务端: 1. 检查时间戳有效性（±5分钟）
     *   服务端->>服务端: 2. 检查nonce唯一性（防重放）
     *   服务端->>服务端: 3. 验证签名(HMAC-SHA-256)
     *   服务端-->>客户端: 返回认证结果
     * 兼容模式流程：
     *   客户端->>服务端: 请求（仅含appKey/appSecret）
     *   服务端->>服务端: 1. 直接比对appSecret
     *   服务端-->>客户端: 返回认证结果
     */
    @Schema(description = "认证模式(standard/legacy)", 
           example = "standard",
           defaultValue = "standard")
    private String authMode;
    
    @Schema(description = "消息体MD5校验码")
    private String bodyMd5;
}
