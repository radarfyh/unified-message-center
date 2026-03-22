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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.center.api.json.CallbackConfig;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "消息详情VO")
@ToString(callSuper = true)
@Accessors(chain = true)
public class MessageDetailVO {
    
    @Schema(description = "消息ID")
    private String id;
    
    @Schema(description = "消息编码")
    private String msgCode;
    
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "主题代码", example = "DEVICE_MGMT_PLATFORM")
    private String topicCode;
    
    @Schema(description = "消息标题")
    private String title;
    
    @Schema(description = "消息内容")
    private Map<String, Object> content;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "发送应用标识", example = "APP001")
    private String senderAppKey;

    @Schema(description = "发送者类型", example = "APP", allowableValues = {"APP", "USER", "SYSTEM"})
    private String senderType;

    @Schema(description = "发送者ID", example = "user_001")
    private String senderId;

    @Schema(description = "发送者名称", example = "系统管理员")
    private String senderName;
    
    @Schema(description = "发送者身份证号码", example = "5102241973XXXXXXXX")
    private String senderIdNumber;
    
    @Schema(description = "发送者电话号码", example = "13912345678")
    private String senderPhone;
    
    @Schema(description = "发送单位ID", example = "111111111111111")
    private String senderUnitId;

    @Schema(description = "发送单位代码", example = "130100000000")
    private String senderUnitCode;

    @Schema(description = "发送单位名称", example = "石家庄市消防局")
    private String senderUnitName;
    
    @Schema(description = "代理消息ID", example = "agent_msg_123")
    private String agentMsgId;

    @Schema(description = "代理平台标识", example = "AGENT_PLATFORM")
    private String agentAppKey;
    
    /**
     * 处理地址：用户看到消息后点击跳转到这个地址，为空则不跳转
     */
    @Schema(description = "处理地址", example = "http://xxx/process")
    private String processUrl;
    
    /**
     * 业务参数：业务参数和消息类型相关，不同消息类型对应的业务参数不同。跨省协同类业务暂无业务参数，本参数为空。
     */
    @Schema(description = "业务参数", example = "")
    private String businessParam;
    
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "推送方式")
    private String pushMode;
    
    @Schema(description = "回调配置(JSON)")
    private CallbackConfig callbackConfig;
    
    @Schema(description = "扩展参数，业务系统自行定义")
    private String extParams;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime expireTime;
    
    @Schema(description = "消息状态")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "发送时间")
    private LocalDateTime sendTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "分发时间")
    private LocalDateTime distributeTime;
    
    @Schema(description = "完成时间（消息生命周期终结，例如已读")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime completeTime;
    
    @Schema(description = "总接收人数")
    private Integer totalReceivers;
    
    @Schema(description = "已接收人数")
    private Integer receivedCount;
    
    @Schema(description = "已读人数")
    private Integer readCount;
    
    @Schema(description = "已读率")
    private Double readRate;
}
