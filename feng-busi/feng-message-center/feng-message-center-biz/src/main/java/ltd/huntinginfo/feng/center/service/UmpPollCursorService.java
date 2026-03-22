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
package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpPollCursor;

import java.util.List;

/**
 * 轮询游标表 服务接口
 */
public interface UmpPollCursorService extends IService<UmpPollCursor> {

    /**
     * 根据ID查询游标详情
     */
    UmpPollCursor getById(String id);

    /**
     * 分页查询游标列表
     */
    IPage<UmpPollCursor> page(IPage<UmpPollCursor> page, UmpPollCursor msgPollCursor);

    /**
     * 查询游标列表
     */
    List<UmpPollCursor> list(UmpPollCursor msgPollCursor);

    /**
     * 新增游标
     */
    boolean save(UmpPollCursor msgPollCursor);

    /**
     * 更新游标
     */
    boolean updateById(UmpPollCursor msgPollCursor);

    /**
     * 删除游标
     */
    boolean removeById(String id);

	/**
	 * 根据应用标识获取游标（使用默认游标键）
	 */
	UmpPollCursor getByAppKey(String appKey);

	/**
	 * 获取或创建游标（如果不存在则创建）
	 */
	UmpPollCursor getOrCreateCursor(String appKey, String cursorKey, Integer pollInterval);

	/**
	 * 记录轮询错误
	 */
	boolean recordPollError(String appKey, String cursorKey, String errorMessage);

	/**
	 * 记录轮询成功
	 */
	boolean recordPollSuccess(String appKey, String cursorKey, String newCursorId, int messageCount);

	/**
	 * 获取所有运行中的游标列表
	 */
	List<UmpPollCursor> getRunningCursors();

	/**
	 * 重置游标（清空游标值和错误信息）
	 */
	boolean resetCursor(String appKey, String cursorKey);

	/**
	 * 根据应用标识和游标键获取游标
	 */
	UmpPollCursor getByAppKeyAndCursorKey(String appKey, String cursorKey);

	/**
	 * 更新游标值
	 */
	boolean updateCursorId(String appKey, String cursorKey, String cursorId);

	/**
	 * 检查应用标识和游标键组合是否存在
	 */
	boolean existsAppKeyAndCursorKey(String appKey, String cursorKey);
}