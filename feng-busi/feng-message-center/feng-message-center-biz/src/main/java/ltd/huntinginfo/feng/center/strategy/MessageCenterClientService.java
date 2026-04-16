package ltd.huntinginfo.feng.center.strategy;

import java.util.List;

import ltd.huntinginfo.feng.center.api.strategy.CodeApplyRequest;
import ltd.huntinginfo.feng.center.api.strategy.CodeApplyResponse;
import ltd.huntinginfo.feng.center.api.strategy.MessageReceiveRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageReceiveResponse;
import ltd.huntinginfo.feng.center.api.strategy.MessageSendRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageSendResponse;
import ltd.huntinginfo.feng.center.api.strategy.MessageStatusUpdateRequest;
import ltd.huntinginfo.feng.center.api.strategy.MessageStatusUpdateResponse;
import ltd.huntinginfo.feng.center.api.strategy.UnreadMessageRequest;
import ltd.huntinginfo.feng.center.api.strategy.UnreadMessageResponse;

/**
 * 消息中心客户端服务接口
 * 负责与部级消息中心进行HTTP通信
 */
public interface MessageCenterClientService {
    
    /**
     * 申请消息编码
     */
    CodeApplyResponse applyMessageCode(String appKey, CodeApplyRequest request);
    
    /**
     * 发送消息到消息中心
     */
    MessageSendResponse sendMessages(String appKey, List<MessageSendRequest> requests);
    
    /**
     * 接收消息（轮询）
     */
    MessageReceiveResponse receiveMessages(String appKey, MessageReceiveRequest request);
    
    /**
     * 更新消息状态
     */
    MessageStatusUpdateResponse updateMessageStatus(String appKey, MessageStatusUpdateRequest request);
    
    /**
     * 查询用户未读消息（前20条）
     */
    UnreadMessageResponse queryUnreadMessages(String appKey, UnreadMessageRequest request);
    
    /**
     * 获取部级消息中心基础URL
     * @param isApplyCode 是否申请编码服务
     */
    String getBaseUrl(Boolean isApplyCode);
    
    /**
     * 获取是否使用桩代码
     */
    boolean isMockEnabled();
    
    /**
     * 清除Token缓存（用于测试或强制刷新）
     */
    void clearTokenCache();
}