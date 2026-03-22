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
package ltd.huntinginfo.feng.center.api.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    // 过期时间，单位秒
    @Value("${jwt.expiration}")
    private Long expiration;

    // 缓存转换后的密钥
    private SecretKey cachedSecretKey;

    /**
     * 获取已验证的密钥（带缓存）
     */
    private SecretKey getSecretKey() {
        if (cachedSecretKey == null) {
            cachedSecretKey = convertStringToKey(this.secret);
        }
        return cachedSecretKey;
    }

    // 以下所有方法都使用getSecretKey()替代直接使用secret字符串
    // ================ 生成Token方法 ================
    public String generateToken(String userId) {
        return generateToken(userId, null, null);
    }

    public String generateToken(String userId, String appKey) {
        return generateToken(userId, appKey, null);
    }

    public String generateToken(String userId, List<String> permissions) {
        return generateToken(userId, null, permissions);
    }

    public String generateToken(String userId, String appKey, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userId);
        claims.put("iat", new Date());
        
        if (appKey != null) claims.put("appKey", appKey);
        if (permissions != null) claims.put("perms", permissions);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ================ 验证/解析方法 ================
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getExpireInSeconds() {
        return expiration;
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        return (List<String>) getClaimsFromToken(token).get("perms");
    }

    // ================ 密钥转换方法 ================
    private SecretKey convertStringToKey(String secretStr) {
        try {
            // 1. 清理密钥字符串
            String cleanedSecret = secretStr
                .trim()
                .replaceAll("\\s+", ""); // 移除所有空白字符
            
            // 2. 验证Base64格式
            if (!cleanedSecret.matches("^[A-Za-z0-9+/]+={0,2}$")) {
                throw new IllegalArgumentException("密钥包含非法Base64字符");
            }
            
            // 3. 解码并验证长度
            byte[] keyBytes = Decoders.BASE64.decode(cleanedSecret);
            if (keyBytes.length < 64) {
                throw new IllegalArgumentException(
                    "HS512需要64字节密钥，当前长度: " + keyBytes.length + "字节\n" +
                    "有效密钥示例: " + generateNewKey());
            }
            
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "密钥转换失败，请检查：\n" +
                "1. 是否为有效的Base64编码（仅包含A-Za-z0-9+/=）\n" +
                "2. 是否包含隐藏字符（建议复制此密钥）：\n" +
                "-----BEGIN KEY-----\n" +
                secretStr + "\n" +
                "-----END KEY-----", 
                e);
        }
    }

    // ================ 密钥生成工具 ================
    public static String generateNewKey() {
        return Encoders.BASE64.encode(
            Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()
        );
    }
    
    @PostConstruct
    public void init() {
    	log.info("secret:{}", this.secret);
    	if (this.secret.length() < 64) {
//    		this.secret = generateNewKey();
    		this.secret = "TURkaU5HRTFZVFEwTTJNeE4yRXpOVFppTlRFd1lUUTFaRGhoT0daaE1XVmhOMlE1WkdVMVpqTXdOakV5WWpNWgo=";
    	}
    	
    	if (this.expiration < 2400) {
    		this.expiration = 2400L;
    	}
    	
        try {
            SecretKey key = convertStringToKey(this.secret);
            log.info("JWT密钥初始化成功，算法: {}", key.getAlgorithm());
        } catch (Exception e) {
            log.error("JWT密钥配置错误", e);
            log.info("尝试使用此自动生成密钥：\n{}", generateNewKey());
            throw e;
        }
    }
}