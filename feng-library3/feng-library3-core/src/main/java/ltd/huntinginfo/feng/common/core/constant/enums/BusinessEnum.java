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
package ltd.huntinginfo.feng.common.core.constant.enums;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码枚举类
 * <p>
 * 该枚举类用于定义不同的业务状态码，每个状态码都有对应的描述信息。
 * </p>
 * @author radarfyh
 * @date 2024/12/30
 */

@Getter
@AllArgsConstructor
public enum BusinessEnum implements IEnum<Integer> {
    /**
     * 常用HTTP状态码
     * 范围：100-600
     */
	WEB_OK(HttpStatus.OK.value(), "请求成功"),
	WEB_CREATED(HttpStatus.CREATED.value(), "已创建"),
	WEB_ACCEPTED(HttpStatus.ACCEPTED.value(), "已接受"),
	WEB_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "客户端错误，请求包含语法错误或无法完成请求"),
	WEB_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "请求要求用户的身份认证"),
	WEB_FORBIDDEN(HttpStatus.FORBIDDEN.value(), "服务器理解请求客户端的请求，但是拒绝执行此请求"),
	WEB_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "服务器无法根据客户端的请求找到资源"),
	WEB_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误，无法完成请求"),
	WEB_NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED.value(), "服务器不支持请求的功能，无法完成请求"),
	WEB_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value(), "由于超载或系统维护，服务器暂时的无法处理客户端的请求"),
	WEB_UPGRADE_REQUIRED(HttpStatus.UPGRADE_REQUIRED.value(), "客户端需要升级协议或资源"),
	WEB_METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "方法不允许"),
	WEB_FAILED_DEPENDENCY(HttpStatus.FAILED_DEPENDENCY.value(), "依赖错误"),
	
    /**
     * 用户管理模块异常
     * 状态码范围：1000-1099
     */
	U2_QUERY_USER(1000, "查询用户异常"),
	U2_ADD_USER(1001, "添加用户异常"),
	U2_UPDATE_USER(1002, "更新用户信息异常"),
	U2_DELETE_USER(1003, "删除用户异常"),
	U2_QUERY_ROLE(1010, "查询角色异常"),
	U2_ADD_ROLE(1011, "添加角色异常"),
	U2_UPDATE_ROLE(1012, "更新角色异常"),
	U2_DELETE_ROLE(1013, "删除角色异常"),
	U2_QUERY_DEPT(1020, "查询部门异常"),
	U2_ADD_DEPT(1021, "添加部门异常"),
	U2_UPDATE_DEPT(1022, "更新部门异常"),
	U2_DELETE_DEPT(1023, "删除部门异常"),
	U2_QUERY_STAFF(1030, "查询员工异常"),
	U2_ADD_STAFF(1031, "添加员工异常"),
	U2_UPDATE_STAFF(1032, "更新员工异常"),
	U2_DELETE_STAFF(1033, "删除员工异常"),
	
	U2_JOB_CATEGORY(1034, "员工专业技术职务代码为空"),
	
    /**
     * 日志管理模块异常
     * 状态码范围：1100-1199
     */
	L2_QUERY_LOG_TYPE(1100, "查询日志类型异常"),
	L2_ADD_LOG_TYPE(1101, "添加日志类型异常"),
	L2_UPDATE_LOG_TYPE(1102, "更新日志类型异常"),
	L2_DELETE_LOG_TYPE(1103, "删除日志类型异常"),
	L2_QUERY_LOG(1110, "查询日志异常"),
	L2_ADD_LOG(1111, "添加日志异常"),
	L2_UPDATE_LOG(1112, "更新日志异常"),
	L2_DELETE_LOG(1113, "删除日志异常"),

    /**
     * 消息管理模块异常
     * 状态码范围：1200-1299
     */
	M1_QUERY_MSG_TYPE(1200, "查询日志类型异常"),
	M1_ADD_MSG_TYPE(1201, "添加日志类型异常"),
	M1_UPDATE_MSG_TYPE(1202, "更新日志类型异常"),
	M1_DELETE_MSG_TYPE(1203, "删除日志类型异常"),
	M1_QUERY_MSG_SERVICE_TYPE(1210, "查询消息业务类型异常"),
	M1_ADD_MSG_SERVICE_TYPE(1211, "添加消息业务类型异常"),
	M1_UPDATE_MSG_SERVICE_TYPE(1212, "更新消息业务类型异常"),
	M1_DELETE_MSG_SERVICE_TYPE(1213, "删除消息业务类型异常"),
	M1_QUERY_MAIL_LIST(1220, "查询邮件列表异常"),
	M1_ADD_MAIL_LIST(1221, "添加邮件列表异常"),
	M1_UPDATE_MAIL_LIST(1222, "更新邮件列表异常"),
	M1_DELETE_MAIL_LIST(1223, "删除邮件列表异常"),
	
    /**
     * APP鉴权
     * 状态码范围：1300-1399
     */
    INVALID_APPKEY(1301, "无效的应用标识"),
    SECRET_MISMATCH(1302, "应用密钥不匹配"),
    PARAM_MISSING(1303, "缺少必要的参数"),
    APPKEY_INVALID(1304, "无效的应用标识"),
    APP_DISABLED(1305, "应用已被禁用"),
    APP_EXPIRED(1306, "应用凭证已过期"),
    SYSTEM_ERROR(1307, "认证服务异常"),
    TIMESTAMP_EXPIRED(1308, "时间戳已过期"),
    REPLAY_ATTACK(1309,"请求已被处理"),
    CALLER_INVALID(1310, "无效的调用者标识"),
    SIGNATURE_INVALID(1311, "签名验证失败"),
    APP_NOT_EXISTED(1312, "应用不存在"),
    PERMISSION_DENIED(1399, "权限不足"),
	
    /**
     * 统一消息平台返回码
     * 状态码范围：10000-14000
     */
    UMP_SUCCESS(10000, "业务处理成功"),
    UMP_PARAM_MISSING(11000, "必选参数缺失或为空"),
    UMP_PARAM_LENGTH_ERROR(11001, "{}参数长度错误，请参考规范"),
    UMP_ID_CARD_INVALID(11002, "{}身份证号码不合法，请参考规范"),
    UMP_REGION_CODE_INVALID(11003, "{}行政区划代码不合法，请参考规范"),
    UMP_PROVINCE_MISMATCH(11004, "申请行政区划代码和单位代码不符"),
    UMP_UNIT_CODE_INVALID(11005, "{}单位代码不合法，请参考规范"),  
    UMP_CODE_INVALID(11300, "无效的代码"),
    UMP_CODE_EXISTED(11301, "代码已存在"),
    UMP_CREATE_FAILED(11302, "记录创建失败"),
    UMP_READ_FAILED(11303, "记录查询失败"),
    UMP_UPDATE_FAILED(11304, "记录修改失败"),
    UMP_DELETE_FAILED(11305, "记录删除失败"),
    UMP_ID_INVALID(11306, "无效的记录标识"),
    UMP_ID_EXITST(11307, "ID已存在"),
    UMP_TYPE_INVALID(11308, "无效的类型"),
    UMP_TYPE_EXITST(11309, "类型已存在"),
    UMP_REPLAY_ATTACK(11310,"请求已被处理"),
    UMP_RECORD_NOT_EXISTED(11312, "记录不存在"),
    UMP_PERMISSION_DENIED(11399, "权限不足"),
    UMP_TOKEN_EMPTY(12001, "请求头中Token不能为空"),
    UMP_SERVICE_ERROR_13000(13000, "服务调用异常1，请联系管理员"),
    UMP_SERVICE_ERROR_14000(14000, "服务调用异常2，请联系管理员"),
    ;
	
    /**
     * 状态码
     */
    private final int code;

    /**
     * 描述信息
     */
    private final String msg;

    /**
     * 根据状态码获取对应的描述信息
     * <p>
     * 该方法用于通过状态码获取对应的描述信息，方便业务逻辑中快速获取状态码的含义。
     * </p>
     * 
     * @param code 状态码
     * @return 对应的描述信息
     */
    public static String getMsgByCode(int code) {
        for (BusinessEnum status : BusinessEnum.values()) {
            if (status.getCode() == code) {
                return status.getMsg();
            }
        }
        return "未知状态码"; // 未找到对应的状态码时返回默认值
    }
    
    // code直接存入数据库
    @Override
    public Integer getValue() {
        return this.code; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public Integer getCode() {
        return code;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static BusinessEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(WEB_INTERNAL_SERVER_ERROR);
    }
}
