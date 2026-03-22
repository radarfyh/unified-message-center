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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "消息上报请求（用于已接收、已拉取、已阅读）")
public class UnifiedMessageReportRequest {

    @NotBlank(message = "消息ID不能为空")
    @JsonProperty("messageId")
    @Schema(description = "消息ID", example = "abc123")
    private String messageId;

    @JsonProperty("receiverId")
    @Schema(description = "接收者ID（若为广播消息且需要指定接收者时使用）", example = "user001")
    private String receiverId;

    @JsonProperty("receiverType")
    @Schema(description = "接收者类型", example = "USER")
    private String receiverType;

    @JsonProperty("broadcastId")
    @Schema(description = "广播ID（若为广播消息时使用）", example = "broadcast001")
    private String broadcastId;
}
