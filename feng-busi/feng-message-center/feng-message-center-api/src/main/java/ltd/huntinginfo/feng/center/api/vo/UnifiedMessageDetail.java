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
package ltd.huntinginfo.feng.center.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltd.huntinginfo.feng.center.api.dto.UnifiedMessage;

/**
 * 统一消息详情VO（对应UnifiedMessageData.xxjl）
 */
@Data
@NoArgsConstructor
@Schema(description = "信息记录（详情）,对应UnifiedMessageData.xxjl")
public class UnifiedMessageDetail extends UnifiedMessage {
    /**
     * 处理状态(阅读状态) 0-未读 1-已读
     */
    @JsonProperty("clzt")
    @Schema(description = "处理状态(阅读状态) 0-未读 1-已读", example = "0")
    private String processStatus;
    
    /**
     * 发送时间（发送到队列）
     */
    @JsonProperty("fssj")
    @Schema(description = "发送时间（发送到队列） yyyy-MM-dd HH:mm:ss.SSS", example = "2025-02-02 12:00:00.000")
    private String sendTime; 
    
    /**
     * 分发时间（分发到收件箱或者广播信息筒）
     */
    @JsonProperty("ffsj")
    @Schema(description = "分发时间（分发到收件箱或者广播信息筒） yyyy-MM-dd HH:mm:ss.SSS", example = "2025-02-02 12:00:00.000")
    private String distributeTime; 
    
    /**
     * 接收范围 有单位代码和个人身份证就不看接收人范围
     */
    @JsonProperty("jsfw")
    @Schema(description = "接收范围配置，有单位代码和个人身份证就不看接收人范围，JSON格式：{ \"include\": {\"loginIds\": [\"u001\", \"u002\"], "
    		+ "\"deptIds\": [\"D_FIN_001\"]}, \"exclude\": { \"loginIds\": [\"u003\", \"u004\"], \"deptIds\": [\"D_FIN_002\"]}")
    private String receivingScope;

    /**
     * 消息ID
     */
    @JsonProperty("id")
    @Schema(description = "消息ID", example = "3b7c188271e1453abfc4c17cc304e796")
    private String messageId;
}
