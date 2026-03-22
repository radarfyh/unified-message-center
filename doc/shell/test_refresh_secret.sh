#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"                # 要刷新的应用标识
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"    # 当前应用密钥（用于获取 token）
bodyMd5=""                                   # 如果有 bodyMd5 则填写，否则留空
baseUrl="http://192.168.137.115:11000"             # 替换为实际的服务地址和端口
# ============================================

# 1. 获取 access_token（用于认证刷新接口）
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

# 2. 刷新应用密钥
echo ""
echo ">>> 刷新应用密钥 ..."

# 调用刷新接口（PUT 方法），appKey 作为查询参数
curl -X PUT "$baseUrl/ump/open/refreshSecret?appKey=$appKey" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json"

echo ""
echo ">>> 刷新完成"