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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Schema(description = "消息发送DTO")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class MessageSendDTO extends UnifiedMessage {
    
	@JsonProperty("yybs")
    @NotBlank(message = "发送应用标识不能为空")
    @Schema(description = "发送应用标识", example = "APP001")
    private String senderAppKey;
    
	@JsonProperty("fszlx")
    @Schema(description = "发送者类型:APP-应用自动产生 USER-用户 SYSTEM-本系统自动产生", example = "APP", defaultValue = "APP")
    private String senderType = "APP";
    
	@JsonProperty("fsrbs")
    @Schema(description = "发送人标识", example = "user_001")
    private String senderId;
    
	@JsonProperty("fsdwbs")
    @Schema(description = "发送单位标识", example = "dept_001")
    private String senderUnitId;
    
	@JsonProperty("mbdm")
    @Schema(description = "模板代码（使用模板时提供）", example = "NOTICE_TEMPLATE_001")
    private String templateCode;

	@JsonProperty("mbbl")
    @Schema(description = "模板变量（用于替换模板中的占位符）", example = "{"
    		+ "        \"headerTitle\": {\"required\": true, \"description\": \"标题\"},"
    		+ "        \"body1\": {\"required\": true, \"description\": \"内容第一段\"},"
    		+ "        \"body2\": {\"required\": false, \"description\": \"内容第二段\"},"
    		+ "        \"org\": {\"required\": true, \"description\": \"发布单位\"},"
    		+ "        \"date\": {\"required\": true, \"description\": \"日期\"}"
    		+ "    }")
    private Map<String, Object> templateVariables;
    
	@JsonProperty("ztdm")
    @Schema(description = "主题代码")
    private String topicCode;
    
	@JsonProperty("jsrsl")
    @Schema(description = "接收人数量", example = "1", defaultValue = "1")
    private Integer receiverCount = 1;
    
	@JsonProperty("jsfwpz")
    @Schema(description = "接收范围配置(JSON)：include对象必发，exclude对象必不发；loginIds：用户ID集合，deptIds：单位ID集合，不同时为空才有效", example = "{"
    		+ "  \"include\": {"
    		+ "    \"loginIds\": [\"u001\", \"u002\"],"
    		+ "    \"deptIds\": [\"D_FIN_001\"]"
    		+ "  },"
    		+ "  \"exclude\": {"
    		+ "    \"loginIds\": [\"u003\", \"u004\"],"
    		+ "    \"deptIds\": [\"D_FIN_002\"]"
    		+ "  }"
    		+ "}")
    private Map<String, Object> receivingScope;
    
	@JsonProperty("tsfs")
    @Schema(description = "推送方式", example = "PUSH", defaultValue = "PUSH")
    private String pushMode = "PUSH";
    
	@JsonProperty("hddz")
    @Schema(description = "回调地址", example = "http://callback.example.com/api/message/status")
    private String callbackUrl;
    
	@JsonProperty("hdpz")
    @Schema(description = "回调配置(JSON)：平台调用回调地址时提供的请求消息和应答消息模板，使用$+属性名作为变量", example = "{"
    		+ "    request: {"
    		+ "      \"method\": \"POST\","
    		+ "      \"timeout\": 3000,"
    		+ "    "
    		+ "      \"headers\": {"
    		+ "		\"Content-Type\": application/json"
    		+ "		\"X-App-Key\": \"${appKey}\""
    		+ "		\"X-Timestamp\": \"${tiemstamp}\""
    		+ "		\"X-Nonce\": \"${nonce}\""
    		+ "		\"X-Signature\": \"${signature}\""
    		+ "		\"X-Body-Md5\": \"${bodyMd5}\""
    		+ "      },"
    		+ "    "
    		+ "      \"body\": {"
    		+ "        \"fsdw\": \"${fsdw}\","
    		+ "        \"jsdw\": \"${jsdw}\","
    		+ "		......"
    		+ "      }"
    		+ "    },"
    		+ "    response: {"
    		+ "      \"http_status\": 200,"
    		+ "      \"code\": \"0\""
    		+ "    }")
    private Map<String, Object> callbackConfig;
    
	@JsonProperty("kzcs")
    @Schema(description = "扩展参数(JSON)", example = "{"
    		+ "    request: {"
    		+ "      \"timeout\": 3000"
    		+ "    },"
    		+ "    response: {"
    		+ "      \"timeout\": 3000"
    		+ "    }")
    private Map<String, Object> extParams;
    
	@JsonProperty("gqsj")
    @Schema(description = "过期时间", example = "2025-02-03 12:00:00.000")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime expireTime;
}
