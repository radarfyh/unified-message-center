package ltd.huntinginfo.feng.center.api.strategy;

import java.util.List;

import lombok.Data;

/**
 * 消息接收响应
 */
@Data
public class MessageReceiveResponse {
    private String code;               // 返回码
    private String info;               // 返回信息
    private String ybid;               // 查询ID
    private List<MessageRecord> xxjl; // 消息记录列表
}