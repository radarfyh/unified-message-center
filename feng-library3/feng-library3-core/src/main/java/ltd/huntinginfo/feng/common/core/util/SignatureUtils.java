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

package ltd.huntinginfo.feng.common.core.util;

import java.nio.charset.StandardCharsets;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class SignatureUtils {

    /**
     * 生成 HMAC-SHA256 签名
     * @param appKey 应用KEY
     * @param appSecret 应用密钥
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param bodyMd5 请求消息体的MD5值
     * @return 签名字符串
     */
    public String generateSignature(String appKey, String appSecret, Long timestamp, String nonce, String bodyMd5) {
        // 构建签名字符串: appKey|timestamp|nonce|body_md5
    	String signContent = "";
    	if(StrUtil.isNotBlank(bodyMd5)) {
	        signContent = String.format("%s|%d|%s|%s", 
	        		appKey, 
	        		timestamp, 
	        		nonce,
	        		bodyMd5);
    	} else {
	        signContent = String.format("%s|%d|%s", 
	        		appKey, 
	        		timestamp, 
	        		nonce);
    	}
    	
    	log.debug("signContent:{},getAppKey:{},getTimestamp:{},getNonce:{},getBodyMd5:{}", signContent, appKey, timestamp, nonce, bodyMd5);
        
        // 使用HMAC-SHA256计算签名
        HMac hmac = new HMac(HmacAlgorithm.HmacSHA256, appSecret.getBytes(StandardCharsets.UTF_8));
        String calculatedSignature = hmac.digestHex(signContent);
        
        return calculatedSignature;
    }
    
    /**
     * 校验签名是否合法
     * @param appKey 应用KEY
     * @param appSecret 应用密钥
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param bodyMd5 业务系统请求消息体的MD5值
     * @param signature 业务系统提供的签名值
     * @return 签名字符串
     */
    public Boolean verifySignature(String appKey, String appSecret, Long timestamp, String nonce, String bodyMd5, String signature) {
    	String mySignature = generateSignature(appKey, appSecret, timestamp, nonce, bodyMd5);
        
        log.debug("calculatedSignature:{},appSecret:{}, getSignature:{}, getCaller:{}", mySignature, appSecret, signature, appKey);
        
        // 比对签名
        return mySignature.equals(signature);
    }
    
    /**
     * 校验字符串是否为有效的 MD5（32位十六进制）
     */
    public boolean isValidMd5(String md5) {
        return md5 != null && md5.matches("^[a-fA-F0-9]{32}$");
    }
    
    /**
     * 计算消息体的 MD5 值（字节数组形式）
     * @param body 消息体字节数组
     * @return MD5 十六进制字符串，若 body 为空则返回空字符串
     */
    public String calculateBodyMd5(byte[] body) {
        if (body == null || body.length == 0) {
            return "";
        }
        return DigestUtil.md5Hex(body);
    }

    /**
     * 计算消息体的 MD5 值（字符串形式）
     * @param body 消息体字符串
     * @return MD5 十六进制字符串，若 body 为 blank 则返回空字符串
     */
    public String calculateBodyMd5(String body) {
        if (StrUtil.isBlank(body)) {
            return "";
        }
        return DigestUtil.md5Hex(body, StandardCharsets.UTF_8);
    }
}