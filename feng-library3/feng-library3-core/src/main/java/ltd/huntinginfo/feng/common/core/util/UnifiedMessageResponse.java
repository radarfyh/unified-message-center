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

package ltd.huntinginfo.feng.common.core.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;

import java.io.Serializable;
import java.util.List;

/**
 * 统一消息平台API响应VO，用于消息拉取接口
 * 优化：提供静态工厂方法简化构建
 * @author radarfyh
 * @date 2024/12/30
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UnifiedMessageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * HTTP状态码，取自HttpStatus
     */
    private Integer status;

    /**
     * 响应消息 取自BusinessEnum 常用HTTP状态码，对应httpStatus
     */
    private String message;

    /**
     * 统一消息响应数据
     */
    private UnifiedMessageData<T> data;

    // ========== 静态工厂方法 ==========

    /**
     * 构建成功响应（无数据）
     */
    public static UnifiedMessageResponse<?> success() {
        UnifiedMessageResponse<Object> response = new UnifiedMessageResponse<>();
        response.setStatus(BusinessEnum.WEB_OK.getCode());
        response.setMessage(BusinessEnum.WEB_OK.getMsg());
        UnifiedMessageData<Object> data = new UnifiedMessageData<>();
        data.setCode(BusinessEnum.UMP_SUCCESS.getCode().toString());
        data.setInfo(BusinessEnum.UMP_SUCCESS.getMsg());
        response.setData(data);
        return response;
    }

    /**
     * 构建成功响应（带数据和游标，用于拉取接口）
     */
    public static <T> UnifiedMessageResponse<T> success(List<T> records, String cursorId) {
        UnifiedMessageResponse<T> response = new UnifiedMessageResponse<>();
        response.setStatus(BusinessEnum.WEB_OK.getCode());
        response.setMessage(BusinessEnum.WEB_OK.getMsg());
        UnifiedMessageData<T> data = new UnifiedMessageData<>();
        data.setCode(BusinessEnum.UMP_SUCCESS.getCode().toString());
        data.setInfo(BusinessEnum.UMP_SUCCESS.getMsg());
        data.setXxjl(records);
        data.setYbid(cursorId);
        response.setData(data);
        return response;
    }

    /**
     * 构建失败响应（业务失败）
     */
    public static UnifiedMessageResponse<?> fail(String code, String msg) {
        UnifiedMessageResponse<Object> response = new UnifiedMessageResponse<>();
        response.setStatus(BusinessEnum.WEB_OK.getCode());
        response.setMessage(BusinessEnum.WEB_OK.getMsg());
        UnifiedMessageData<Object> data = new UnifiedMessageData<>();
        data.setCode(code);
        data.setInfo(msg);
        data.setXxjl(null);
        response.setData(data);
        return response;
    }

    /**
     * 构建失败响应（使用业务枚举）
     */
    public static UnifiedMessageResponse<?> fail(BusinessEnum businessEnum) {
        return fail(businessEnum.getCode().toString(), businessEnum.getMsg());
    }

    /**
     * 构建失败响应（HTTP 状态非 200 的情况，如 401、500 等）
     */
    public static UnifiedMessageResponse<?> error(Integer httpStatus, String message) {
        UnifiedMessageResponse<Object> response = new UnifiedMessageResponse<>();
        response.setStatus(httpStatus);
        response.setMessage(message);
        UnifiedMessageData<Object> data = new UnifiedMessageData<>();
        data.setCode(String.valueOf(httpStatus));
        data.setInfo(message);
        response.setData(data);
        return response;
    }

    @Deprecated
    public static UnifiedMessageResponse<?> buildSuccessResponse(List<Object> records, String cursorId) {
        return success(records, cursorId);
    }

    @Deprecated
    public static UnifiedMessageResponse<?> buildFailedResponse(String code, String msg) {
        return fail(code, msg);
    }
}
