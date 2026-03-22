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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 未读接收者视图对象
 * 用于返回广播消息的未读接收者信息
 */
@Data
@Schema(description = "未读接收者信息")
public class UnreadReceiverVO {

    @Schema(description = "广播ID")
    private String broadcastId;

    @Schema(description = "接收者ID")
    private String receiverId;

    @Schema(description = "接收者类型:USER/DEPT/CUSTOM/ALL")
    private String receiverType;

    @Schema(description = "接收状态:PENDING-待送达 SUCCESS-已送达 FAILED-送达失败")
    private String receiveStatus;

    @Schema(description = "阅读状态:0-未读 1-已读")
    private Integer readStatus;
}