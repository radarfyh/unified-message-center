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

package ltd.huntinginfo.feng.common.core.exception;

import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务异常类
 * <p>
 * 用于封装业务相关的异常信息，支持自定义异常状态码、消息、数据以及 Throwable 原因。
 * </p>
 * @author radarfyh
 * @date 2024/12/30
 */
@NoArgsConstructor
@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private int code = CommonConstants.FAIL;

    /**
     * 异常消息
     */
    private String message;

    /**
     * 额外的异常数据
     */
    private Object data;

    /**
     * 最常用的自定义业务异常构造器
     * 
     * @param code    异常状态码
     * @param message 异常消息
     */
    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 直接抛出异常，使用 Throwable 作为原因
     * 
     * @param cause 原因异常
     */
    public BusinessException(Throwable cause) {
        super(cause);
    }

    /**
     * 带有消息的构造方法，允许传递异常消息及原始异常
     * 
     * @param message 异常消息
     * @param cause   原因异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /**
     * 带有异常状态码的构造方法
     * 
     * @param code  异常状态码
     * @param cause 原因异常
     */
    public BusinessException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * 带有状态码、消息和数据的自定义异常构造方法
     * 
     * @param code    异常状态码
     * @param message 异常消息
     * @param data    异常附带的数据
     */
    public BusinessException(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 带有状态码、消息、数据和原始异常的构造方法
     * 
     * @param code    异常状态码
     * @param message 异常消息
     * @param data    异常附带的数据
     * @param cause   原因异常
     */
    public BusinessException(int code, String message, Object data, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 根据 BusinessEnum 定义的异常信息构造业务异常
     * 
     * @param businessEnum 业务枚举类型，包含异常状态码和消息
     */
    public BusinessException(BusinessEnum businessEnum) {
        this.code = businessEnum.getCode();
        this.message = businessEnum.getMsg();
    }
}
