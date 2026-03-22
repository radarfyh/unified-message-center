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

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息处理状态枚举：
 * ump_broadcast_receive_record.receive_status
 * ump_msg_queue.status
 * ump_msg_callback.status
 * ump_msg_inbox.receive_status
 * ump_msg_inbox.push_status
 * <p>
 * 该枚举类定义了消息处理状态。
 * </p>
 * 
 * @author edison
 * @since 2026/2/9
 */
@Getter
@AllArgsConstructor
public enum MessageProcessStatus implements IEnum<String> {
	PENDING("PENDING", "待处理"),
	PROCESSING("PROCESSING", "处理中"),
	SUCCESS("SUCCESS", "已完成/成功"),
	FAILED("FAILED", "失败"),
	RETRY("RETRY", "待重试"),
	RETRYING("RETRYING", "重试中"),
	RETRY_SUCCESS("RETRY_SUCCESS", "重试成功"),
	RETRY_FAILED("RETRY_FAILED", "重试失败");
	
    /**
     * 编码
     */
    private final String code;

    /**
     * 描述信息
     */
    private final String msg;
    
    // code直接存入数据库
    @Override
    public String getValue() {
        return this.code; 
    }
    
    // 序列化时使用code值
    @JsonValue  
    public String getCode() {
        return code;
    }
    
    // 反序列化注解
    @JsonCreator 
    public static MessageProcessStatus fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(FAILED);
    }

}
