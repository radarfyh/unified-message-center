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
//package ltd.huntinginfo.feng.center.api.vo;
//
//import java.util.List;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.constraints.NotBlank;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * 统一消息响应数据VO，用于UnifiedMessageResponse.data
// */
//@Data
//@NoArgsConstructor
//@Schema(description = "消息拉取响应数据，用于UnifiedMessageResponse.data")
//public class UnifiedMessageData<T> {
//    /**
//     * 业务返回码 统一消息平台返回码
//     */
//	@NotBlank(message = "code业务返回码不能为空")
//    @JsonProperty("code")
//    @Schema(description = "业务返回码", example = "00000")
//    private String resultCode;
//    
//    /**
//     * 业务处理结果描述
//     */
//    @JsonProperty("info")
//    @Schema(description = "业务处理结果描述", example = "业务处理成功")
//    private String resultMessage;
//    
//    /**
//     * 游标ID
//     */
//    @JsonProperty("ybid")
//    @Schema(description = "游标ID，初次填空，后续填上一次返回的ybid", example = "MHwwfDE4O3TkyOTY3ODcxMDkwNjQ4MDV8MHwwfDE3NDE2NjM1OTcyNTk=")
//    private String cursorId;
//    
//    /**
//     * 统一消息记录（详情列表）
//     */
//    @JsonProperty("xxjl")
//    private List<T> messageDetails;
//}
