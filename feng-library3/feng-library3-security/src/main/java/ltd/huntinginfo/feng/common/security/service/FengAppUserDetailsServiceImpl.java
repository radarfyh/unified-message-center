/*
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
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
 *  Author: lengleng
 *
 *  Modified by radarfyh(Edison.Feng) on 2025-12-30.
 *  Copyright (c) 2026 radarfyh(Edison.Feng). All rights reserved.
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */

package ltd.huntinginfo.feng.common.security.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;

import ltd.huntinginfo.feng.admin.api.dto.UserDTO;
import ltd.huntinginfo.feng.admin.api.dto.UserInfo;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUserService;
import ltd.huntinginfo.feng.common.core.constant.CacheConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.util.R;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用户详细信息服务实现类，提供基于手机号的用户信息加载功能
 *
 * @author lengleng hccake
 * @date 2025/05/31
 */
@RequiredArgsConstructor
public class FengAppUserDetailsServiceImpl implements FengUserDetailsService {

	private final RemoteUserService remoteUserService;

	private final CacheManager cacheManager;

	/**
	 * 根据手机号加载用户信息
	 * @param phone 用户手机号
	 * @return 用户详细信息
	 * @throws Exception 获取用户信息过程中可能抛出的异常
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String phone) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(phone) != null) {
			return (FengUser) cache.get(phone).get();
		}

		UserDTO userDTO = new UserDTO();
		userDTO.setPhone(phone);
		R<UserInfo> result = remoteUserService.info(userDTO);

		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(phone, userDetails);
		}
		return userDetails;
	}

	/**
	 * 根据用户信息加载用户详情
	 * @param fengUser 用户信息对象
	 * @return 用户详情
	 */
	@Override
	public UserDetails loadUserByUser(FengUser fengUser) {
		return this.loadUserByUsername(fengUser.getPhone());
	}

	/**
	 * 是否支持此客户端校验
	 * @param clientId 目标客户端
	 * @return true/false
	 */
	@Override
	public boolean support(String clientId, String grantType) {
		return SecurityConstants.MOBILE.equals(grantType);
	}

}
