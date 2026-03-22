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
package ltd.huntinginfo.feng.admin.api.dto;

import java.io.Serial;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;

/**
 * 统一用户DTO
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@Schema(description = "统一用户传输对象")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class UniqueUserDTO extends UniqueUser {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	@Schema(description = "角色id集合")
	private List<String> roleIds;

	/**
	 * 部门id
	 */
	@Schema(description = "机关id")
	private List<String> agencyIds;

	/**
	 * 机构ID
	 */
	private List<String> orgIds;
	
	/**
	 * 区域ID
	 */
	private List<String> divisionIds;

	/**
	 * 新密码
	 */
	@Schema(description = "新密码")
	private String newPassword;

}
