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

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 数据字典基础服务接口
 * @author Edison.Feng
 * @date 2025/12/30
 */
public interface BaseDictService<T> extends IService<T> {
    /**
     * 根据代码获取字典项
     */
    T getByCode(String code);

    /**
     * 获取所有有效字典项
     */
    List<T> getAllValidItems();

    /**
     * 刷新缓存
     */
    void refreshCache();
}
