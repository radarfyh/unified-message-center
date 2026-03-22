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
package ltd.huntinginfo.feng.common.datasource.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * JDBC URL 枚举
 *
 * @author lengleng
 * @date 2020/12/11
 */
@Getter
@AllArgsConstructor
public enum DsJdbcUrlEnum {

	/**
	 * mysql 数据库
	 */
	MYSQL("mysql",
			"jdbc:mysql://%s:%s/%s?characterEncoding=utf8"
					+ "&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true"
					+ "&useLegacyDatetimeCode=false&allowMultiQueries=true&allowPublicKeyRetrieval=true",
			"select 1", "mysql8 链接"),

	/**
	 * pg 数据库
	 */
	PG("pg", "jdbc:postgresql://%s:%s/%s", "select 1", "postgresql 链接"),

	/**
	 * SQL SERVER
	 */
	MSSQL("mssql", "jdbc:sqlserver://%s:%s;database=%s;characterEncoding=UTF-8", "select 1", "sqlserver 链接"),

	/**
	 * oracle
	 */
	ORACLE("oracle", "jdbc:oracle:thin:@%s:%s:%s", "select 1 from dual", "oracle 链接"),

	/**
	 * db2
	 */
	DB2("db2", "jdbc:db2://%s:%s/%s", "select 1 from sysibm.sysdummy1", "DB2 TYPE4 连接"),

	/**
	 * 达梦
	 */
	DM("dm", "jdbc:dm://%s:%s/%s", "select 1 from dual", "达梦连接"),

	/**
	 * pg 数据库
	 */
	HIGHGO("highgo", "jdbc:highgo://%s:%s/%s", "select 1", "highgo 链接");

	private final String dbName;

	private final String url;

	private final String validationQuery;

	private final String description;

	public static DsJdbcUrlEnum get(String dsType) {
		return Arrays.stream(DsJdbcUrlEnum.values())
			.filter(dsJdbcUrlEnum -> dsType.equals(dsJdbcUrlEnum.getDbName()))
			.findFirst()
			.get();
	}

}
