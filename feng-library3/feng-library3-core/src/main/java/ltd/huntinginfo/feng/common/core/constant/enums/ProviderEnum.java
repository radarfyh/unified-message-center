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
 * LLM提供商 枚举
 * <p>
 * 该枚举类定义了LLM提供商。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 */
@Getter
@AllArgsConstructor
public enum ProviderEnum implements IEnum<String> {

    OPENAI("OPENAI", "OPENAI"),
    DEEPSEEK("DEEPSEEK", "DEEPSEEK"),
    ZHIPU("ZHIPU", "智谱"),
    BAAI("BAAI", "BAAI"),
    MOKAAI("MOKAAI", "MOKAAI"),
    CLAUDE("CLAUDE", "CLAUDE"),
    JURASSIC("JURASSIC", "JURASSIC"),
    META("META", "元宇宙"),
    GOOGLE("GOOGLE", "谷歌"),
    DOUBAO("DOUBAO", "抖音豆包"),
    NETEASE("NETEASE", "网易"),
    XUNFEI("XUNFEI", "科大讯飞"),
    BAIDU("BAIDU", "百度"),
    ALICLOUD("ALICLOUD", "阿里云"),
    OTHER("OTHER", "其他");
	
    /**
     * 状态码
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
    public static ProviderEnum fromCode(String code) {
        return Arrays.stream(values())
                   .filter(e -> e.getCode().equals(code))
                   .findFirst()
                   .orElse(OTHER);
    }
}
