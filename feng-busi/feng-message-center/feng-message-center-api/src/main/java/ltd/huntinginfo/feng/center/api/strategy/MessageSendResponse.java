package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息发送响应
 */
@Data
public class MessageSendResponse {
    private String code;               // 返回码
    private String info;               // 返回信息
}