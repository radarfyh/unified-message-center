/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng
 */
package ltd.huntinginfo.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.admin.api.entity.SysDictItem;
import ltd.huntinginfo.feng.common.core.util.R;

/**
 * 字典项服务接口
 *
 * @author lengleng
 * @date 2025/05/30
 */
public interface SysDictItemService extends IService<SysDictItem> {

	/**
	 * 删除字典项
	 * @param id 字典项ID
	 * @return 操作结果
	 */
	R removeDictItem(String id);

	/**
	 * 更新字典项
	 * @param item 需要更新的字典项
	 * @return 操作结果
	 */
	R updateDictItem(SysDictItem item);

}
