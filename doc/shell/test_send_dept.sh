#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"          # 替换为实际的 appKey
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"    # 替换为实际的 appSecret
bodyMd5=""                          # 如果有 bodyMd5 则填写，否则留空
hostServer="192.168.137.115"
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

token_response=$(curl -s -X POST "http://$hostServer:11000/auth/oauth2/token" \
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

# 2. 发送消息
echo ""
echo ">>> 开始发送消息 ..."

# 构造发送请求体（参数根据实际需要修改）
cat << EOF > request_send_dept.json
{
    "yybs": "DEVICE_MGMT_PLATFORM",
    "fsdw": "石家庄市应急管理局",
    "fsdwdm": "SJZ_YJGLJ",
    "fsdx": "DEPT",
    "xxlx": "BIZ",
    "xxbt": "关于2026年度预算申报的通知",
    "xxnr": {
      "header": {
        "title": "关于2026年度预算申报的通知",
        "sub_title": "财务处〔2026〕12号"
      },
      "body": [
        "各单位：",
        "为做好2026年度预算编制工作，现将有关事项通知如下。",
        "一、预算申报截止时间为2026年3月31日。",
        "二、请通过财政一体化平台完成申报。",
        "特此通知。"
      ],
      "footer": {
        "org": "财政处",
        "date": "2026-01-10"
      }
    },
    "jsdw": "河北省应急管理厅",
    "jsdwdm": "HEB_YJGLT",
    "cldz": "http://localhost:8089/api/test"
}
EOF

echo "请求体："
cat request_send_dept.json

curl -X POST "http://$hostServer:11000/ump/open/message/send" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json" \
  -d @request_send_dept.json

echo ""
echo ">>> 发送完成"