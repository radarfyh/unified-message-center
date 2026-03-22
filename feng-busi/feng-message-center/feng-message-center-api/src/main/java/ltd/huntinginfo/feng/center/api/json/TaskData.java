/*
 *      Copyright (c) 2018-2025, radarfyh(Edison.Feng) All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: radarfyh(Edison.Feng)
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */
package ltd.huntinginfo.feng.center.api.json;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TaskData {
	/**
	 * 对应消息主表的sender_app_key
	 */
	private String senderAppKey;
	/**
	 * 对应消息主表的id
	 */
//	private String messageId;
	/**
	 * 对应消息主表的msg_code
	 */
	private String msgCode;
	/**
	 * 对应消息主表的title
	 */
	private String title;
	
	/**
	 * 接收者业务系统appKey，根据主表的topic_code查询主题订阅表ump_topic_subscription来获取，注意不是发送者appkey
	 */
	private List<String> receiverAppKeys;
	
    /**
     * 接收者类型，即发送对象类型 
     * USER-个人 DEPT-部门 CUSTOM-自定义 ALL-全体 
     * DEPT:要么Agency机关，要么Org组织/公司，暂时只支持Agency机关
     * CUSTOM：暂时支持USER和DEPT的组合，后续扩展支持USER，DEPT，ORG，DIVISION的组合
     */
    private String receiverType;
	
	/**
	 * 接收个人信息
	 */
	private MessageRecipient recipient;
	/**
	 * 接收单位信息
	 */
	private MessageReceivingUnit unit;

	/**
	 * 接收者范围（非单一接收者）
	 */
	private MessageReceiver receiver;
	/**
	 * 推送模式
	 */
	private String pushMode;
	/**
	 * 回调链接
	 */
	private String callbackUrl;
	/**
	 * 回调配置
	 */
	private String callbackConfig;
	/**
	 * 优先级
	 */
	private String priority;
	/**
	 * 过期时间
	 */
	private String expireTime;
	/**
	 * 发送时间
	 */
	private String sendTime;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 旧状态
	 */
	private String oldStatus;
	/**
	 * 任务ID: ump_msg_queue.id
	 */
	private String taskId;
	
	/**
	 * 线程ID
	 */
	private String workId;
	
	/**
	 * 收件箱ID
	 */
	private List<String> inboxIds;
	/**
	 * 广播ID
	 */
	private List<String> broadcastIds;
	/**
	 * 广播消息接收记录ID
	 */
	private List<BrrId> brrIds;
}
