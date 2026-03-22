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

package ltd.huntinginfo.feng.common.core.constant;

/**
 * @author lengleng
 * @date 2019/2/1
 */
public interface CommonConstants {
    /**
     * 租户头
     */
    String TENANT_HEADER_KEY = "J-Cat";
    /**
     * 租户ID
     */
    String TENANT_ID = "jCat";
    /**
     * 默认租户ID
     */
    String DEFAULT_TENANT_ID = "100";
    
    /**
     * 默认租户编码
     */
    String DEFAULT_TENANT_CODE = "default";
    
    /**
     * 租户头默认密钥
     */
    String DEFAULT_TENANT_CRYPT_KEY = "fengyonghua4java";
    /**
     * Header 中版本信息
     */
    String VERSION = "VERSION";

    /**
     * 机构编码
     */
    String ORGAN_CODE = "organCode";
    /**
     * 默认机构编码
     */
    String ORGAN_CODE_ADMIN = "F001";
    
	/**
	 * 删除
	 */
	String STATUS_DEL = "1";

	/**
	 * 正常
	 */
	String STATUS_NORMAL = "0";

	/**
	 * 锁定
	 */
	String STATUS_LOCK = "9";

	/**
	 * 菜单树根节点
	 */
	String MENU_TREE_ROOT_ID = "-1";

	/**
	 * 菜单
	 */
	String MENU = "0";

	/**
	 * 编码
	 */
	String UTF8 = "UTF-8";

	/**
	 * JSON 资源
	 */
	String CONTENT_TYPE = "application/json; charset=utf-8";

	/**
	 * 前端工程名
	 */
	String FRONT_END_PROJECT = "feng-cloud3-ui";

	/**
	 * 后端工程名
	 */
	String BACK_END_PROJECT = "feng-cloud3";

	/**
	 * 成功标记
	 */
	Integer SUCCESS = 0;

	/**
	 * 失败标记
	 */
	Integer FAIL = 1;

	/**
	 * 当前页
	 */
	String CURRENT = "current";

	/**
	 * size
	 */
	String SIZE = "size";

	/**
	 * 用户名
	 */
	String USERNAME = "username";

	/**
	 * 密码
	 */
	String PASSWORD = "password";

	/**
	 * 请求开始时间
	 */
	String REQUEST_START_TIME = "REQUEST-START-TIME";
	
	/**
	 * 5分钟NONCE时间窗口
	 */
    Integer NONCE_TIME_WINDOW = 5 * 60 * 1000; 
    /**
     * NONC缓存键
     */
    String NONCE_CACHE_PREFIX = "auth:nonce:";
    /**
     * 批量查询最大限制
     */
    Integer BATCH_QUERY_SIZE = 1000;
    /**
     * 分页和列表查询最大记录数量
     */
    Integer PAGE_LIST_QUERY_LIMIT = 10000;

}
