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

package ltd.huntinginfo.feng.admin.service.impl.dict;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ltd.huntinginfo.feng.admin.api.dto.UniqueUserDTO;
import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueUserInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.SysUser;
import ltd.huntinginfo.feng.admin.api.entity.SysUserRole;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueRole;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueUserInfoVO;
import ltd.huntinginfo.feng.admin.mapper.dict.UniqueUserMapper;
import ltd.huntinginfo.feng.admin.service.SysUserRoleService;
import ltd.huntinginfo.feng.admin.service.SysUserService;
import ltd.huntinginfo.feng.admin.service.dict.UniqueRoleService;
import ltd.huntinginfo.feng.admin.service.dict.UniqueUserService;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一用户代码服务实现类
 *
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Slf4j
@Service
@AllArgsConstructor
public class UniqueUserServiceImpl 
    extends ServiceImpl<UniqueUserMapper, UniqueUser> 
    implements UniqueUserService {
	
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final UniqueRoleService uniqueRoleService;
    
    @Override
    public UniqueUserInfoVO getUserById(String id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public IPage<UniqueUserInfoVO> page(IPage page, UniqueUserInfoDTO uniqueUserInfo) {
        QueryWrapper<UniqueUser> wrapper = buildQueryWrapper(uniqueUserInfo);
        IPage<UniqueUser> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }

    @Override
    public List<UniqueUserInfoVO> list(UniqueUserInfoDTO uniqueUserInfo) {
        QueryWrapper<UniqueUser> wrapper = buildQueryWrapper(uniqueUserInfo);
        List<UniqueUser> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(UniqueUserInfoDTO uniqueUserInfo) {
        return super.save(convertToEntity(uniqueUserInfo));
    }

    @Override
    public boolean saveBatch(List<UniqueUserInfoDTO> uniqueUserInfos) {
        return super.saveBatch(uniqueUserInfos.stream().map(this::convertToEntity).collect(Collectors.toList()));
    }

    @Override
    public boolean updateById(UniqueUserInfoDTO uniqueUserInfo) {
        return super.updateById(convertToEntity(uniqueUserInfo));
    }

    @Override
    public boolean removeById(String id) {
        return super.removeById(id);
    }

    private QueryWrapper<UniqueUser> buildQueryWrapper(UniqueUserInfoDTO uniqueUserInfo) {
        QueryWrapper<UniqueUser> wrapper = new QueryWrapper<>();
        
        if (uniqueUserInfo != null) {
            // 用户名模糊查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getUsername())) {
                wrapper.like("username", uniqueUserInfo.getUsername());
            }
            
            // 用户昵称模糊查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getNickname())) {
                wrapper.like("nickname", uniqueUserInfo.getNickname());
            }
            
            // 用户类型查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getType())) {
                wrapper.eq("type", uniqueUserInfo.getType());
            }
            
            // 身份证号精确查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getIdCard())) {
                wrapper.eq("id_card", uniqueUserInfo.getIdCard());
            }
            
            // 机构查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getOrgId())) {
                wrapper.eq("org_id", uniqueUserInfo.getOrgId());
            }
            
            // 手机号查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getMobile())) {
                wrapper.eq("mobile", uniqueUserInfo.getMobile());
            }
        }
        
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
    
    /**
     * 转换为VO对象
     */
    private UniqueUserInfoVO convertToVo(UniqueUser info) {
        if (info == null) {
            return null;
        }
        
        UniqueUserInfoVO vo = new UniqueUserInfoVO();
        BeanUtil.copyProperties(info, vo);
        return vo;
    }
    
    /**
     * 转换为实体对象
     */
    private UniqueUser convertToEntity(UniqueUserInfoDTO uniqueUserInfo) {
    	UniqueUser doi = new UniqueUser();
    	BeanUtil.copyProperties(uniqueUserInfo, doi);
    	return doi;
    }
    
	@Override
//    @Cacheable(key = "UniqueUserInfo_" + "#code")
	@Cacheable(key = "UniqueUserInfo_" + "#username")
	public UniqueUser getByCode(String username) {
		LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(UniqueUser::getLoginId, username);
		UniqueUser ret = baseMapper.selectOne(wrapper);
		return ret;
	}

	@Override
    @Cacheable(key = "UniqueUserInfo_" + "all")
	public List<UniqueUser> getAllValidItems() {
		LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(UniqueUser::getLoginId);
        List<UniqueUser> list = baseMapper.selectList(wrapper);
		return list;
	}

	@Override
    @CacheEvict(allEntries = true)
	public void refreshCache() {
		log.info("刷新统一用户信息缓存");		
	}
	
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enableLoginUser(String uniqueUserId) {

        UniqueUser uniqueUser = this.getById(uniqueUserId);
        if (uniqueUser == null) {
            throw new IllegalArgumentException("员工不存在");
        }

        // 1. 判断是否已开通过
        SysUser exist = sysUserService.getOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, uniqueUser.getMobile()),
                false
        );
        if (exist != null) {
            throw new IllegalStateException("该员工已开通登录账号");
        }

        // 2. 创建 SysUser
        SysUser sysUser = new SysUser();
        sysUser.setUsername(uniqueUser.getMobile());
        sysUser.setPhone(uniqueUser.getMobile());
        sysUser.setName(uniqueUser.getName());
        sysUser.setNickname(uniqueUser.getName());
        sysUser.setDelFlag(CommonConstants.STATUS_NORMAL);
        sysUser.setLockFlag(CommonConstants.STATUS_NORMAL);

        // 默认密码 = 手机号或者123456
        String password = StrUtil.isBlank(uniqueUser.getMobile()) ? "123456" : uniqueUser.getMobile(); 
        sysUser.setPassword(
                new BCryptPasswordEncoder().encode(password)
        );

        sysUserService.save(sysUser);

        // 3. 赋默认角色，若统一认证中心角色含有ADMIN，则为管理员（内置角色：1），否则为普通用户（内置角色:2）
        List<UniqueRole> roles = uniqueRoleService.list(
                Wrappers.<UniqueRole>lambdaQuery()
                        .like(UniqueRole::getCode, "ADMIN")
        );
        String defaultRoleId = roles.isEmpty() ? "2" : "1";
        
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(sysUser.getUserId());
        userRole.setRoleId(defaultRoleId);
        sysUserRoleService.save(userRole);

        // 4. 回写 UniqueUser
        uniqueUser.setSysUserId(sysUser.getUserId());
        uniqueUser.setSysRoleId(defaultRoleId);
        this.updateById(uniqueUser);

        return true;
    }
    
	/**
	 * 查询用户全部信息，包括角色和权限
	 * @param query 用户查询条件
	 * @return 包含用户角色和权限的用户信息对象
	 */
	@Override
	public UniqueUser getUserInfo(UniqueUserDTO query) {
		log.info("getUserInfo query: {}", query);
		
		if (StrUtil.isBlank(query.getLoginId()) 
				&& StrUtil.isBlank(query.getId())
				&& StrUtil.isBlank(query.getIdCard()) ) {
			return null;
		}
			
		UniqueUser dbUser = baseMapper.selectOne(Wrappers.<UniqueUser>lambdaQuery()
                .eq(StrUtil.isNotBlank(query.getLoginId()), UniqueUser::getLoginId, query.getLoginId())
                .eq(StrUtil.isNotBlank(query.getId()), UniqueUser::getId, query.getId())
                .eq(StrUtil.isNotBlank(query.getIdCard()), UniqueUser::getIdCard, query.getIdCard()));
		
		log.info("getUserInfo dbUser: {}", dbUser);
		
		return dbUser;
	}
}
