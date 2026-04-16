package ltd.huntinginfo.feng.center.api.strategy;

import java.util.List;

import lombok.Data;

/**
 * 查询未读消息响应
 */
@Data
public class UnreadMessageResponse {
    private String code;               // 返回码
    private String info;               // 返回信息
    private Integer xxzs;              // 未读消息总数
    private List<MessageRecord> xxjl; // 消息记录列表
}