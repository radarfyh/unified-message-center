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
import lombok.Data;

@Data
@Schema(description = "消息上报请求（用于已接收、已拉取、已阅读）")
public class UnifiedMessageReportRequest {

	@Schema(description = "处理单位名称")
	private String cldw;
	@Schema(description = "处理单位代码")
	private String cldwdm;
	@Schema(description = "处理人姓名")
	private String clr;
	@Schema(description = "处理人证件号码")
	private String clrzjhm;
	@Schema(description = "消息编码")
	private String xxbm;   
	@Schema(description = "用户登录业务系统时警综平台分配的令牌")
    private String token;  
}
