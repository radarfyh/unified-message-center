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
package ltd.huntinginfo.feng.common.file.core;

import org.springframework.beans.factory.InitializingBean;

import java.io.InputStream;
import java.util.List;

/**
 * 文件操作模板
 *
 * @author lengleng
 * @date 2022/4/19
 */
public interface FileTemplate extends InitializingBean {

	/**
	 * 创建bucket
	 * @param bucketName bucket名称
	 */
	void createBucket(String bucketName);

	/**
	 * 获取全部bucket
	 * <p>
	 *
	 * API Documentation</a>
	 */
	List<? extends Object> getAllBuckets();

	/**
	 * @param bucketName bucket名称
	 * @see <a href= Documentation</a>
	 */
	void removeBucket(String bucketName);

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @param contextType 文件类型
	 * @throws Exception
	 */
	void putObject(String bucketName, String objectName, InputStream stream, String contextType) throws Exception;

	/**
	 * 上传文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream 文件流
	 * @param contextType 文件类型
	 * @throws Exception
	 */
	void putObject(String bucketName, String objectName, InputStream stream) throws Exception;

	/**
	 * 获取文件
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 文件对象 API Documentation</a>
	 */
	Object getObject(String bucketName, String objectName);

	void removeObject(String bucketName, String objectName) throws Exception;

	/**
	 * @throws Exception
	 */
	@Override
	default void afterPropertiesSet() throws Exception {
	}

	/**
	 * 根据文件前置查询文件
	 * @param bucketName bucket名称
	 * @param prefix 前缀
	 * @param recursive 是否递归查询
	 * @return 文件对象列表
	 * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListObjects">AWS
	 * API Documentation</a>
	 */
	List<? extends Object> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive);

}
