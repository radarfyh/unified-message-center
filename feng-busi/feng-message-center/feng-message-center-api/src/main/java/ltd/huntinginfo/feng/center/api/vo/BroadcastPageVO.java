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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;

import java.time.LocalDateTime;

@Data
@Schema(description = "广播分页VO")
public class BroadcastPageVO {
    
    @Schema(description = "广播ID")
    private String id;
    
    @Schema(description = "消息ID")
    private String msgId;
    
    @Schema(description = "广播类型")
    private String broadcastType;
    
    @Schema(description = "接收单位ID")
    private String receivingUnitId;

    @Schema(description = "接收单位代码")
    private String receivingUnitCode;

    @Schema(description = "接收单位名称")
    private String receivingUnitName;
    
    @Schema(description = "接收范围配置(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ReceivingScope receivingScope;

    @Schema(description = "接收范围描述")
    private String receivingDescription;
    
    @Schema(description = "总接收人数")
    private Integer totalReceivers;
    
    @Schema(description = "已分发数量")
    private Integer distributedCount;
    
    @Schema(description = "已接收数量")
    private Integer receivedCount;
    
    @Schema(description = "已读人数")
    private Integer readCount;
    
    @Schema(description = "状态")
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;
    
    @Schema(description = "分发进度")
    private Double distributeProgress;
}