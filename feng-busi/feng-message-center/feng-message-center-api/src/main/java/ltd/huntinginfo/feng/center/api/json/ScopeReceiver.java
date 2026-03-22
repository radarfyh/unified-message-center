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
package ltd.huntinginfo.feng.center.api.json;

import java.util.List;

import lombok.Data;

@Data
public class ScopeReceiver {
	/**
	 * 用户列表，取自unique_user的ID(不是login_id)
	 */
	private List<String> loginIds;
	/**
	 * 部门列表，取自gov_agency
	 */
	private List<String> deptIds;
	/**
	 * 行政区划代码列表
	 */
	private List<String> divisionCodes;
	/**
	 * 角色列表
	 */
	private List<String> roleCodes;
}
