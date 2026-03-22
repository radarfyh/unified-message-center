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
package ltd.huntinginfo.feng.common.datasource.support;

/**
 * 数据源相关常量
 *
 * @author lengleng
 * @date 2019-04-01
 */
public interface DataSourceConstants {

	/**
	 * 数据源名称
	 */
	String NAME = "name";

	/**
	 * 默认数据源（master）
	 */
	String DS_MASTER = "master";

	/**
	 * jdbcurl
	 */
	String DS_JDBC_URL = "url";

	/**
	 * 配置类型
	 */
	String DS_CONFIG_TYPE = "conf_type";

	/**
	 * 用户名
	 */
	String DS_USER_NAME = "username";

	/**
	 * 密码
	 */
	String DS_USER_PWD = "password";

	/**
	 * 数据库类型
	 */
	String DS_TYPE = "ds_type";

	/**
	 * 数据库名称
	 */
	String DS_NAME = "ds_name";

	/**
	 * 主机类型
	 */
	String DS_HOST = "host";

	/**
	 * 端口
	 */
	String DS_PORT = "port";

	/**
	 * 实例名称
	 */
	String DS_INSTANCE = "instance";

}
