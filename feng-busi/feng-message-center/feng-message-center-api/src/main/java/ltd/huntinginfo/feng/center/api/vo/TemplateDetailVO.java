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
import ltd.huntinginfo.feng.center.api.json.TemplateVariableDefinition;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Schema(description = "模板详情VO")
public class TemplateDetailVO {
    
    @Schema(description = "模板ID")
    private String id;
    
    @Schema(description = "模板代码")
    private String templateCode;
    
    @Schema(description = "模板名称")
    private String templateName;
    
    @Schema(description = "模板类型")
    private String templateType;
    
    @Schema(description = "标题模板")
    private String titleTemplate;
    
    @Schema(description = "内容模板")
    private String contentTemplate;
    
    @Schema(description = "模板变量定义")
    private Map<String, TemplateVariableDefinition> variables;
    
    @Schema(description = "解析后的变量定义")
    private Map<String, TemplateVariableDefinition> parsedVariables;
    
    @Schema(description = "默认优先级")
    private Integer defaultPriority;
    
    @Schema(description = "默认推送方式")
    private String defaultPushMode;
    
    @Schema(description = "默认回调地址")
    private String defaultCallbackUrl;
    
    @Schema(description = "状态")
    private Integer status;
    
    @Schema(description = "创建者")
    private String createBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新者")
    private String updateBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}