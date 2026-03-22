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
package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.center.api.json.CallbackConfig;
import ltd.huntinginfo.feng.center.api.json.MessageContent;
import ltd.huntinginfo.feng.common.mybatis.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * 消息主表实体类
 * 对应表：ump_msg_main
 * 作用：存储所有消息的核心元数据，支持消息状态流转和统计
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName(value = "ump_msg_main", autoResultMap = true)
@Schema(description = "消息主表实体")
public class UmpMsgMain extends BaseEntity<UmpMsgMain> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID(UUID)", example = "msg_1234567890abcdef")
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息编码(xxbm)", example = "XXBM-DE85WY5M2Y6")
    private String msgCode;

    @Schema(description = "消息类型", example = "NOTICE", allowableValues = {"NOTICE", "ALERT", "BIZ", "AGENT"})
    private String msgType;
    
    @Schema(description = "主题代码", example = "DEVICE_MGMT_PLATFORM")
    private String topicCode;

    @Schema(description = "消息标题", example = "系统维护通知")
    @TableField("title")
    private String title;

    @Schema(description = "消息内容(JSON格式)", example = "{\r\n"
    		+ "      \"header\": {\r\n"
    		+ "        \"title\": \"关于2026年度预算申报的通知\",\r\n"
    		+ "        \"sub_title\": \"财务处〔2026〕12号\"\r\n"
    		+ "      },\r\n"
    		+ "      \"body\": [\r\n"
    		+ "        \"各单位：\",\r\n"
    		+ "        \"为做好2026年度预算编制工作，现将有关事项通知如下。\",\r\n"
    		+ "        \"一、预算申报截止时间为2026年3月31日。\",\r\n"
    		+ "        \"二、请通过财政一体化平台完成申报。\",\r\n"
    		+ "        \"特此通知。\"\r\n"
    		+ "      ],\r\n"
    		+ "      \"footer\": {\r\n"
    		+ "        \"org\": \"财政处\",\r\n"
    		+ "        \"date\": \"2026-01-10\"\r\n"
    		+ "      }\r\n"
    		+ "    }")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private MessageContent content;

    @Schema(description = "优先级(1-5,数字越小优先级越高)", example = "3")
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

	@Schema(description = "发送对象类型: USER-个人 DEPT-部门 CUSTOM-自定义 ALL-全体", example = "USER")
    private String sendTargetType;
	
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

    @Schema(description = "回调地址", example = "http://callback.example.com/api/message/status")
    private String callbackUrl;

    @Schema(description = "推送方式", example = "PUSH", allowableValues = {"PUSH", "POLL"})
    private String pushMode;

    @Schema(description = "回调配置(JSON)", example = "{\r\n"
    		+ "    request: {\r\n"
    		+ "      \"method\": \"POST\",\r\n"
    		+ "      \"timeout\": 3000,\r\n"
    		+ "    \r\n"
    		+ "      \"headers\": {\r\n"
    		+ "		\"Content-Type\": application/json\r\n"
    		+ "		\"X-App-Key\": \"${appKey}\"\r\n"
    		+ "		\"X-Timestamp\": \"${tiemstamp}\"\r\n"
    		+ "		\"X-Nonce\": \"${nonce}\"\r\n"
    		+ "		\"X-Signature\": \"${signature}\"\r\n"
    		+ "		\"X-Body-Md5\": \"${bodyMd5}\"\r\n"
    		+ "      },\r\n"
    		+ "    \r\n"
    		+ "      \"body\": {\r\n"
    		+ "        \"fsdw\": \"${fsdw}\",\r\n"
    		+ "        \"jsdw\": \"${jsdw}\",\r\n"
    		+ "		......\r\n"
    		+ "      }\r\n"
    		+ "    },\r\n"
    		+ "    response: {\r\n"
    		+ "      \"http_status\": 200,\r\n"
    		+ "      \"code\": \"0\"\r\n"
    		+ "    }")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private CallbackConfig callbackConfig;

    @Schema(description = "扩展参数，业务系统自行定义")
    private String extParams;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime expireTime;

    @Schema(description = "消息状态", example = "RECEIVED", 
            allowableValues = {"RECEIVED", "DISTRIBUTING", "DISTRIBUTED", "DIST_FAILED", "PUSHED", "PUSH_FAILED", 
            		"BIZ_RECEIVED", "POLL", "BIZ_PULLED", "POLL_FAILED", "READ"})
    private String status;

    @Schema(description = "发送时间（发送到队列）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime sendTime;

    @Schema(description = "分发时间（分发到收件箱或者广播信息筒）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime distributeTime;

    @Schema(description = "完成时间（消息生命周期终结，例如已读")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime completeTime;

    @Schema(description = "总接收人数", example = "100")
    private Integer totalReceivers;

    @Schema(description = "已接收人数", example = "50")
    private Integer receivedCount;

    @Schema(description = "已读人数", example = "30")
    private Integer readCount;
}