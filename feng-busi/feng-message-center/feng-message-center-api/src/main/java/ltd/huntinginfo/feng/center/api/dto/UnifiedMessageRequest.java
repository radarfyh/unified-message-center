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
//package ltd.huntinginfo.feng.center.api.dto;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//import lombok.experimental.Accessors;
//
///**
// * 统一消息DTO，用于推送到业务系统时使用的请求消息，或者业务系统发送消息时的请求消息
// */
//@Data
//@NoArgsConstructor
//@ToString(callSuper = true)
//@Schema(description = "消息发送请求")
//@EqualsAndHashCode(callSuper = true)
//@Accessors(chain = true)
//public class UnifiedMessageRequest extends UnifiedMessage {
//    /**
//     * 发送时间开始
//     */
//    @JsonProperty("fssjks")
//    @Schema(description = "发送时间开始 yyyy-MM-dd HH:mm:ss.SSS", example = "2025-02-02 12:00:00.000")
//    private String sendTimeStart; 
//    
//    /**
//     * 发送时间结束
//     */
//    @JsonProperty("fssjjs")
//    @Schema(description = "发送时间结束 yyyy-MM-dd HH:mm:ss.SSS", example = "2025-02-03 12:00:00.000")
//    private String sendTimeEnd; 
//    
//    /**
//     * 接收人范围 有单位代码和个人身份证就不看接收人范围
//     */
//    @JsonProperty("jsrfw")
//    @Schema(description = "接收者范围配置（JSON对象），多个接收者时使用", example = "{ \"include\": {\"loginIds\": [\"u001\", \"u002\"], "
//    		+ "\"deptIds\": [\"D_FIN_001\"]}, \"exclude\": { \"loginIds\": [\"u003\", \"u004\"], \"deptIds\": [\"D_FIN_002\"]}")
//    private String receiverScope;
//
//    /**
//     * 消息ID
//     */
//    @JsonProperty("id")
//    @Schema(description = "消息ID", example = "3b7c188271e1453abfc4c17cc304e796")
//    private String messageId;
//}
