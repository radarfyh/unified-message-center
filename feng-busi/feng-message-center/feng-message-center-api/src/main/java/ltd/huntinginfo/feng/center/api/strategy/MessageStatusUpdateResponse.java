package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息状态更新响应
 */
@Data
public class MessageStatusUpdateResponse {
    private String code;               // 返回码
    private String info;               // 返回信息
}
