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
package ltd.huntinginfo.feng.common.mybatis.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 长整型数组与字符串类型转换处理器
 * <p>
 * 实现数据库VARCHAR类型与Java Long数组类型的相互转换
 *
 * @author lengleng
 * @date 2025/05/31
 * @see MappedTypes 映射的Java类型为Long[]
 * @see MappedJdbcTypes 映射的JDBC类型为VARCHAR
 */
@MappedTypes(value = { Long[].class })
@MappedJdbcTypes(value = JdbcType.VARCHAR)
public class JsonLongArrayTypeHandler extends BaseTypeHandler<Long[]> {

	/**
	 * 设置非空参数到PreparedStatement中
	 * @param ps PreparedStatement对象
	 * @param i 参数位置
	 * @param parameter 长整型数组参数
	 * @param jdbcType JDBC类型
	 * @throws SQLException 数据库操作异常
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Long[] parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, ArrayUtil.join(parameter, StrUtil.COMMA));
	}

	/**
	 * 从ResultSet中获取指定列名的长整型数组结果
	 * @param rs 结果集
	 * @param columnName 列名
	 * @return 转换后的长整型数组，可能为null
	 * @throws SQLException 数据库访问错误时抛出
	 */
	@Override
	@SneakyThrows
	public Long[] getNullableResult(ResultSet rs, String columnName) {
		String reString = rs.getString(columnName);
		return Convert.toLongArray(reString);
	}

	/**
	 * 从ResultSet中获取指定列的长整型数组
	 * @param rs 结果集
	 * @param columnIndex 列索引
	 * @return 长整型数组，可能为null
	 * @throws SQLException 数据库访问错误时抛出
	 */
	@Override
	@SneakyThrows
	public Long[] getNullableResult(ResultSet rs, int columnIndex) {
		String reString = rs.getString(columnIndex);
		return Convert.toLongArray(reString);
	}

	/**
	 * 从CallableStatement中获取指定列的长整型数组
	 * @param cs CallableStatement对象
	 * @param columnIndex 列索引
	 * @return 转换后的长整型数组，可能为null
	 * @throws Exception 数据库访问出错或转换异常时抛出
	 */
	@Override
	@SneakyThrows
	public Long[] getNullableResult(CallableStatement cs, int columnIndex) {
		String reString = cs.getString(columnIndex);
		return Convert.toLongArray(reString);
	}

}
