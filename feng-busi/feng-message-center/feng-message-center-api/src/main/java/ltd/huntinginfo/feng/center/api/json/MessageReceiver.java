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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 消息接收者，从ReceiverScope解析出来的接收者详细信息
 */
@Data
public class MessageReceiver {
    /**
     * 接收者业务系统appKey，根据主表的topic_code查询主题订阅表ump_topic_subscription来获取，注意不是发送者appkey
     */
    private List<String> appKeys;
	
	/**
	 * 接收个人信息详情列表
	 */
	private List<MessageRecipient> recipients;
	
	/**
	 * 接收单位信心详情列表
	 */
	private List<MessageReceivingUnit> units;
	
	/**
	 * 总用户数:含部门成员的数量
	 */
	private Integer totalRecipientCount;
	
	/**
	 * 转换为JSON字符串格式
	 */
	public String toString() {
		return JSONUtil.toJsonPrettyStr(toMap());
	}
	
	/**
	 * 转换为MAP格式
	 * @return
	 */
	public Map<String, Object> toMap() {
		return BeanUtil.beanToMap(toReceivingScope());
	}
	
	/**
	 * 转换为ReceiverScope格式
	 * @return
	 */
	public ReceivingScope toReceivingScope() {
		ScopeReceiver scopeReceiver = new ScopeReceiver();
		scopeReceiver.setLoginIds(new ArrayList<>());
		scopeReceiver.setDeptIds(new ArrayList<>());
		scopeReceiver.setDivisionCodes(new ArrayList<>());
		scopeReceiver.setRoleCodes(new ArrayList<>());
		for(MessageRecipient recipient : recipients) {
			if (BeanUtil.isNotEmpty(recipient)) {
				scopeReceiver.getLoginIds().add(recipient.getReceiverId());
			}
		}
		for(MessageReceivingUnit unit : units) {
			if (BeanUtil.isNotEmpty(unit)) {
				scopeReceiver.getDeptIds().add(unit.getReceivingUnitId());
			}
		}
		
		ReceivingScope receiverScope = new ReceivingScope();
		
		receiverScope.setInclude(scopeReceiver);
		receiverScope.setExclude(new ScopeReceiver());
		return receiverScope;
	}
}
