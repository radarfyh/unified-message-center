#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"
bodyMd5=""
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

echo "signContent: $signContent"
echo "signature: $signature"

token_response=$(curl -s -X POST "http://$hostServer:11000/auth/oauth2/token" \
  -H "Authorization: Basic REVWSUNFX01HTVRfUExBVEZPUk06RE1QZTdmOGE5YjBjMWQyZTNmNGE1YjZjN2Q4ZTlmMGExYjI=" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=app_key&scope=server&timestamp=$timestamp&nonce=$nonce&signature=$signature")

access_token=$(echo "$token_response" | grep -o '"access_token":"[^"]*"' | sed 's/"access_token":"//;s/"//')
if [ -z "$access_token" ]; then
    echo "获取 token 失败，响应：$token_response"
    exit 1
fi
echo "获取到 access_token: $access_token"

# 2. 使用 jq 构建请求体（安全处理 JSON 转义）
echo ""
echo ">>> 开始发送消息（部门）..."

xxnr=$(cat <<'EOF'
为做好2026年度预算编制工作，现将有关事项通知如下
EOF
)

request_body=$(jq -n \
  --arg yybs "DEVICE_MGMT_PLATFORM" \
  --arg fsdw "石家庄市应急管理局" \
  --arg fsdwdm "SJZ_YJGLJ" \
  --arg fsr "张三" \
  --arg fsrzjhm "110101199001011234" \
  --arg fsdx "DEPT" \
  --arg xxlx "BIZ" \
  --arg xxbt "关于2026年度预算申报的通知" \
  --arg xxnr "$xxnr" \
  --arg jsdw "河北省应急管理厅" \
  --arg jsdwdm "HEB_YJGLT" \
  --arg cldz "http://localhost:8089/api/test" \
  '{
    "yybs": $yybs,
    "fsdw": $fsdw,
    "fsdwdm": $fsdwdm,
    "fsr": $fsr,
    "fsrzjhm": $fsrzjhm,
    "fsdx": $fsdx,
    "xxlx": $xxlx,
    "xxbt": $xxbt,
    "xxnr": $xxnr,
    "jsdw": $jsdw,
    "jsdwdm": $jsdwdm,
    "cldz": $cldz
  }')

echo "请求体："
echo "$request_body" > request_send_dept_jp.json
cat request_send_dept_jp.json

curl -X POST "http://$hostServer:11000/ump/open/message/send" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json" \
  -d @request_send_dept_jp.json

echo ""
echo ">>> 发送完成"