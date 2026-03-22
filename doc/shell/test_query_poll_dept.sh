#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"          # 替换为实际的 appKey
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"    # 替换为实际的 appSecret
bodyMd5=""                          # 如果有 bodyMd5 则填写，否则留空
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

# 调试信息
echo "signContent: $signContent"
echo "signature: $signature"

token_response=$(curl -s -X POST "$baseUrl/auth/oauth2/token" \
  -H "Authorization: Basic REVWSUNFX01HTVRfUExBVEZPUk06RE1QZTdmOGE5YjBjMWQyZTNmNGE1YjZjN2Q4ZTlmMGExYjI=" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=app_key&scope=server&timestamp=$timestamp&nonce=$nonce&signature=$signature")

# 解析 access_token（假设返回的 JSON 中包含 access_token 字段）
access_token=$(echo "$token_response" | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//')
if [ -z "$access_token" ]; then
    echo "获取 token 失败，响应：$token_response"
    exit 1
fi
echo "获取到 access_token: $access_token"

# 2. 拉取消息
echo ""
echo ">>> 开始拉取消息 ..."

# 构造拉取请求体（参数根据实际需要修改）
# 注意：接收人身份证号应与发送消息时使用的 jsrzjhm 一致
request_body='{
    "current": 1,
    "size": 10,
    "fsdx": "DEPT",
    "jsdwdm": "HEB_YJGLT"
}'

echo "请求体：$request_body"

curl -X POST "$baseUrl/ump/open/message/poll" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json" \
  -d "$request_body"

echo ""
echo ">>> 拉取完成"