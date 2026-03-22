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

package ltd.huntinginfo.feng.admin.service.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;

import ltd.huntinginfo.feng.admin.api.dto.UniqueUserDTO;
import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueUserInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueUserInfoVO;
import ltd.huntinginfo.feng.common.core.util.R;

import java.util.List;

/**
 * 统一用户信息表 服务接口
 * @author Edison.Feng
 * @date 2025/12/30
 */
public interface UniqueUserService extends BaseDictService<UniqueUser> {

    /**
     * 根据ID查询用户详情
     */
    UniqueUserInfoVO getUserById(String id);

    /**
     * 分页查询用户列表
     */
    IPage<UniqueUserInfoVO> page(IPage page, UniqueUserInfoDTO dictUserInfo);

    /**
     * 查询用户列表
     */
    List<UniqueUserInfoVO> list(UniqueUserInfoDTO dictUserInfo);

    /**
     * 新增用户信息
     */
    boolean save(UniqueUserInfoDTO dictUserInfo);

    /**
     * 批量新增用户信息
     */
    boolean saveBatch(List<UniqueUserInfoDTO> dictUserInfos);

    /**
     * 更新用户信息
     */
    boolean updateById(UniqueUserInfoDTO dictUserInfo);

    /**
     * 删除用户信息
     */
    boolean removeById(String id);
    
    /**
     * 为员工开通系统登录账号
     * @param uniqueUserId 员工ID
     * @return 是否成功
     */
    boolean enableLoginUser(String uniqueUserId);

	/**
	 * 查询用户全部信息，包括角色和权限
	 * @param query 用户查询条件
	 * @return 包含用户角色和权限的用户信息对象
	 */
	UniqueUser getUserInfo(UniqueUserDTO query);
}