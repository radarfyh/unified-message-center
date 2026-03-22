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

import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.admin.api.dto.UserInfo;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.core.util.RetOps;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户详情服务接口，扩展了Spring Security的UserDetailsService和Ordered接口 提供用户详情加载、客户端支持校验及排序功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
public interface FengUserDetailsService extends UserDetailsService, Ordered {

	/**
	 * 是否支持此客户端校验
	 * @param clientId 目标客户端
	 * @return true/false
	 */
	default boolean support(String clientId, String grantType) {
		return true;
	}

	/**
	 * 排序值 默认取最大的
	 * @return 排序值
	 */
	default int getOrder() {
		return 0;
	}

	/**
	 * 根据用户信息构建UserDetails对象
	 * @param result 包含用户信息的R对象
	 * @return 构建好的UserDetails对象
	 * @throws UsernameNotFoundException 当用户信息不存在时抛出异常
	 */
	default UserDetails getUserDetails(R<UserInfo> result) {
		UserInfo info = RetOps.of(result).getData().orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
		Set<String> dbAuthsSet = new HashSet<>();

		// 维护角色列表
		info.getRoleList().forEach(role -> dbAuthsSet.add(SecurityConstants.ROLE + role.getRoleId()));

		// 维护权限列表
		dbAuthsSet.addAll(info.getPermissions());
		Collection<GrantedAuthority> authorities = AuthorityUtils
			.createAuthorityList(dbAuthsSet.toArray(new String[0]));

		// 构造security用户
		return new FengUser(info.getUserId(), info.getDept().getDeptId(), info.getUsername(),
				SecurityConstants.BCRYPT + info.getPassword(), info.getPhone(), true, true, true,
				StrUtil.equals(info.getLockFlag(), CommonConstants.STATUS_NORMAL), authorities);
	}

	/**
	 * 通过用户实体查询用户详情
	 * @param fengUser 用户实体对象
	 * @return 用户详情信息
	 */
	default UserDetails loadUserByUser(FengUser fengUser) {
		return this.loadUserByUsername(fengUser.getUsername());
	}

}
