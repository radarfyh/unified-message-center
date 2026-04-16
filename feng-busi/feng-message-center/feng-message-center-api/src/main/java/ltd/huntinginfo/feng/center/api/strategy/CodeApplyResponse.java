package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息编码申请响应
 */
@Data
public class CodeApplyResponse {
    private String code;               // 返回码
    private String info;               // 返回信息
    private String jzptjcbm;           // 警综平台消息编码
    private String ewm;                // 二维码图片(base64)
}