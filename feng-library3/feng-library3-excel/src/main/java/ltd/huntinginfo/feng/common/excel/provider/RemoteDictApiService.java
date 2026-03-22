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
package ltd.huntinginfo.feng.common.excel.provider;

import ltd.huntinginfo.feng.common.core.util.R;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.Map;

/**
 * 远程字典API服务接口，基于RestClient GetExchange实现
 *
 * @author lengleng
 * @date 2025/05/31
 */
public interface RemoteDictApiService {

	/**
	 * 根据类型获取字典数据
	 * @param type 字典类型
	 * @return 包含字典数据的响应对象，字典数据以Map列表形式返回
	 */
	@GetExchange("/dict/remote/type/{type}")
	R<List<Map<String, Object>>> getDictByType(@PathVariable String type);

}
