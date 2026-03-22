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
@Schema(description = "查询未读消息请求")
public class UnifiedMessageUnreadRequest {
    
	private Cxdwxx cxdwxx;	
	private Cxrxx cxrxx;
	
	@Data
	@Schema(description = "查询单位信息")
	public class Cxdwxx {
		@Schema(description = "查询单位名称")
		private String cxdw;
		@Schema(description = "查询单位代码")
		private String cxdwdm;
	}
	
	@Data
	@Schema(description = "查询人信息")
	public class Cxrxx {
		@Schema(description = "查询人姓名")
		private String cxr;
		@Schema(description = "查询人证件号码")
		private String cxrzjhm;
	}
}