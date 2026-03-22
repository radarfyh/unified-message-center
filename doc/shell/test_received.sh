#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"                # 替换为实际的 appKey
appSecret="DMPe7f8a9b0c1d2e3f4a5b6c7d8e9f0a1b2"    # 替换为实际的 appSecret
bodyMd5=""                                   # 如果有 bodyMd5 则填写，否则留空
messageId="a48888a670a2c8947c5b857f530c3bb8"                  # 替换为要上报的消息ID（从拉取结果中获取）
receiverId="6d7d266f226911f197e7000c2926aaa4"                # 替换为接收者ID（从用户表查询或拉取结果中的 receiverId 字段）
receiverType="USER"                          # 接收者类型：USER（个人）或 DEPT（部门）
broadcastId=""                               # 如果是广播消息，填写广播ID，否则留空
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

# 2. 上报消息已接收
echo ""
echo ">>> 上报消息已接收 ..."

request_body='{
    "messageId": "'"$messageId"'",
    "receiverId": "'"$receiverId"'",
    "receiverType": "'"$receiverType"'",
    "broadcastId": "'"$broadcastId"'"
}'

echo "请求体：$request_body"

curl -X POST "$baseUrl/ump/open/message/received" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json" \
  -d "$request_body"

echo ""
echo ">>> 上报完成"