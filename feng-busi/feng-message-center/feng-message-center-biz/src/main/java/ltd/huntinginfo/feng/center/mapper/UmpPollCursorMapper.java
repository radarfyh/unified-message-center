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
package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import ltd.huntinginfo.feng.center.api.entity.UmpPollCursor;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface UmpPollCursorMapper extends BaseMapper<UmpPollCursor> {
    
    @Select("SELECT * FROM ump_poll_cursor WHERE app_key = #{appKey} AND cursor_key = #{cursorKey} AND del_flag = '0'")
    UmpPollCursor selectByAppKeyAndCursorKey(@Param("appKey") String appKey, @Param("cursorKey") String cursorKey);
    
    @Select("SELECT * FROM ump_poll_cursor WHERE status = 1 AND (last_poll_time IS NULL OR DATE_ADD(last_poll_time, INTERVAL poll_interval SECOND) <= NOW()) AND del_flag = '0'")
    List<UmpPollCursor> selectReadyToPoll();
    
    @Update("UPDATE ump_poll_cursor SET cursor_id = #{cursorId}, last_poll_time = NOW(), poll_count = poll_count + 1, error_count = 0, last_success_time = NOW() WHERE id = #{id} AND del_flag = '0'")
    int updatePollSuccess(@Param("id") String id, @Param("cursorId") String cursorId);
    
    @Update("UPDATE ump_poll_cursor SET last_poll_time = NOW(), poll_count = poll_count + 1, error_count = error_count + 1, last_error = #{errorMsg} WHERE id = #{id} AND del_flag = '0'")
    int updatePollError(@Param("id") String id, @Param("errorMsg") String errorMsg);
    
    @Update("UPDATE ump_poll_cursor SET message_count = message_count + #{count}, last_message_time = NOW() WHERE id = #{id} AND del_flag = '0'")
    int incrementMessageCount(@Param("id") String id, @Param("count") Integer count);
}