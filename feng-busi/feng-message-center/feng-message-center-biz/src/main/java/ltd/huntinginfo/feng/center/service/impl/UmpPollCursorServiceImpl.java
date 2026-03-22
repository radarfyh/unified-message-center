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
package ltd.huntinginfo.feng.center.service.impl;

import ltd.huntinginfo.feng.center.api.entity.UmpPollCursor;
import ltd.huntinginfo.feng.center.mapper.UmpPollCursorMapper;
import ltd.huntinginfo.feng.center.service.UmpPollCursorService;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UmpPollCursorServiceImpl extends ServiceImpl<UmpPollCursorMapper, UmpPollCursor> implements UmpPollCursorService {
    private static final String DEFAULT_CURSOR_KEY = "COMPONENT1";
    
    @Override
    public UmpPollCursor getById(String id) {
        try {
            UmpPollCursor result = super.getById(id);
            if (BeanUtil.isEmpty(result)) {
                log.warn("未找到对应的游标记录: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("查询游标详情失败: id={}", id, e);
            throw new BusinessException("查询游标详情失败", e);
        }
    }

    @Override
    public IPage<UmpPollCursor> page(IPage<UmpPollCursor> page, UmpPollCursor msgPollCursor) {
        try {
            LambdaQueryWrapper<UmpPollCursor> wrapper = buildQueryWrapper(msgPollCursor);
            wrapper.orderByDesc(UmpPollCursor::getLastPollTime); // 默认按最后轮询时间倒序
            return super.page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询游标列表失败", e);
            throw new BusinessException("分页查询游标列表失败", e);
        }
    }

    @Override
    public List<UmpPollCursor> list(UmpPollCursor msgPollCursor) {
        try {
            LambdaQueryWrapper<UmpPollCursor> wrapper = buildQueryWrapper(msgPollCursor);
            wrapper.orderByDesc(UmpPollCursor::getLastPollTime); // 默认按最后轮询时间倒序
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("查询游标列表失败", e);
            throw new BusinessException("查询游标列表失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(UmpPollCursor msgPollCursor) {
        try {
            // 设置ID
            if (BeanUtil.isNotEmpty(msgPollCursor) && StrUtil.isBlank(msgPollCursor.getId())) {
                msgPollCursor.setId(IdUtil.fastSimpleUUID());
            }
            
            // 验证应用标识和游标键是否已存在
            if (msgPollCursor.getAppKey() != null && msgPollCursor.getCursorKey() != null) {
                UmpPollCursor existingCursor = getByAppKeyAndCursorKey(
                    msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
                if (BeanUtil.isNotEmpty(existingCursor)) {
                    log.error("保存游标失败，应用标识和游标键组合已存在: appKey={}, cursorKey={}", 
                            msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
                    throw new BusinessException("应用标识和游标键组合已存在", new Throwable("应用标识和游标键组合已存在"));
                }
            }
            
            // 设置默认值
            if (msgPollCursor.getCursorKey() == null) {
                msgPollCursor.setCursorKey(DEFAULT_CURSOR_KEY);
            }
            
            if (msgPollCursor.getPollInterval() == null) {
                msgPollCursor.setPollInterval(10); // 默认10秒
            }
            
            if (msgPollCursor.getPollCount() == null) {
                msgPollCursor.setPollCount(0);
            }
            
            if (msgPollCursor.getMessageCount() == null) {
                msgPollCursor.setMessageCount(0);
            }
            
            if (msgPollCursor.getErrorCount() == null) {
                msgPollCursor.setErrorCount(0);
            }
            
            if (msgPollCursor.getStatus() == null) {
                msgPollCursor.setStatus(1);
            }
            
            boolean result = super.save(msgPollCursor);
            if (result) {
                log.debug("保存游标成功: id={}, appKey={}, cursorKey={}", 
                        msgPollCursor.getId(), msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
            } else {
                log.error("保存游标失败");
            }
            return result;
        } catch (Exception e) {
            log.error("保存游标失败", e);
            throw new BusinessException("保存游标失败", e);
        }
    }

    @Override
    public boolean updateById(UmpPollCursor msgPollCursor) {
        try {
            // 验证游标是否存在
            UmpPollCursor existingCursor = super.getById(msgPollCursor.getId());
            if (BeanUtil.isEmpty(existingCursor)) {
                log.warn("更新游标失败，记录不存在: id={}", msgPollCursor.getId());
                return false;
            }
            
            // 如果修改了应用标识或游标键，需要检查是否与其他记录冲突
            if ((msgPollCursor.getAppKey() != null && !msgPollCursor.getAppKey().equals(existingCursor.getAppKey())) ||
                (msgPollCursor.getCursorKey() != null && !msgPollCursor.getCursorKey().equals(existingCursor.getCursorKey()))) {
                
                String appKey = msgPollCursor.getAppKey() != null ? msgPollCursor.getAppKey() : existingCursor.getAppKey();
                String cursorKey = msgPollCursor.getCursorKey() != null ? msgPollCursor.getCursorKey() : existingCursor.getCursorKey();
                
                UmpPollCursor duplicateCursor = getByAppKeyAndCursorKey(appKey, cursorKey);
                if (BeanUtil.isNotEmpty(duplicateCursor) && !duplicateCursor.getId().equals(msgPollCursor.getId())) {
                    log.error("更新游标失败，应用标识和游标键组合已存在: appKey={}, cursorKey={}", appKey, cursorKey);
                    throw new BusinessException("应用标识和游标键组合已存在", new Throwable("应用标识和游标键组合已存在"));
                }
            }
            
            boolean result = super.updateById(msgPollCursor);
            if (result) {
                log.debug("更新游标成功: id={}, appKey={}, cursorKey={}", 
                        msgPollCursor.getId(), msgPollCursor.getAppKey(), msgPollCursor.getCursorKey());
            } else {
                log.warn("更新游标失败: id={}", msgPollCursor.getId());
            }
            return result;
        } catch (Exception e) {
            log.error("更新游标失败: id={}", msgPollCursor.getId(), e);
            throw new BusinessException("更新游标失败", e);
        }
    }

    @Override
    public boolean removeById(String id) {
        try {
            boolean result = super.removeById(id);
            if (result) {
                log.debug("删除游标成功: id={}", id);
            } else {
                log.error("删除游标失败: id={}", id);
            }
            return result;
        } catch (Exception e) {
            log.error("删除游标失败: id={}", id, e);
            throw new BusinessException("删除游标失败", e);
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<UmpPollCursor> buildQueryWrapper(UmpPollCursor msgPollCursor) {
        LambdaQueryWrapper<UmpPollCursor> wrapper = new LambdaQueryWrapper<>();
        
        if (BeanUtil.isNotEmpty(msgPollCursor)) {
            // 按ID查询
            if (msgPollCursor.getId() != null && !msgPollCursor.getId().isEmpty()) {
                wrapper.eq(UmpPollCursor::getId, msgPollCursor.getId());
            }
            
            // 按应用标识查询
            if (msgPollCursor.getAppKey() != null && !msgPollCursor.getAppKey().isEmpty()) {
                wrapper.eq(UmpPollCursor::getAppKey, msgPollCursor.getAppKey());
            }
            
            // 按游标键查询
            if (msgPollCursor.getCursorKey() != null && !msgPollCursor.getCursorKey().isEmpty()) {
                wrapper.eq(UmpPollCursor::getCursorKey, msgPollCursor.getCursorKey());
            }
            
            // 按状态查询
            if (msgPollCursor.getStatus() != null) {
                wrapper.eq(UmpPollCursor::getStatus, msgPollCursor.getStatus());
            }
            
            // 按错误次数查询
            if (msgPollCursor.getErrorCount() != null) {
                wrapper.ge(UmpPollCursor::getErrorCount, msgPollCursor.getErrorCount());
            }
        }
        
        return wrapper;
    }

    /**
     * 根据应用标识和游标键获取游标
     */
    @Override
    public UmpPollCursor getByAppKeyAndCursorKey(String appKey, String cursorKey) {
        try {
        	return baseMapper.selectByAppKeyAndCursorKey(appKey, cursorKey);
        } catch (Exception e) {
            log.error("根据应用标识和游标键查询失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("查询游标失败", e);
        }
    }

    /**
     * 根据应用标识获取游标（使用默认游标键）
     */
    @Override
    public UmpPollCursor getByAppKey(String appKey) {
        return getByAppKeyAndCursorKey(appKey, DEFAULT_CURSOR_KEY);
    }

    /**
     * 检查应用标识和游标键组合是否存在
     */
    @Override
    public boolean existsAppKeyAndCursorKey(String appKey, String cursorKey) {
        try {
        	UmpPollCursor cursor = getByAppKeyAndCursorKey(appKey, cursorKey);
        	return BeanUtil.isNotEmpty(cursor);
        } catch (Exception e) {
            log.error("检查应用标识和游标键组合是否存在失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("检查游标失败", e);
        }
    }

    /**
     * 获取或创建游标（如果不存在则创建）
     */
    @Override
    public UmpPollCursor getOrCreateCursor(String appKey, String cursorKey, Integer pollInterval) {
        if (StrUtil.isBlank(appKey)) {
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "应用标识不能为空");
        }
        try {
            UmpPollCursor cursor = getByAppKeyAndCursorKey(appKey, cursorKey);
            
            if (BeanUtil.isEmpty(cursor)) {
                // 创建新游标
                cursor = new UmpPollCursor();
                cursor.setId(IdUtil.fastSimpleUUID());
                cursor.setAppKey(appKey);
                cursor.setCursorKey(cursorKey);
                cursor.setPollInterval(pollInterval != null ? pollInterval : 10);
                cursor.setCursorId(""); // 初始为空
                cursor.setStatus(1);
                
                boolean result = this.save(cursor);
                if (result) {
                    log.debug("创建游标成功: appKey={}, cursorKey={}", appKey, cursorKey);
                } else {
                    log.error("创建游标失败: appKey={}, cursorKey={}", appKey, cursorKey);
                    return null;
                }
            }
            
            return cursor;
        } catch (Exception e) {
            log.error("获取或创建游标失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("获取或创建游标失败", e);
        }
    }

    /**
     * 更新游标值
     */
    @Override
    public boolean updateCursorId(String appKey, String cursorKey, String cursorId) {
        try {
            LambdaUpdateWrapper<UmpPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UmpPollCursor::getAppKey, appKey);
            wrapper.eq(UmpPollCursor::getCursorKey, cursorKey);
            wrapper.set(UmpPollCursor::getCursorId, cursorId);
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("更新游标值成功: appKey={}, cursorKey={}, cursorId={}...", 
                        appKey, cursorKey, cursorId != null ? cursorId.substring(0, Math.min(10, cursorId.length())) : "");
            } else {
                log.warn("更新游标值失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("更新游标值失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("更新游标值失败", e);
        }
    }

    /**
     * 记录轮询成功
     */
    @Override
    public boolean recordPollSuccess(String appKey, String cursorKey, String newCursorId, int messageCount) {
        try {
            LambdaUpdateWrapper<UmpPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UmpPollCursor::getAppKey, appKey);
            wrapper.eq(UmpPollCursor::getCursorKey, cursorKey);
            
            // 设置游标值
            if (StrUtil.isNotBlank(newCursorId)) {
                wrapper.set(UmpPollCursor::getCursorId, newCursorId);
            }
            
            // 更新统计信息
            wrapper.setSql("poll_count = poll_count + 1");
            wrapper.setSql("message_count = message_count + " + messageCount);
            wrapper.set(UmpPollCursor::getLastPollTime, LocalDateTime.now());
            wrapper.set(UmpPollCursor::getLastSuccessTime, LocalDateTime.now());
            wrapper.set(UmpPollCursor::getErrorCount, 0); // 重置错误计数
            wrapper.set(UmpPollCursor::getLastError, null); // 清除错误信息
            
            if (messageCount > 0) {
                wrapper.set(UmpPollCursor::getLastMessageTime, LocalDateTime.now());
            }
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("记录轮询成功: appKey={}, cursorKey={}, messageCount={}", 
                        appKey, cursorKey, messageCount);
            } else {
                log.warn("记录轮询成功失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("记录轮询成功失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("记录轮询成功失败", e);
        }
    }

    /**
     * 记录轮询错误
     */
    @Override
    public boolean recordPollError(String appKey, String cursorKey, String errorMessage) {
        try {
            LambdaUpdateWrapper<UmpPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UmpPollCursor::getAppKey, appKey);
            wrapper.eq(UmpPollCursor::getCursorKey, cursorKey);
            
            wrapper.setSql("poll_count = poll_count + 1");
            wrapper.setSql("error_count = error_count + 1");
            wrapper.set(UmpPollCursor::getLastPollTime, LocalDateTime.now());
            wrapper.set(UmpPollCursor::getLastError, errorMessage);
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("记录轮询错误: appKey={}, cursorKey={}, error={}", 
                        appKey, cursorKey, errorMessage);
            } else {
                log.warn("记录轮询错误失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("记录轮询错误失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("记录轮询错误失败", e);
        }
    }

    /**
     * 重置游标（清空游标值和错误信息）
     */
    @Override
    public boolean resetCursor(String appKey, String cursorKey) {
        try {
            LambdaUpdateWrapper<UmpPollCursor> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UmpPollCursor::getAppKey, appKey);
            wrapper.eq(UmpPollCursor::getCursorKey, cursorKey);
            
            wrapper.set(UmpPollCursor::getCursorId, "");
            wrapper.set(UmpPollCursor::getErrorCount, 0);
            wrapper.set(UmpPollCursor::getLastError, null);
            
            boolean result = super.update(wrapper);
            if (result) {
                log.debug("重置游标成功: appKey={}, cursorKey={}", appKey, cursorKey);
            } else {
                log.warn("重置游标失败，记录不存在: appKey={}, cursorKey={}", appKey, cursorKey);
            }
            return result;
        } catch (Exception e) {
            log.error("重置游标失败: appKey={}, cursorKey={}", appKey, cursorKey, e);
            throw new BusinessException("重置游标失败", e);
        }
    }

    /**
     * 获取所有运行中的游标列表
     */
    @Override
    public List<UmpPollCursor> getRunningCursors() {
        try {
            LambdaQueryWrapper<UmpPollCursor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UmpPollCursor::getStatus, 1); // 运行中
            return super.list(wrapper);
        } catch (Exception e) {
            log.error("获取运行中的游标列表失败", e);
            throw new BusinessException("获取游标列表失败", e);
        }
    }
}