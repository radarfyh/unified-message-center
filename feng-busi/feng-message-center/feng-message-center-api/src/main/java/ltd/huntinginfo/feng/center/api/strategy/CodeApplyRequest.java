package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息编码申请请求
 */
@Data
public class CodeApplyRequest {
    private String sqssdm;              // 申请单位行政区划代码
    private ApplyUnitInfo sqdwxx;      // 申请单位信息
    private ApplyPersonInfo sqrxx;     // 申请人信息
    private String token;  // 用户登录业务系统时警综平台分配的令牌
    
    @Data
    public static class ApplyUnitInfo {
        private String sqdwdm;         // 申请单位代码
        private String sqdwmc;         // 申请单位名称
    }
    
    @Data
    public static class ApplyPersonInfo {
        private String sqrxm;          // 申请人姓名
        private String sqrzjhm;        // 申请人证件号码
        private String sqrdh;          // 申请人电话
    }
}
