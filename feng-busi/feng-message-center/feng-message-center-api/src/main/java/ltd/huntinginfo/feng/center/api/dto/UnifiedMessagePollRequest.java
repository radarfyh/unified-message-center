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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "消息拉取请求")
public class UnifiedMessagePollRequest {

//    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", example = "1")
    private Integer current = 1;

//    @Min(value = 1, message = "每页数量最小为1")
//    @Max(value = 100, message = "每页数量最大为100")
    @Schema(description = "每页数量", example = "10")
    private Integer size = 10;
    
    @Schema(description = "游标ID，上一次拉取返回的cursorId，用于分页和断点续传", example = "MHwwfDE4O3TkyOTY3ODcxMDkwNjQ4MDV8MHwwfDE3NDE2NjM1OTcyNTk=")
    private String cursorId;
    
    @Schema(description = "主题代码（过滤条件）", example = "123456")
    private String topicCode;

    /**
     * 发送对象类型 MqMessageEventConstants.ReceiverTypes
     */
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
    @Schema(description = "接收单位代码", example = "500241000001")
    private String receivingUnitCode;
    
    /**
     * 接收人姓名
     */
    @JsonProperty("jsr")
    @Schema(description = "接收人姓名", example = "李四")
    private String receiverName;
    
    /**
     * 接收人身份证号（个人）
     */
    @JsonProperty("jsrzjhm")
    @Schema(description = "接收人身份证号", example = "123456789123456789")
    private String receiverIdNumber;
    
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
	
    private String token;  // 用户登录业务系统时警综平台分配的令牌

}