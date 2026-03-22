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
package ltd.huntinginfo.feng.admin.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ltd.huntinginfo.feng.admin.api.entity.SysDict;
import ltd.huntinginfo.feng.admin.api.entity.SysDictItem;
import ltd.huntinginfo.feng.admin.mapper.SysDictItemMapper;
import ltd.huntinginfo.feng.admin.mapper.SysDictMapper;
import ltd.huntinginfo.feng.admin.service.SysDictService;
import ltd.huntinginfo.feng.common.core.constant.CacheConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.DictTypeEnum;
import ltd.huntinginfo.feng.common.core.exception.ErrorCodes;
import ltd.huntinginfo.feng.common.core.util.MsgUtils;
import ltd.huntinginfo.feng.common.core.util.R;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;

/**
 * 系统字典服务实现类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Service
@AllArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

	private final SysDictItemMapper dictItemMapper;

	/**
	 * 根据ID删除字典
	 * @param ids 字典ID数组
	 * @return 操作结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R removeDictByIds(String[] ids) {

		List<String> dictIdList = baseMapper.selectByIds(CollUtil.toList(ids))
			.stream()
			.filter(sysDict -> !sysDict.getSystemFlag().equals(DictTypeEnum.SYSTEM.getType()))// 系统内置类型不删除
			.map(SysDict::getId)
			.toList();

		baseMapper.deleteByIds(dictIdList);

		dictItemMapper.delete(Wrappers.<SysDictItem>lambdaQuery().in(SysDictItem::getDictId, dictIdList));
		return R.ok();
	}

	/**
	 * 更新字典数据
	 * @param dict 字典对象
	 * @return 操作结果
	 * @see R 返回结果封装类
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, key = "#dict.dictType")
	public R updateDict(SysDict dict) {
		SysDict sysDict = this.getById(dict.getId());
		// 系统内置
		if (DictTypeEnum.SYSTEM.getType().equals(sysDict.getSystemFlag())) {
			return R.failed(MsgUtils.getMessage(ErrorCodes.SYS_DICT_UPDATE_SYSTEM));
		}
		this.updateById(dict);
		return R.ok(dict);
	}

	/**
	 * 同步字典缓存（清空缓存）
	 * @return 操作结果
	 */
	@Override
	@CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
	public R syncDictCache() {
		return R.ok();
	}

}
