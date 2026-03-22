#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"                # 应用标识
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"  # 应用密钥
bodyMd5=""                                   # 若有 bodyMd5 则填写
receivingUnitCode="HEB_YJGLT"        # 测试用的接收人身份证号
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

# 2. 查询未读消息
echo ""
echo ">>> 查询未读消息 ..."

# 使用 jq 构建请求体，避免转义问题
request_body=$(jq -n \
  --arg cxdwdm "$receivingUnitCode" \
  '{
    "cxdwxx": {
      "cxdw": "河北省应急管理厅",
      "cxdwdm": $cxdwdm
    }
  }')

echo "$request_body" > request_query_unread.json
echo "请求体："
cat request_query_unread.json

curl -X POST "$baseUrl/ump/open/message/unread" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json" \
  -d @request_query_unread.json

echo ""
echo ">>> 查询完成"