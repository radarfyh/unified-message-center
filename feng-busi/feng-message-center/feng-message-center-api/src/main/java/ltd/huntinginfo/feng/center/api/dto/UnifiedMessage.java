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
package ltd.huntinginfo.feng.center.api.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ltd.huntinginfo.feng.center.api.json.MessageContent;

/**
 * 统一消息
 */
@Data
@NoArgsConstructor
@Schema(description = "统一消息")
public class UnifiedMessage {
    
    /**
     * 发送单位名称
     */
    @JsonProperty("fsdw")
	@Schema(description = "发送单位名称", example = "石家庄市消防救援支队")
    private String sendUnitName;
    
    /**
     * 发送单位代码
     */
    @JsonProperty("fsdwdm")
	@Schema(description = "发送单位代码", example = "130100000000")
    private String sendUnitCode;
    
    /**
     * 发送人姓名
     */
    @JsonProperty("fsr")
	@Schema(description = "发送人姓名", example = "张三")
    private String senderName;
    
    /**
     * 发送人身份证号
     */
    @JsonProperty("fsrzjhm")
	@Schema(description = "发送人身份证号", example = "123456789123456789")
    private String senderIdNumber;
    
    /**
     * 发送人电话号码
     */
    @JsonProperty("fsrdhhm")
	@Schema(description = "发送人电话号码", example = "13912345678")
    private String senderPhone;
	
    /**
     * 发送对象类型 MqMessageEventConstants.ReceiverTypes
     */
	@NotBlank(message = "发送对象类型不能为空")
    @JsonProperty("fsdx")
	@Schema(description = "发送对象类型: USER-个人 DEPT-部门 CUSTOM-自定义 ALL-全体", example = "USER")
    private String sendTargetType;
    
    /**
     * 接收单位名称
     */
    @JsonProperty("jsdw")
    @Schema(description = "接收单位名称", example = "重庆市秀山县消防救援大队")
    private String receivingUnitName;
    
    /**
     * 接收单位代码
     */
    @JsonProperty("jsdwdm")
    @Schema(description = "接收单位代码，无接收人身份证号、无接收单位代码就看接收者范围配置", example = "500241000001")
    private String receivingUnitCode;
    
    /**
     * 接收人姓名
     */
    @JsonProperty("jsr")
    @Schema(description = "接收人姓名", example = "李四")
    private String receiverName;
    
    /**
     * 接收人身份证号（个人） 有身份证号码就不看单位代码
     */
    @JsonProperty("jsrzjhm")
    @Schema(description = "接收人身份证号，无接收人身份证号就看接收单位代码", example = "123456789123456789")
    private String receiverIdNumber;
    
    /**
     * 接收人电话号码
     */
    @JsonProperty("jsrdhhm")
    @Schema(description = "接收人电话号码", example = "13912345678")
    private String receiverPhone;

    /**
     * 消息编码
     */
    @JsonProperty("xxbm")
    @Schema(description = "消息编码", example = "XXBM-DE85WY5M2Y6")
    private String messageCode;
    
    /**
     * 消息类型
     */
    @NotBlank(message = "消息类型不能为空")
    @JsonProperty("xxlx")
    @Schema(description = "消息类型", example = "050201")
    private String messageType;
    
    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    @JsonProperty("xxbt")
    @Schema(description = "消息标题", example = "工作周报")
    private String messageTitle;
    
    /**
     * 消息内容
     */
    @NotNull(message = "消息内容不能为空")
    @JsonProperty("xxnr")
    @Schema(description = "消息内容，支持JSON格式，若提供模板代码，则会替换消息内容")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private MessageContent messageContent;
    
    /**
     * 处理地址：用户看到消息后点击跳转到这个地址，为空则不跳转
     */
    @JsonProperty("cldz")
    @Schema(description = "处理地址", example = "http://xxx/process")
    private String processUrl;
    
    /**
     * 紧急程度（优先级）
     */
    @JsonProperty("jjcd")
    @Schema(description = "紧急程度（优先级）", example = "5")
    private String priority;
    
    /**
     * 业务参数：业务参数和消息类型相关，不同消息类型对应的业务参数不同。跨省协同类业务暂无业务参数，本参数为空。
     */
    @JsonProperty("ywcs")
    @Schema(description = "业务参数", example = "")
    private String businessParam;

    /**
     * 图标
     */
    @JsonProperty("tb")
    @Schema(description = "图标base64", example = "data:image/jpg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKD...")
    private String icon;
}

