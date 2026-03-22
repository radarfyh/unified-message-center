#!/bin/bash

# ========== 请根据实际情况配置以下参数 ==========
appKey="DEVICE_MGMT_PLATFORM"                # 用于获取 token 的 appKey
appSecret="d398c20f2aec4e0d8eabfabac99d4cde"    # 用于获取 token 的 appSecret
bodyMd5=""                                   # 如果有 bodyMd5 则填写，否则留空
baseUrl="http://192.168.137.115:11000"             # 服务地址

# 申请消息编码所需参数（根据 MsgCodingDTO）
agencyCode="SJZ_YJGLJ"                      # 申请单位代码
divisionCode="130100"                       # 所属单位区域代码（行政区划）
agencyName="石家庄市应急管理局"              # 申请单位名称
applicantIdCard="110101199001011234"        # 申请人证件号码
applicantPhone="13800138000"                # 申请人电话
applicantName="张三"                         # 申请人姓名
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

# 2. 申请消息编码
echo ""
echo ">>> 申请消息编码 ..."

cat <<EOF> request_apply_code.json
{
    "agencyCode": "$agencyCode",
    "divisionCode": "$divisionCode",
    "agencyName": "$agencyName",
    "applicantIdCard": "$applicantIdCard",
    "applicantPhone": "$applicantPhone",
    "applicantName": "$applicantName"
}
EOF

echo "请求体："
cat request_apply_code.json

response=$(curl -s -X POST "$baseUrl/ump/open/applyCode" \
  -H "Authorization: Bearer $access_token" \
  -H "Content-Type: application/json" \
  -d @request_apply_code.json)

echo ">>> 响应结果："
if command -v jq &> /dev/null; then
    echo "$response" | jq '.'
else
    echo "$response"
fi

# 可选：提取并打印消息编码、条形码、二维码
if command -v jq &> /dev/null; then
    code=$(echo "$response" | jq -r '.data.xxjl[0].messageCode')
    barcode=$(echo "$response" | jq -r '.data.xxjl[0].txm')
    qrcode=$(echo "$response" | jq -r '.data.xxjl[0].ewm')
    if [ "$code" != "null" ] && [ -n "$code" ]; then
        echo ""
        echo ">>> 申请成功，消息编码: $code"
        echo ">>> 条形码（base64）长度: ${#barcode}"
        echo ">>> 二维码（base64）长度: ${#qrcode}"
    fi
fi

echo ""
echo ">>> 完成"