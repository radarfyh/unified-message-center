package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息接收请求
 */
@Data
public class MessageReceiveRequest {
    private String ybid;               // 查询ID
    private String ztbm;               // 消息主题编码
    private String token;  // 用户登录业务系统时警综平台分配的令牌
}
