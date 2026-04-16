package ltd.huntinginfo.feng.center.api.strategy;

import lombok.Data;

/**
 * 消息记录
 */
@Data
public class MessageRecord {
    private String fsdw;               // 发送单位
    private String fsdwdm;            // 发送单位代码
    private String fsr;               // 发送人
    private String fsrzjhm;           // 发送人证件号码
    private String fsdx;              // 发送对象
    private String fssj;              // 发送时间
    private String jsdw;              // 接收单位
    private String jsdwdm;            // 接收单位代码
    private String jsr;               // 接收人
    private String jsrzjhm;           // 接收人证件号码
    private String xxbm;              // 消息编码
    private String xxlx;              // 消息类型
    private String xxbt;              // 消息标题
    private String xxnr;              // 消息内容
    private String cldz;              // 处理地址
    private String jjcd;              // 紧急程度
    private String ywcs;              // 业务参数
    private String tb;                // 图标
    private String clzt;              // 处理状态
}