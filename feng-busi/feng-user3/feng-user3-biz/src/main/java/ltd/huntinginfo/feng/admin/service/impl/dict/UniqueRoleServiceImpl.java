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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueRoleInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueRole;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueRoleInfoVO;
import ltd.huntinginfo.feng.admin.mapper.dict.UniqueRoleMapper;
import ltd.huntinginfo.feng.admin.service.dict.UniqueRoleService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一角色代码服务实现类
 *
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Slf4j
@Service
public class UniqueRoleServiceImpl 
    extends ServiceImpl<UniqueRoleMapper, UniqueRole> 
    implements UniqueRoleService {

    @Override
    public UniqueRoleInfoVO getById(Integer id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public IPage<UniqueRoleInfoVO> page(IPage page, UniqueRoleInfoDTO uniqueRoleInfo) {
        QueryWrapper<UniqueRole> wrapper = buildQueryWrapper(uniqueRoleInfo);
        IPage<UniqueRole> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }

    @Override
    public List<UniqueRoleInfoVO> list(UniqueRoleInfoDTO uniqueRoleInfo) {
        QueryWrapper<UniqueRole> wrapper = buildQueryWrapper(uniqueRoleInfo);
        List<UniqueRole> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(UniqueRoleInfoDTO uniqueRoleInfo) {
        return super.save(convertToEntity(uniqueRoleInfo));
    }

    @Override
    public boolean saveBatch(List<UniqueRoleInfoDTO> uniqueRoleInfos) {
        return super.saveBatch(uniqueRoleInfos.stream().map(this::convertToEntity).collect(Collectors.toList()));
    }

    @Override
    public boolean updateById(UniqueRoleInfoDTO uniqueRoleInfo) {
        return super.updateById(convertToEntity(uniqueRoleInfo));
    }

    @Override
    public boolean removeById(Integer id) {
        return super.removeById(id);
    }

    private QueryWrapper<UniqueRole> buildQueryWrapper(UniqueRoleInfoDTO uniqueRoleInfo) {
        QueryWrapper<UniqueRole> wrapper = new QueryWrapper<>();
        
        if (uniqueRoleInfo != null) {
            // 角色名称模糊查询
            if (StringUtils.isNotBlank(uniqueRoleInfo.getName())) {
                wrapper.like("name", uniqueRoleInfo.getName());
            }
            
            // 角色代码精确查询
            if (StringUtils.isNotBlank(uniqueRoleInfo.getCode())) {
                wrapper.eq("code", uniqueRoleInfo.getCode());
            }
            
            // 角色类型查询
            if (uniqueRoleInfo.getType() != null) {
                wrapper.eq("type", uniqueRoleInfo.getType());
            }
            
            // 状态查询
            if (uniqueRoleInfo.getStatus() != null) {
                wrapper.eq("status", uniqueRoleInfo.getStatus());
            }
        }
        
        wrapper.orderByAsc("sort").orderByDesc("create_time");
        return wrapper;
    }
    
    /**
     * 转换为VO对象
     */
    private UniqueRoleInfoVO convertToVo(UniqueRole info) {
        if (info == null) {
            return null;
        }
        
        UniqueRoleInfoVO vo = new UniqueRoleInfoVO();
        BeanUtil.copyProperties(info, vo);
        return vo;
    }
    

    /**
     * 转换为实体对象
     */
    private UniqueRole convertToEntity(UniqueRoleInfoDTO uniqueRoleInfo) {
    	UniqueRole doi = new UniqueRole();
    	BeanUtil.copyProperties(uniqueRoleInfo, doi);
    	return doi;
    }
    
	@Override
    @Cacheable(key = "UniqueRoleInfo_" + "#code")
	public UniqueRole getByCode(String code) {
		LambdaQueryWrapper<UniqueRole> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(UniqueRole::getCode, code);
		UniqueRole ret = baseMapper.selectOne(wrapper);
		return ret;
	}

	@Override
    @Cacheable(key = "UniqueRoleInfo_" + "all")
	public List<UniqueRole> getAllValidItems() {
		LambdaQueryWrapper<UniqueRole> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(UniqueRole::getCode);
        List<UniqueRole> list = baseMapper.selectList(wrapper);
		return list;
	}

	@Override
    @CacheEvict(allEntries = true)
	public void refreshCache() {
		log.info("刷新统一角色信息缓存");
		
	}
}
