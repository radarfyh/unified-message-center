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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import ltd.huntinginfo.feng.center.api.dto.MsgCodingDTO;

public class CodeGeneratorUtil {
    
    private static final int MAX_CODE_LENGTH = 32;
    private static final int MIN_CODE_LENGTH = 18;

    static public String UnifiedMessageCodeGenerator(MsgCodingDTO request, String appKey, Integer length) {
    	validateLength(length);
    	
        // 时间戳：毫秒级时间戳（或格式化字符串，此处使用毫秒数）
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        // 签名：基于请求和appKey字段拼接后计算MD5，确保唯一性
        String rawData = request.getAgencyCode() 
                + request.getDivisionCode() 
                + request.getAgencyName() 
                + request.getApplicantIdCard() 
                + request.getApplicantPhone() 
                + request.getApplicantName()
                + appKey;  // 加入appKey进一步增强唯一性
        String signature = DigestUtil.md5Hex(rawData);
        
        // 计算固定部分实际长度
        int fixedLength = 2 + timestamp.length(); // "UM" + timestamp
        if (fixedLength >= length) {
            throw new IllegalArgumentException("长度必须为" + MIN_CODE_LENGTH + "-" + MAX_CODE_LENGTH + "之间的整数，实际输入: " + length);
        }
        int sigLength = length - fixedLength;
        // 截取签名
        String truncatedSig = signature.substring(0, sigLength);
        return "UM" + timestamp + truncatedSig;
    }
    
    // 设备码生成（ZB前缀）
    static public String DeviceCodeGenerator(Integer length) {
        validateLength(length);
        return "ZB" + getRandomUUIDSubstring(length - 2);
    }
    
    // 区域码生成（QY前缀）
    static public String ZoneCodeGenerator(Integer length) {
        validateLength(length);
        return "QY" + getRandomUUIDSubstring(length - 2);
    }
    
    // 位置码生成（CS前缀）
    static public String PlaceCodeGenerator(Integer length) {
        validateLength(length);
        return "CS" + getRandomUUIDSubstring(length - 2);
    }
    
    // 生成条形码（默认尺寸）
    static public String BarcodeGenerator(String code) throws IOException {
        return BarcodeGenerator(code, 300, 100);
    }
    
    // 生成条形码（自定义尺寸，返回Base64）
    static public String BarcodeGenerator(String code, int width, int height) throws IOException {
        validateCodeAndSize(code, width, height);
        
        Code128Writer writer = new Code128Writer();
        BitMatrix matrix = writer.encode(code, BarcodeFormat.CODE_128, width, height);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        }
    }
    
    // 生成二维码（返回Base64）
    static public String QrcodeGenerator(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("编码内容不能为空");
        }
        byte[] qrCodeBytes = QrCodeUtil.generatePng(code, 300, 300);
        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }
    
    //--- 私有方法 ---//
    static private void validateLength(Integer length) {
        if (length == null || length < MIN_CODE_LENGTH || length > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException("长度必须为" + MIN_CODE_LENGTH + "-" + MAX_CODE_LENGTH + "之间的整数，实际输入: " + length);
        }
    }
    
    static private String getRandomUUIDSubstring(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, length);
    }
    
    static private void validateCodeAndSize(String code, int width, int height) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("编码内容不能为空");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("宽度和高度必须为正整数");
        }
    }
}