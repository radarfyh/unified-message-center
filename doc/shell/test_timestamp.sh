#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"                # 替换为实际的 appKey
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"    # 替换为实际的 appSecret
bodyMd5=""                                   # 如果有 bodyMd5 则填写，否则留空
baseUrl="http://192.168.137.115:11000"             # 替换为实际的服务地址和端口
# ============================================

# 1. 获取 access_token
echo ">>> 获取 access_token ..."
timestamp=$(date +%s%3N)
nonce=$(openssl rand -hex 8)

if [ -n "$bodyMd5" ]; then
    signContent="${appKey}|${timestamp}|${nonce}|${bodyMd5}"
else
    signContent="${appKey}|${timestamp}|${nonce}"
fi

signature=$(printf "%s" "$signContent" | openssl dgst -sha256 -hmac "$appSecret" -hex | sed 's/^.* //')

echo "signContent: $signContent"
echo "signature: $signature"

token_response=$(curl -s -X POST "$baseUrl/auth/oauth2/token" \
  -H "Authorization: Basic REVWSUNFX01HTVRfUExBVEZPUk06RE1QZTdmOGE5YjBjMWQyZTNmNGE1YjZjN2Q4ZTlmMGExYjI=" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=app_key&scope=server&timestamp=$timestamp&nonce=$nonce&signature=$signature")

access_token=$(echo "$token_response" | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//')
if [ -z "$access_token" ]; then
    echo "获取 token 失败，响应：$token_response"
    exit 1
fi
echo "获取到 access_token: $access_token"

# 2. 获取服务器时间戳（需要携带 token）
echo ""
echo ">>> 获取服务器时间戳 ..."

response=$(curl -s -X GET "$baseUrl/ump/open/getTimestamp" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json")

echo ">>> 响应结果："
if command -v jq &> /dev/null; then
    echo "$response" | jq '.'
else
    echo "$response"
fi

# 可选：提取时间戳并打印
if command -v jq &> /dev/null; then
    timestamp_val=$(echo "$response" | jq -r '.data.xxjl[0].timestamp')
    strDate=$(echo "$response" | jq -r '.data.xxjl[0].strDate')
    if [ "$timestamp_val" != "null" ] && [ -n "$timestamp_val" ]; then
        echo ""
        echo ">>> 服务器时间戳: $timestamp_val ($strDate)"
    fi
fi

echo ""
echo ">>> 完成"