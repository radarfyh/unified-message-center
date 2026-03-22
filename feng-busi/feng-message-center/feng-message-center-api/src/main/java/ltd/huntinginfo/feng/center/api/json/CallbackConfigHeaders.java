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
package ltd.huntinginfo.feng.center.api.json;

import org.springframework.http.MediaType;

import lombok.Data;

@Data
public class CallbackConfigHeaders {
	/**
	 * HTTP头部：Content-Type
	 */
	private MediaType contentType;
	/**
	 * 自定义HTTP头部：X-App-Key
	 * 传递发起方应用键
	 */
	private String xAppKey;
	/**
	 * 自定义HTTP头部：X-Timestamp
	 * 传递发起方时间戳
	 */
	private String xTimestamp;
	/**
	 * 自定义HTTP头部：X-Nonce
	 * 传递发起方随机值
	 */
	private String xNonce;
	/**
	 * 自定义HTTP头部：X-Signature
	 * 传递发起方签名
	 */
	private String xSignature;
	/**
	 * 自定义HTTP头部：X-Body-Md5
	 * 传递发起方消息体的MD5校验值
	 */
	private String xBodyMd5;
}
