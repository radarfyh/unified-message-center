#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"                # 替换为实际的 appKey
appSecret="DMPe7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2"    # 替换为实际的 appSecret
bodyMd5=""                                   # 如果有 bodyMd5 则填写，否则留空
authMode="standard"                          # 认证模式：standard 或 legacy
caller=""                                    # 调用者AppKey，可选
appType=""                                   # 应用类型，可选
deviceId=""                                  # 设备标识，可选
baseUrl="http://192.168.137.115:11000"             # 替换为实际的服务地址和端口
# ============================================

# 生成时间戳（毫秒）和随机数
timestamp=$(date +%s%3N)
nonce=$(openssl rand -hex 8)

# 构建签名字符串（与 Java 逻辑一致，使用 appSecret）
if [ -n "$bodyMd5" ]; then
    signContent="${appKey}|${timestamp}|${nonce}|${bodyMd5}"
else
    signContent="${appKey}|${timestamp}|${nonce}"
fi

# 计算 HMAC-SHA256 签名（输出十六进制字符串）
signature=$(printf "%s" "$signContent" | openssl dgst -sha256 -hmac "$appSecret" -hex | sed 's/^.* //')

echo ">>> 应用认证请求参数"
echo "appKey: $appKey"
echo "timestamp: $timestamp"
echo "nonce: $nonce"
echo "signature: $signature"
echo "bodyMd5: $bodyMd5"
echo "authMode: $authMode"
echo ""

# 构建请求体 JSON（与 AppKeyAuthRequest 字段对应）
# 注意：json 中字段名必须与 DTO 完全一致
request_body=$(cat <<EOF
{
    "appKey": "$appKey",
    "appSecret": "$appSecret",
    "timestamp": $timestamp,
    "nonce": "$nonce",
    "signature": "$signature",
    "bodyMd5": "$bodyMd5",
    "authMode": "$authMode",
    "caller": "$caller",
    "appType": "$appType",
    "deviceId": "$deviceId"
}
EOF
)

echo ">>> 请求体："
echo "$request_body"
echo ""

# 发送认证请求
echo ">>> 发送认证请求 ..."
response=$(curl -s -X POST "$baseUrl/ump/open/app/authenticate" \
  -H "Content-Type: application/json" \
  -d "$request_body")

# 输出原始响应（美化）
echo ">>> 响应结果："
if command -v jq &> /dev/null; then
    echo "$response" | jq '.'
else
    echo "$response"
fi

# 提取并打印 token（如果成功）
if command -v jq &> /dev/null; then
    success=$(echo "$response" | jq -r '.data.code')
    if [ "$success" = "00000" ]; then
        token=$(echo "$response" | jq -r '.data.xxjl[0].token')
        if [ "$token" != "null" ] && [ -n "$token" ]; then
            echo ""
            echo ">>> 认证成功，获取到的 token: $token"
            echo ">>> token 有效期（秒）: $(echo "$response" | jq -r '.data.xxjl[0].expiresTime')"
        fi
    else
        error_msg=$(echo "$response" | jq -r '.data.info')
        echo ""
        echo ">>> 认证失败：$error_msg"
    fi
fi

echo ""
echo ">>> 认证完成"