package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 查询未读消息请求
 */
@Data
public class UnreadMessageRequest {
    private QueryUnitInfo cxdwxx;     // 查询单位信息
    private QueryPersonInfo cxrxx;    // 查询人信息
    private String token;  // 用户登录业务系统时警综平台分配的令牌
    
    @Data
    public static class QueryUnitInfo {
        private String cxdw;          // 查询单位
        private String cxdwdm;        // 查询单位代码
    }
    
    @Data
    public static class QueryPersonInfo {
        private String cxr;           // 查询人
        private String cxrzjhm;       // 查询人证件号码
    }
}