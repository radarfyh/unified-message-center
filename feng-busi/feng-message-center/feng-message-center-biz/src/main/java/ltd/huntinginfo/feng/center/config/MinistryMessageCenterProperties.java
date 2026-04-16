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
package ltd.huntinginfo.feng.center.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * yml配置属性
 * @author radarfyh
 * @date 2026/3/24
 */
@Data
@Validated
@ConfigurationProperties(prefix = "ministry.message-center")
public class MinistryMessageCenterProperties {
	
	/**
	 * 使能部级消息中心
	 */
    @NotNull
    private Boolean enabled = false;
    
    /**
     * 是否桩服务器
     */
    @NotNull
    private Boolean mockEnabled = false;
    
    /**
     * 部级消息中心主地址
     */
    @NotBlank
    private String homeUrl = "https://jzpt.ga:8686/api/v1";
    
    /**
     * 部级消息中心资源ID
     */
    @NotBlank
    private String resource = "010000-jzpt-xxzx";
    
    /**
     * 部级消息中心桩服务器主地址
     */
    private String mockHomeUrl = "http://11.33.159.157:3000/mock/59/";
    
    /**
     * 部级消息中心桩服务器资源ID
     */
    private String mockResource = "010000-jzpt-xxzx";
    
    /**
     * 发号服务器主地址
     */
    @NotBlank
    private String homeUrlApplyCode = "http://jzpt.ga:8585/api/v1";
    
    /**
     * 发号服务器资源ID
     */
    @NotBlank
    private String resourceApplyCode = "010000-fhfw";
    
    /**
     * 发号服务器桩地址
     */
    private String mockHomeUrlApplyCode = "http://11.33.159.157:3000/mock/59/";
    /**
     * 发号服务器桩资源
     */
    private String mockResourceApplyCode = "010000-fhfw";
    /**
     * 消息代码发号请求路径
     */
    private String pathApplyMessageCode = "/jzpt/xxbm/17/jcfh";
    
    /**
     * 消息发送请求路径
     */
    private String pathSendMessages = "/external/xxfsfw";
    
    /**
     * 消息接收请求路径
     */
    private String pathReceiveMessage = "/external/xxjsfw";
    
    /**
     * 消息状态修改请求路径
     */
    private String pathUpdateMessageStatus = "/external/xxztgxfw";
    
    /**
     * 未读消息查询请求路径
     */
    private String pathQueryUnreadMessage = "/external/unread/20";
    
    /**
     * Token服务地址
     */
    @NotBlank
    private String tokenUrl = "http://jzpt.ga:9080/jcjq-token-service/api/v1/010000-jzpt-identify/token";
    
    /**
     * 应用ID
     */
    @NotBlank
    private String appId = "DEVICE_MGMT_PLATFORM";
    
    /**
     * 应用密钥
     */
    @NotBlank
    private String appSecret = "d398c20f2aec4e0d8eabfabac99d4cde";
    
    /**
     * 部级消息中心分配的主题编码
     */
    @NotBlank
    private String topicCode = "fz_001800";
    
    /**
     * 部级消息中心分配的消息类型
     */
    @NotBlank
    private String msgType = "001800";
}