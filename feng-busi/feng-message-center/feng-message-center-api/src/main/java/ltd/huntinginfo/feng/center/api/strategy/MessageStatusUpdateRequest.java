package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息状态更新请求
 */
@Data
public class MessageStatusUpdateRequest {
    private String cldw;               // 处理单位
    private String cldwdm;             // 处理单位代码
    private String clr;                // 处理人
    private String clrzjhm;            // 处理人证件号码
    private String xxbm;               // 消息编码
    private String token;  // 用户登录业务系统时警综平台分配的令牌
}