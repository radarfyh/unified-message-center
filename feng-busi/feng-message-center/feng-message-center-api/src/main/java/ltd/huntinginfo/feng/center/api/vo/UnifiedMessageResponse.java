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
//import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
//
///**
// * 统一消息平台API响应VO，用于消息拉取接口
// */
//@Data
//@NoArgsConstructor
//@Schema(description = "消息拉取响应，用于消息拉取接口")
//public class UnifiedMessageResponse<T> {
//    /**
//     * HTTP状态码，取自HttpStatus
//     */
//	@NotBlank(message = "HTTP状态码不能为空")
//    @JsonProperty("status")
//    @Schema(description = "HTTP状态码", example = "200")
//    private Integer httpStatus;
//    
//    /**
//     * 响应消息 取自BusinessEnum 常用HTTP状态码，对应httpStatus
//     */
//    @JsonProperty("message")
//    @Schema(description = "HTTP状态描述", example = "WEB处理成功")
//    private String message;
//    
//    /**
//     * 统一消息响应数据
//     */
//    @JsonProperty("data")
//    @Schema(description = "消息拉取响应数据")
//    private UnifiedMessageData<T> data;
//    
//    // 辅助方法：构建成功响应
//    static public UnifiedMessageResponse<?> buildSuccessResponse(List<Object> records, String cursorId) {
//        UnifiedMessageResponse<Object> response = new UnifiedMessageResponse<Object>();
//        response.setHttpStatus(BusinessEnum.WEB_OK.getCode());
//        response.setMessage(BusinessEnum.WEB_OK.getMsg());
//        UnifiedMessageData<Object> data = new UnifiedMessageData<Object>();
//        data.setMessageDetails(records);
//        data.setResultCode(BusinessEnum.UMP_SUCCESS.getCode().toString());
//        data.setResultMessage(BusinessEnum.UMP_SUCCESS.getMsg());
//        data.setCursorId(cursorId);
//        response.setData(data);
//        return response;
//    }
//    
//    // 辅助方法：构建失败响应
//    static public UnifiedMessageResponse<?> buildFailedResponse(String code, String msg) {
//        UnifiedMessageResponse<Object> response = new UnifiedMessageResponse<Object>();
//        response.setHttpStatus(BusinessEnum.WEB_OK.getCode());
//        response.setMessage(BusinessEnum.WEB_OK.getMsg());
//        UnifiedMessageData<Object> data = new UnifiedMessageData<Object>();
//        data.setMessageDetails(null);
//        data.setResultCode(code);
//        data.setResultMessage(msg);
//        response.setData(data);
//        return response;
//    }
//}
