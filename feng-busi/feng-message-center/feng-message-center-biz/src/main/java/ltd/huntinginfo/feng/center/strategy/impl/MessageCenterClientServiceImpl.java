package ltd.huntinginfo.feng.center.strategy.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.center.api.strategy.*;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyRequest.ApplyPersonInfo;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyRequest.ApplyUnitInfo;
import ltd.huntinginfo.feng.center.config.MinistryMessageCenterProperties;
import ltd.huntinginfo.feng.center.service.UmpSystemLogService;
import ltd.huntinginfo.feng.center.strategy.MessageCenterClientService;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCenterClientServiceImpl implements MessageCenterClientService {

    private final UmpSystemLogService umpSystemLogService;
    private final MinistryMessageCenterProperties ministryMessageCenterProperties;
    
    // 请求超时时间（毫秒）
    private static final int REQUEST_TIMEOUT = 10000;
    // 重试次数
    private static final int MAX_RETRY_COUNT = 3;
    // Token缓存
    private static final Map<String, TokenCache> TOKEN_CACHE = new ConcurrentHashMap<>();
    
    @Override
    public CodeApplyResponse applyMessageCode(String appKey, CodeApplyRequest request) {
        return executeWithLogging("申请消息编码", appKey, () -> {
            String apiUrl = buildApiUrl("ApplyMessageCode");
            JSONObject requestBody = buildCodeApplyRequestBody(request);
            HttpResponse response = sendHttpRequest(apiUrl, getValidMinistryToken(), requestBody);
            return parseCodeApplyResponse(response);
        });
    }
    
    @Override
    public MessageSendResponse sendMessages(String appKey, List<MessageSendRequest> requests) {
        return executeWithLogging("发送消息", appKey, () -> {
            String apiUrl = buildApiUrl("SendMessages");
            JSONArray requestArray = buildSendMessagesRequestBody(appKey, requests);
            HttpResponse response = sendHttpRequest(apiUrl, getValidMinistryToken(), requestArray);
            return parseMessageSendResponse(response);
        });
    }
    
    @Override
    public MessageReceiveResponse receiveMessages(String appKey, MessageReceiveRequest request) {
        return executeWithLogging("接收消息", appKey, () -> {
            String apiUrl = buildApiUrl("ReceiveMessage");
            JSONObject requestBody = buildReceiveMessageRequestBody(request);
            HttpResponse response = sendHttpRequest(apiUrl, getValidMinistryToken(), requestBody);
            return parseMessageReceiveResponse(response);
        });
    }
    
    @Override
    public MessageStatusUpdateResponse updateMessageStatus(String appKey, MessageStatusUpdateRequest request) {
        return executeWithLogging("更新消息状态", appKey, () -> {
            String apiUrl = buildApiUrl("UpdateMessageStatus");
            JSONObject requestBody = buildUpdateMessageStatusRequestBody(request);
            HttpResponse response = sendHttpRequest(apiUrl, getValidMinistryToken(), requestBody);
            return parseMessageStatusUpdateResponse(response);
        });
    }
    
    @Override
    public UnreadMessageResponse queryUnreadMessages(String appKey, UnreadMessageRequest request) {
        return executeWithLogging("查询未读消息", appKey, () -> {
            String apiUrl = buildApiUrl("QueryUnreadMessage");
            JSONObject requestBody = buildQueryUnreadMessageRequestBody(request);
            HttpResponse response = sendHttpRequest(apiUrl, getValidMinistryToken(), requestBody);
            return parseMessageUnreadResponse(response);
        });
    }
    
    /**
     * 通用执行方法，包含日志记录和异常处理
     */
    private <T> T executeWithLogging(String operation, String appKey, BusinessExecutor<T> executor) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        T result = null;
        Exception exception = null;
        String apiUrl = "";
        
        try {
            result = executor.execute();
            return result;
        } catch (Exception e) {
            exception = e;
            throw new RuntimeException(operation + "失败: " + e.getMessage(), e);
        } finally {
            // 统一记录日志
            recordLog(operation, appKey, requestId, apiUrl, result, exception, 
                     System.currentTimeMillis() - startTime);
        }
    }
    
    /**
     * 统一日志记录
     */
    private void recordLog(String operation, String appKey, String requestId, String apiPath,
                          Object result, Exception exception, long costTime) {
        try {
            String logLevel = exception == null ? "INFO" : "ERROR";
            String responseCode = extractResponseCode(result, exception);
            String responseMessage = extractResponseMessage(result, exception);
            Map<String, Object> responseData = extractResponseData(result);
            
            umpSystemLogService.recordOperationLog(
                logLevel, appKey, "system", operation, requestId,
                truncateString(apiPath, 1024), "POST", null,
                responseCode, truncateString(responseMessage, 1024), responseData,
                null, null, null, (int) costTime, 0,
                exception != null ? truncateString(operation + "失败: " + exception.getMessage(), 1024) : null,
                exception != null ? truncateStackTrace(exception) : null
            );
            
            if (exception == null) {
                log.debug("{}成功，请求ID: {}, 耗时: {}ms", operation, requestId, costTime);
            } else {
                log.warn("{}失败，请求ID: {}, 耗时: {}ms, 错误: {}", operation, requestId, costTime, exception.getMessage());
            }
        } catch (Exception e) {
            log.error("记录日志失败", e);
        }
    }
    
    /**
     * 提取响应码
     */
    private String extractResponseCode(Object result, Exception exception) {
        if (exception != null) {
            return "ERROR";
        }
        if (result == null) {
            return "UNKNOWN";
        }
        
        try {
            if (result instanceof CodeApplyResponse) {
                return ((CodeApplyResponse) result).getCode();
            } else if (result instanceof MessageSendResponse) {
                return ((MessageSendResponse) result).getCode();
            } else if (result instanceof MessageReceiveResponse) {
                return ((MessageReceiveResponse) result).getCode();
            } else if (result instanceof MessageStatusUpdateResponse) {
                return ((MessageStatusUpdateResponse) result).getCode();
            } else if (result instanceof UnreadMessageResponse) {
                return ((UnreadMessageResponse) result).getCode();
            }
        } catch (Exception e) {
            log.warn("提取响应码失败", e);
        }
        return "UNKNOWN";
    }
    
    /**
     * 提取响应消息
     */
    private String extractResponseMessage(Object result, Exception exception) {
        if (exception != null) {
            return "失败: " + exception.getMessage();
        }
        if (result == null) {
            return "响应为空";
        }
        
        try {
            if (result instanceof CodeApplyResponse) {
                String info = ((CodeApplyResponse) result).getInfo();
                return StrUtil.isNotBlank(info) ? info : "成功";
            } else if (result instanceof MessageSendResponse) {
                String info = ((MessageSendResponse) result).getInfo();
                return StrUtil.isNotBlank(info) ? info : "成功";
            } else if (result instanceof MessageReceiveResponse) {
                String info = ((MessageReceiveResponse) result).getInfo();
                return StrUtil.isNotBlank(info) ? info : "成功";
            } else if (result instanceof MessageStatusUpdateResponse) {
                String info = ((MessageStatusUpdateResponse) result).getInfo();
                return StrUtil.isNotBlank(info) ? info : "成功";
            } else if (result instanceof UnreadMessageResponse) {
                String info = ((UnreadMessageResponse) result).getInfo();
                return StrUtil.isNotBlank(info) ? info : "成功";
            }
        } catch (Exception e) {
            log.warn("提取响应消息失败", e);
        }
        return "成功";
    }
    
    /**
     * 提取响应数据（简化版，避免存储过多数据）
     */
    private Map<String, Object> extractResponseData(Object result) {
        if (result == null) {
            return null;
        }
        
        try {
            Map<String, Object> data = new HashMap<>();
            
            if (result instanceof CodeApplyResponse) {
                CodeApplyResponse r = (CodeApplyResponse) result;
                data.put("code", r.getCode());
                data.put("info", r.getInfo());
                data.put("jzptjcbm", r.getJzptjcbm());
                // 二维码数据过长，不存储
                // data.put("ewm", r.getEwm() != null ? "存在" : null);
            } else if (result instanceof MessageSendResponse) {
                MessageSendResponse r = (MessageSendResponse) result;
                data.put("code", r.getCode());
                data.put("info", r.getInfo());
            } else if (result instanceof MessageReceiveResponse) {
                MessageReceiveResponse r = (MessageReceiveResponse) result;
                data.put("code", r.getCode());
                data.put("info", r.getInfo());
                data.put("ybid", r.getYbid());
                data.put("messageCount", r.getXxjl() != null ? r.getXxjl().size() : 0);
            } else if (result instanceof MessageStatusUpdateResponse) {
                MessageStatusUpdateResponse r = (MessageStatusUpdateResponse) result;
                data.put("code", r.getCode());
                data.put("info", r.getInfo());
            } else if (result instanceof UnreadMessageResponse) {
                UnreadMessageResponse r = (UnreadMessageResponse) result;
                data.put("code", r.getCode());
                data.put("info", r.getInfo());
                data.put("xxzs", r.getXxzs());
                data.put("messageCount", r.getXxjl() != null ? r.getXxjl().size() : 0);
            }
            
            return data;
        } catch (Exception e) {
            log.warn("提取响应数据失败", e);
            return null;
        }
    }
    
    /**
     * 截断堆栈信息
     */
    private String truncateStackTrace(Exception e) {
        if (e == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append("\n");
        int count = 0;
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("  at ").append(element.toString()).append("\n");
            count++;
            if (count >= 10) {
                sb.append("  ...");
                break;
            }
        }
        return truncateString(sb.toString(), 2000);
    }
    
    /**
     * 获取有效Token，如果缓存中没有或已过期，则重新获取
     */
    private String getValidMinistryToken() {
        String appId = ministryMessageCenterProperties.getAppId();
        TokenCache tokenCache = TOKEN_CACHE.get(appId);
        
        if (tokenCache != null && !tokenCache.isExpired()) {
            log.debug("使用缓存的Token，appKey: {}, 剩余有效期: {}秒", appId, tokenCache.getRemainingSeconds());
            return tokenCache.getToken();
        }
        
        log.info("Token已过期或不存在，重新获取，appKey: {}", appId);
        return fetchNewMinistryToken(appId);
    }
    
    /**
     * 从部警综平台获取新Token
     */
    private String fetchNewMinistryToken(String appId) {
        String tokenUrl = ministryMessageCenterProperties.getTokenUrl();
        
        try {
            String nonce = UUID.randomUUID().toString().replace("-", "");
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            if (StrUtil.isBlank(appId)) {
                appId = ministryMessageCenterProperties.getAppId();
            }
            String appSecret = ministryMessageCenterProperties.getAppSecret();
            String rawString = String.format("appId=%s&appSecret=%s&nonce=%s&timeStamp=%s",
                    appId, appSecret, nonce, timeStamp);
            String digest = SmUtil.sm3(rawString);
            
            JSONObject requestBody = new JSONObject();
            requestBody.set("digest", digest);
            requestBody.set("appId", appId);
            requestBody.set("nonce", nonce);
            requestBody.set("timeStamp", timeStamp);
            
            log.debug("获取Token请求参数: appId={}, nonce={}, timeStamp={}", appId, nonce, timeStamp);
            
            HttpResponse response = sendHttpRequest(tokenUrl, null, requestBody);
            
            JSONObject json = JSONUtil.parseObj(response.body());
            String status = json.getStr("status");
            JSONObject data = json.getJSONObject("data");
            
            if ("200".equals(status) && data != null) {
                String code = data.getStr("code");
                if ("10001".equals(code)) {
                    String token = data.getStr("token");
                    Integer expireIn = data.getInt("expireIn");
                    
                    TokenCache newCache = new TokenCache(token, expireIn - 300);
                    TOKEN_CACHE.put(appId, newCache);
                    
                    log.info("获取Token成功，appKey: {}, 有效期: {}秒", appId, expireIn);
                    return token;
                } else {
                    String message = data.getStr("message", "未知错误");
                    log.error("获取Token失败，返回码: {}, 消息: {}", code, message);
                    throw new RuntimeException("获取Token失败: " + message);
                }
            } else {
                log.error("获取Token失败，状态码: {}, 响应: {}", status, response.body());
                throw new RuntimeException("获取Token失败，状态码: " + status);
            }
        } catch (Exception e) {
            log.error("获取Token异常", e);
            throw new RuntimeException("获取Token失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 截断字符串，避免超过数据库字段长度
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
   
    // ========== 构建请求体方法 ==========
    
    private JSONObject buildCodeApplyRequestBody(CodeApplyRequest request) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("sqssdm", request.getSqssdm());
        
        JSONObject sqdwxx = new JSONObject();
        sqdwxx.set("sqdwdm", request.getSqdwxx().getSqdwdm());
        sqdwxx.set("sqdwmc", request.getSqdwxx().getSqdwmc());
        requestBody.set("sqdwxx", sqdwxx);
        
        JSONObject sqrxx = new JSONObject();
        sqrxx.set("sqrxm", request.getSqrxx().getSqrxm());
        sqrxx.set("sqrzjhm", request.getSqrxx().getSqrzjhm());
        sqrxx.set("sqrdh", request.getSqrxx().getSqrdh());
        requestBody.set("sqrxx", sqrxx);
        
        return requestBody;
    }
    
    private JSONArray buildSendMessagesRequestBody(String appKey, List<MessageSendRequest> requests) {
        JSONArray requestArray = new JSONArray();
        for (MessageSendRequest request : requests) {
            JSONObject message = new JSONObject();
            message.set("fsdw", request.getFsdw());
            message.set("fsdwdm", request.getFsdwdm());
            message.set("fsr", request.getFsr());
            message.set("fsrzjhm", request.getFsrzjhm());
            
            // 修复：应该使用转换后的值
            String fsdx = request.getFsdx();
            String transFsdx = convertSendTargetType(fsdx);
            message.set("fsdx", transFsdx);  // 使用转换后的值
            
            message.set("jsdw", request.getJsdw());
            message.set("jsdwdm", request.getJsdwdm());
            message.set("jsr", request.getJsr());
            message.set("jsrzjhm", request.getJsrzjhm());
            message.set("ztbm", request.getZtbm());
            
            // 申请消息编码
            String msgCode = request.getXxbm();
            if (StrUtil.isBlank(msgCode)) {
            	CodeApplyRequest codeApplyRequest = new CodeApplyRequest();
                ApplyUnitInfo sqdwxx = new ApplyUnitInfo();      // 申请单位信息
                sqdwxx.setSqdwdm(request.getFsdwdm());
                sqdwxx.setSqdwmc(request.getFsdw());
                ApplyPersonInfo sqrxx = new ApplyPersonInfo();     // 申请人信息
                sqrxx.setSqrxm(request.getFsr());
                sqrxx.setSqrzjhm(request.getFsrzjhm());
                sqrxx.setSqrdh(request.getFsrdh());
            	codeApplyRequest.setSqdwxx(null);
            	CodeApplyResponse response = this.applyMessageCode(appKey, codeApplyRequest);
            	if (response != null) {
            		msgCode = response.getJzptjcbm();
            	}
            }
            message.set("xxbm", msgCode);
            
            message.set("xxlx", request.getXxlx());
            message.set("xxbt", request.getXxbt());
            message.set("xxnr", request.getXxnr());
            message.set("cldz", request.getCldz());
            message.set("jjcd", request.getJjcd());
            message.set("ywcs", request.getYwcs());
            message.set("tb", request.getTb());
            requestArray.add(message);
        }
        return requestArray;
    }

    /**
     * 转换发送目标类型
     * 0-个人 1-单位 2-全体
     */
    private String convertSendTargetType(String type) {
        if (MqMessageEventConstants.ReceiverTypes.ALL.equals(type)) {
            return "2";
        } else if (MqMessageEventConstants.ReceiverTypes.DEPT.equals(type)) {
            return "1";
        }
        return "0";
    }
    
    private JSONObject buildReceiveMessageRequestBody(MessageReceiveRequest request) {
        JSONObject requestBody = new JSONObject();
        if (StrUtil.isNotBlank(request.getYbid())) {
            requestBody.set("ybid", request.getYbid());
        }
        if (StrUtil.isNotBlank(request.getZtbm())) {
            requestBody.set("ztbm", request.getZtbm());
        }
        return requestBody;
    }
    
    private JSONObject buildUpdateMessageStatusRequestBody(MessageStatusUpdateRequest request) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("cldw", request.getCldw());
        requestBody.set("cldwdm", request.getCldwdm());
        requestBody.set("clr", request.getClr());
        requestBody.set("clrzjhm", request.getClrzjhm());
        requestBody.set("xxbm", request.getXxbm());
        return requestBody;
    }
    
    private JSONObject buildQueryUnreadMessageRequestBody(UnreadMessageRequest request) {
        JSONObject requestBody = new JSONObject();
        if (request.getCxdwxx() != null) {
            JSONObject cxdwxx = new JSONObject();
            cxdwxx.set("cxdw", request.getCxdwxx().getCxdw());
            cxdwxx.set("cxdwdm", request.getCxdwxx().getCxdwdm());
            requestBody.set("cxdwxx", cxdwxx);
        }
        if (request.getCxrxx() != null) {
            JSONObject cxrxx = new JSONObject();
            cxrxx.set("cxr", request.getCxrxx().getCxr());
            cxrxx.set("cxrzjhm", request.getCxrxx().getCxrzjhm());
            requestBody.set("cxrxx", cxrxx);
        }
        return requestBody;
    }
    
    // ========== 其他工具方法 ==========
    
    @Override
    public String getBaseUrl(Boolean isApplyCode) {
        return buildBaseUrl(isApplyCode);
    }
    
    @Override
    public boolean isMockEnabled() {
    	log.debug("ministryMessageCenterProperties:", ministryMessageCenterProperties);
        return ministryMessageCenterProperties.getMockEnabled();
    }
    
    // 清除Token缓存
    @Override
    public void clearTokenCache() {
        TOKEN_CACHE.clear();
        log.info("Token缓存已清除");
    }
    
    private String buildApiUrl(String apiName) {
        String baseUrl = null;
        String apiPath = null;
        switch(apiName) {
            case "ApplyMessageCode":
            	baseUrl = buildBaseUrl(true);
                apiPath = ministryMessageCenterProperties.getPathApplyMessageCode();
                break;
            case "SendMessages":
            	baseUrl = buildBaseUrl(false);
                apiPath = ministryMessageCenterProperties.getPathSendMessages();
                break;
            case "ReceiveMessage":  
            	baseUrl = buildBaseUrl(false);
                apiPath = ministryMessageCenterProperties.getPathReceiveMessage();
                break;
            case "UpdateMessageStatus": 
            	baseUrl = buildBaseUrl(false);
                apiPath = ministryMessageCenterProperties.getPathUpdateMessageStatus();
                break;
            case "QueryUnreadMessage":  
            	baseUrl = buildBaseUrl(false);
                apiPath = ministryMessageCenterProperties.getPathQueryUnreadMessage();
                break;
            default:
            	baseUrl = "";
                apiPath = "";
        }
        return baseUrl + apiPath;
    }
    
    private String buildBaseUrl(Boolean isApplyCode) {
    	log.debug("ministryMessageCenterProperties:", ministryMessageCenterProperties);
    	
        if (isMockEnabled()) {
        	if (isApplyCode) {
        		return ministryMessageCenterProperties.getMockHomeUrlApplyCode() + "/" + 
                        ministryMessageCenterProperties.getMockResourceApplyCode();
        	}
            return ministryMessageCenterProperties.getMockHomeUrl() + "/" + 
                    ministryMessageCenterProperties.getMockResource();
        } else {
        	if (isApplyCode) {
        		return ministryMessageCenterProperties.getHomeUrlApplyCode() + "/" + 
                        ministryMessageCenterProperties.getResourceApplyCode();
        	}
            return ministryMessageCenterProperties.getHomeUrl() + "/" + 
                    ministryMessageCenterProperties.getResource();
        }
    }
    
   
    private HttpResponse sendHttpRequest(String url, String token, Object body) {
        HttpRequest request = HttpRequest.of(url)
                .method(Method.POST)
                .header("Content-Type", "application/json");
        
        if (token != null) {
            request.header("Token", token);
        }
        
        String strBody = JSONUtil.toJsonStr(body);
        request.body(strBody).timeout(REQUEST_TIMEOUT);
        
        // 打印简洁日志
        log.debug("发送请求: url={}, token={}", url, token != null ? "存在" : "无");

        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            try {
                HttpResponse response = request.execute();
                
                if (response.isOk()) {
                    log.debug("请求成功: url={}, status={}", url, response.getStatus());
                    return response;
                } else if (response.getStatus() == HttpStatus.HTTP_UNAUTHORIZED) {
                    log.warn("Token失效，停止重试: status={}", response.getStatus());
                    throw new RuntimeException("Token失效，状态码: " + response.getStatus());
                } else if (i < MAX_RETRY_COUNT - 1) {
                    log.warn("请求失败，重试 {}/{}: url={}, status={}",
                            i + 1, MAX_RETRY_COUNT, url, response.getStatus());
                    Thread.sleep(1000L * (i + 1));
                } else {
                    throw new RuntimeException("请求失败，状态码: " + response.getStatus() +
                            ", 响应: " + truncateString(response.body(), 500));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("请求被中断", e);
            } catch (Exception e) {
                if (i < MAX_RETRY_COUNT - 1) {
                    log.warn("请求异常，重试 {}/{}: url={}, error={}",
                            i + 1, MAX_RETRY_COUNT, url, e.getMessage());
                    try {
                        Thread.sleep(1000L * (i + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    throw new RuntimeException("请求失败: " + e.getMessage(), e);
                }
            }
        }
        throw new RuntimeException("请求失败，已达到最大重试次数");
    }
    
    // ========== 响应解析方法 ==========
    
    private CodeApplyResponse parseCodeApplyResponse(HttpResponse response) {
        try {
            JSONObject json = JSONUtil.parseObj(response.body());
            CodeApplyResponse result = new CodeApplyResponse();
            
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                result.setCode(data.getStr("code"));
                result.setInfo(data.getStr("info"));
                result.setJzptjcbm(data.getStr("jzptjcbm"));
                result.setEwm(data.getStr("ewm"));
                log.debug("申请消息编码响应: code={}, info={}", result.getCode(), result.getInfo());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }
    
    private MessageSendResponse parseMessageSendResponse(HttpResponse response) {
        try {
            JSONObject json = JSONUtil.parseObj(response.body());
            MessageSendResponse result = new MessageSendResponse();
            
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                result.setCode(data.getStr("code"));
                result.setInfo(data.getStr("info"));
                log.debug("发送消息响应: code={}, info={}", result.getCode(), result.getInfo());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }
    
    private MessageReceiveResponse parseMessageReceiveResponse(HttpResponse response) {
        try {
            JSONObject json = JSONUtil.parseObj(response.body());
            MessageReceiveResponse result = new MessageReceiveResponse();
            
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                result.setCode(data.getStr("code"));
                result.setInfo(data.getStr("info"));
                result.setYbid(data.getStr("ybid"));
                
                JSONArray xxjl = data.getJSONArray("xxjl");
                if (xxjl != null) {
                    List<MessageRecord> records = JSONUtil.toList(xxjl, MessageRecord.class);
                    result.setXxjl(records);
                    log.debug("接收消息响应: code={}, info={}, 消息数={}, ybid={}", 
                             result.getCode(), result.getInfo(), records.size(), result.getYbid());
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }
    
    private MessageStatusUpdateResponse parseMessageStatusUpdateResponse(HttpResponse response) {
        try {
            JSONObject json = JSONUtil.parseObj(response.body());
            MessageStatusUpdateResponse result = new MessageStatusUpdateResponse();
            
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                result.setCode(data.getStr("code"));
                result.setInfo(data.getStr("info"));
                log.debug("更新消息状态响应: code={}, info={}", result.getCode(), result.getInfo());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }
    
    private UnreadMessageResponse parseMessageUnreadResponse(HttpResponse response) {
        try {
            JSONObject json = JSONUtil.parseObj(response.body());
            UnreadMessageResponse result = new UnreadMessageResponse();
            
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                result.setCode(data.getStr("code"));
                result.setInfo(data.getStr("info"));
                result.setXxzs(data.getInt("xxzs"));
                
                JSONArray xxjl = data.getJSONArray("xxjl");
                if (xxjl != null) {
                    List<MessageRecord> records = JSONUtil.toList(xxjl, MessageRecord.class);
                    result.setXxjl(records);
                    log.debug("查询未读消息响应: code={}, info={}, 总数={}, 消息数={}", 
                             result.getCode(), result.getInfo(), result.getXxzs(), records.size());
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
        }
    }
    
    // ========== 内部类 ==========
    
    @FunctionalInterface
    private interface BusinessExecutor<T> {
        T execute() throws Exception;
    }
    
    private static class TokenCache {
        private final String token;
        private final long expireTime; // 过期时间戳（毫秒）
        
        public TokenCache(String token, int expireInSeconds) {
            this.token = token;
            this.expireTime = System.currentTimeMillis() + (expireInSeconds * 1000L);
        }
        
        public String getToken() {
            return token;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() >= expireTime;
        }
        
        public long getRemainingSeconds() {
            long remaining = (expireTime - System.currentTimeMillis()) / 1000;
            return Math.max(0, remaining);
        }
    }
}