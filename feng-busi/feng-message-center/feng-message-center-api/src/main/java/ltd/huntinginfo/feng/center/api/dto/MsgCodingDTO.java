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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "消息编码申请DTO")
public class MsgCodingDTO {
	
	@Valid
	@NotBlank(message = "申请单位代码不能为空")
    @Schema(description = "所属单位编码")
    private String agencyCode;
    
    @NotBlank(message = "所属单位区域（行政区划）代码不能为空")
    @Schema(description = "所属单位区域代码")
    private String divisionCode;
    
    @NotBlank(message = "申请单位名称不能为空")
    @Schema(description = "所属单位名称")
    private String agencyName;
    
    @NotBlank(message = "申请人证件号码不能为空")
    @Schema(description = "申请人证件号码")
    private String applicantIdCard;
    
    @NotBlank(message = "申请人电话不能为空")
    @Schema(description = "申请人电话")
    private String applicantPhone;
    
    @NotBlank(message = "申请人姓名不能为空")
    @Schema(description = "申请人姓名")
    private String applicantName;
}
