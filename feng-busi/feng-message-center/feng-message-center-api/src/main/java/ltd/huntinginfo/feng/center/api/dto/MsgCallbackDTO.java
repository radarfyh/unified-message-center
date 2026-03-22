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
import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
@Schema(name = "消息回调DTO", description = "MSG-1010接口回调参数")
public class MsgCallbackDTO {
    
    @Schema(description = "事件类型", requiredMode = Schema.RequiredMode.REQUIRED, 
            allowableValues = {"MESSAGE_RECEIVED"})
    @NotBlank(message = "事件类型不能为空")
    private String eventType;
    
    @Schema(description = "事件时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "事件时间不能为空")
    private Date eventTime;
    
    @Schema(description = "代理平台消息ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "代理平台消息ID不能为空")
    private String msgId;
    
    @Schema(description = "消息内容（省级格式）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息内容不能为空")
    @Valid
    private MessageInfoDTO message;
    
    @Schema(description = "发送方信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "发送方信息不能为空")
    @Valid
    private CenterSenderDTO sender;
    
    @Schema(description = "接收方信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "接收方信息不能为空")
    @Valid
    private CenterReceiverDTO receiver;
    
    @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, 
            allowableValues = {"0", "1"})
    @NotBlank(message = "处理状态不能为空")
    private String clzt;
    
    @Data
    @Schema(name = "消息信息DTO")
    public static class MessageInfoDTO {
        @Schema(description = "消息编码", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息编码不能为空")
        private String xxbm;
        
        @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息类型不能为空")
        private String xxlx;
        
        @Schema(description = "消息标题", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息标题不能为空")
        private String xxbt;
        
        @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "消息内容不能为空")
        private String xxnr;
        
        @Schema(description = "处理地址")
        private String cldz;
        
        @Schema(description = "紧急程度")
        private String jjcd;
        
        @Schema(description = "业务参数")
        private String ywcs;
        
        @Schema(description = "图标(base64)")
        private String tb;
    }
    
    @Data
    @Schema(name = "中心发送方DTO")
    public static class CenterSenderDTO {
        @Schema(description = "发送单位", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "发送单位不能为空")
        private String fsdw;
        
        @Schema(description = "发送单位代码", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "发送单位代码不能为空")
        private String fsdwdm;
        
        @Schema(description = "发送人", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "发送人不能为空")
        private String fsr;
        
        @Schema(description = "发送人证件号码", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "发送人证件号码不能为空")
        private String fsrzjhm;
        
        @Schema(description = "发送时间", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "发送时间不能为空")
        private Date fssj;
    }
    
    @Data
    @Schema(name = "中心接收方DTO")
    public static class CenterReceiverDTO {
        @Schema(description = "接收单位")
        private String jsdw;
        
        @Schema(description = "接收单位代码")
        private String jsdwdm;
        
        @Schema(description = "接收人")
        private String jsr;
        
        @Schema(description = "接收人证件号码")
        private String jsrzjhm;
    }
}