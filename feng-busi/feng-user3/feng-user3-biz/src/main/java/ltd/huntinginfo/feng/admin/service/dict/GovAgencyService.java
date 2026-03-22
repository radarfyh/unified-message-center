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

import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;

import java.util.List;

/**
 * 政府机关代码表 服务接口
 * @author Edison.Feng
 * @date 2025/12/30
 */
public interface GovAgencyService extends BaseDictService<GovAgency> {
    
    /**
     * 根据父级代码获取子机构
     */
    List<GovAgency> getByParentCode(String parentCode);
    
    /**
     * 根据机构级别获取机构列表
     */
    List<GovAgency> getByLevel(Integer level);
    
    /**
     * 获取机构树形结构
     */
    List<GovAgency> getAgencyTree();

    /**
     * 获取子孙列表
     * @param id
     * @return
     */    
	List<GovAgency> getDescendantList(String id);
}
