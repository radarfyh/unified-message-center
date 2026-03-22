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
package ltd.huntinginfo.feng.center.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueDeptService;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUniqueUserService;
import ltd.huntinginfo.feng.center.api.json.MessageReceiver;
import ltd.huntinginfo.feng.center.api.json.MessageReceivingUnit;
import ltd.huntinginfo.feng.center.api.json.MessageRecipient;
import ltd.huntinginfo.feng.center.api.dto.MessageReceivingUnitDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageRecipientDTO;
import ltd.huntinginfo.feng.center.api.dto.MessageSendDTO;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgBroadcast;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgInbox;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgMain;
import ltd.huntinginfo.feng.center.api.json.ScopeReceiver;
import ltd.huntinginfo.feng.center.mapper.UmpMsgBroadcastMapper;
import ltd.huntinginfo.feng.center.mapper.UmpMsgInboxMapper;
import ltd.huntinginfo.feng.center.mapper.UmpTopicSubscriptionMapper;
import ltd.huntinginfo.feng.center.api.json.ReceivingScope;
import ltd.huntinginfo.feng.common.core.constant.CommonConstants;
import ltd.huntinginfo.feng.common.core.constant.enums.BusinessEnum;
import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.common.core.mq.MqMessageEventConstants;

/*
 * 消息接收者处理工具
 * 消息接收者关键属性：
 * 1、身份证号码
 * 2、单位代码
 * 3、接收者范围设置
 * 4、接收者类型（即发送对象类型）：
 * 处理流程：
 * 1、当接收者类型为USER时判断身份证号码是否为空，否则判无效，并发送到收件箱
 * 2、当接收者类型为DEPT时判断单位代码是否为空，否则判无效，并发送到广播信息筒
 * 3、当接收者类型为ALL时忽略判断单位代码和身份证号码，改为：暂不支持
 * 4、当接收者类型为CUSTOM时判断接收者范围设置，如果receiver_scope.include.loginIds和receiver_scope.include.deptIds同时为空则判无效，并发送到广播信息筒
 */
@Slf4j
public class ReceiverUtil {
	static public Boolean isValidReceiver(MessageSendDTO sendDTO) {
        if (StrUtil.isBlank(sendDTO.getReceiverIdNumber()) 
        		&& StrUtil.isBlank(sendDTO.getReceivingUnitCode())) {
        	Map<String, Object> scope = sendDTO.getReceivingScope();
        	if (CollUtil.isEmpty(scope)) return false;
        	
        	Map<String, Object> include = (Map<String, Object>) scope.get("include");
        	if (CollUtil.isEmpty(include)) return false;
        	
        	List<String> loginIds = getStringList(include, "loginIds");
        	        	
        	List<String> deptIds = getStringList(include, "deptIds");
        	
        	if (CollUtil.isEmpty(loginIds) && CollUtil.isEmpty(deptIds)) {
        		return false;
        	}            
        }
        return true;
	}
	
	static public ReceivingScope generateScope(Map<String, Object> receivingScope, String receiverIdNumber, String receivingUnitCode, UniqueUser user, GovAgency dept) {
		ReceivingScope scope = null;
		if (receivingScope == null) {
    		ScopeReceiver include = new ScopeReceiver();
            if (StrUtil.isNotBlank(receiverIdNumber)) {
                // 根据身份证号查询用户ID
            	UniqueUser dto = new UniqueUser();
            	dto.setIdCard(receiverIdNumber);
            	if (user != null) {
            		List<String> loginIds = Collections.singletonList(user.getId());            		
            		include.setLoginIds(loginIds);;
            	}
            } else if (StrUtil.isNotBlank(receivingUnitCode)) {
                // 根据单位代码查询部门ID
            	if (dept != null) {
            		List<String> deptIds = Collections.singletonList(dept.getId());
            		include.setDeptIds(deptIds);
            	}
            } else {
            	// 接收者身份证和单位代码不能都为空，此处不处理，应前置
            }
            scope = new ReceivingScope();
            scope.setInclude(include);
            ScopeReceiver exclude = new ScopeReceiver();
            exclude.setDeptIds(new ArrayList<>());
            exclude.setLoginIds(new ArrayList<>());
            scope.setExclude(exclude);
            
            return scope;
        } else {        	
        	scope = BeanUtil.toBean(receivingScope, ReceivingScope.class);
        }
		
        log.debug("generateScope receivingScope: {}, user: {}, dept: {}, scope: {}", receivingScope, user, dept, scope);
        return scope;
	}
	
	static public Map<String, Object> generateScope(String receiverIdNumber, String receivingUnitCode, Map<String, Object> receivingScope,
			RemoteUniqueUserService remoteUniqueUserService, RemoteUniqueDeptService remoteUniqueDeptService) {
        // 构建 receivingScope
        Map<String, Object> scopeMap = new HashMap<>();
        Map<String, Object> include = new HashMap<>();
        Map<String, Object> exclude = new HashMap<>();
        List<String> ids = new ArrayList<>();
        
        // 有个人则看个人，否则看单位，再则看范围
        if (StrUtil.isNotBlank(receiverIdNumber)) {
            // 根据身份证查询用户
            Map<String, Object> query = new HashMap<>();
            query.put("idCard", receiverIdNumber);
            Map<String, Object> userResult = remoteUniqueUserService.getDetailByQuery(query);
            if (userResult != null && userResult.size() > 0) {
                String userId = (String) userResult.get("id");
                ids.add(userId);
                include.put("loginIds", ids);
            } else {
                log.warn("未找到身份证对应的用户: {}", receiverIdNumber);
                throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "身份证不正确");
            }
        } else if (StrUtil.isNotBlank(receivingUnitCode)) {
            // 根据单位代码查询部门
            GovAgency deptResult = remoteUniqueDeptService.getAgencyByCode(receivingUnitCode);
            if (deptResult != null) {
                String deptId = deptResult.getId();
                ids.add(deptId);
                include.put("deptIds", ids);
            } else {
                log.warn("未找到单位代码对应的部门: {}", receivingUnitCode);
                throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "接收单位代码不正确");
            }
        } else if (CollUtil.isNotEmpty(receivingScope)) {
        	// 如果请求中没有个人也没有单位，但带了 receivingScope，则使用范围接收
            try {
                Map<String, Object> reqScope = receivingScope;
                Map<String, Object> reqInclude = (Map<String, Object>) reqScope.getOrDefault("include", new HashMap<>());
                include = reqInclude; // 覆盖
                Map<String, Object> reqExclude = (Map<String, Object>) reqScope.getOrDefault("exclude", new HashMap<>());
                exclude = reqExclude; // 覆盖

            } catch (Exception e) {
                log.warn("解析 receivingScope 失败", e);
                throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "接收范围不正确");
            }
        }
        scopeMap.put("include", include.isEmpty() ? Collections.emptyMap() : include);
        scopeMap.put("exclude", exclude.isEmpty() ? Collections.emptyMap() : exclude);
        return scopeMap;
	}
	
	static public ReceivingScope generateScope(String ReceiverIdNumber, UniqueUser user) {
		ReceivingScope scope = null;
    		
        if (StrUtil.isNotBlank(ReceiverIdNumber)) {
        	ScopeReceiver include = new ScopeReceiver();
            // 根据身份证号查询用户ID
        	UniqueUser dto = new UniqueUser();
        	dto.setIdCard(ReceiverIdNumber);
        	if (user != null) {
        		List<String> loginIds = Collections.singletonList(user.getId());            		
        		include.setLoginIds(loginIds);
        		include.setDeptIds(new ArrayList<>());
                scope = new ReceivingScope();
                scope.setInclude(include);
                ScopeReceiver exclude = new ScopeReceiver();
                exclude.setDeptIds(new ArrayList<>());
                exclude.setLoginIds(new ArrayList<>());
                scope.setExclude(exclude);
        	}
        }
	
        log.debug("generateScope ReceiverIdNumber: {}, user: {}, scope: {}", ReceiverIdNumber, user, scope);
        return scope;
	}
	
	static public ReceivingScope generateScope(String receivingUnitCode, GovAgency dept) {
		ReceivingScope scope = null;

		if (StrUtil.isNotBlank(receivingUnitCode)) {
			ScopeReceiver include = new ScopeReceiver();
            // 根据单位代码查询部门ID
        	if (dept != null) {
        		List<String> deptIds = Collections.singletonList(dept.getId());
        		include.setDeptIds(deptIds);
        		include.setLoginIds(new ArrayList<>());
                scope = new ReceivingScope();
                scope.setInclude(include);
                ScopeReceiver exclude = new ScopeReceiver();
                exclude.setDeptIds(new ArrayList<>());
                exclude.setLoginIds(new ArrayList<>());
                scope.setExclude(exclude);
        	}
        } 

		log.debug("generateScope receivingUnitCode: {}, dept: {}, scope: {}", receivingUnitCode, dept, scope);
        return scope;
	}
	
	static public MessageReceiver resolveReceivers(String receiverType, ReceivingScope receivingScope, RemoteUniqueUserService remoteUserService, List<String> appKeys) {
    	if (!receiverType.equals(MqMessageEventConstants.ReceiverTypes.CUSTOM)) {
    		return null;
    	}
		
		if (BeanUtil.isEmpty(receivingScope)) {
            log.error("接收者范围为空");
            throw new BusinessException(BusinessEnum.UMP_TYPE_INVALID.getCode(), "接收者范围不能为空");
        } 

		MessageReceiver messageReceiver = null;
        try {
        	ScopeReceiver include = receivingScope.getInclude();
        	ScopeReceiver exclude = receivingScope.getExclude();

            // 根据 ID 列表获取接收者详情
            if (MqMessageEventConstants.ReceiverTypes.ALL.equals(receiverType)) {
            	messageReceiver = resolveAllReceivers(include, remoteUserService, appKeys);
            } else {
            	messageReceiver = fetchReceiversByIds(receiverType, include, remoteUserService, appKeys);
            }

            // 应用排除过滤
            filterReceivers(messageReceiver, exclude);
        } catch (Exception e) {
            log.error("解析接收者失败，receiverType: {}, receivingScope: {}", receiverType, receivingScope, e);
            throw new RuntimeException("解析接收者失败: " + e.getMessage(), e);
        }
        if (messageReceiver == null) {
            messageReceiver = new MessageReceiver();
            messageReceiver.setRecipients(new ArrayList<>());
            messageReceiver.setTotalRecipientCount(0);
        }
        return messageReceiver;
    }
    
	static public MessageReceiver fetchReceiversByIds(String receiverType, ScopeReceiver include, 
			RemoteUniqueUserService remoteUserService, List<String> appKeys) {
		MessageReceiver messageReceiver = null;
        try {
            // 使用 Set 对用户ID去重，避免重复添加同一用户
            Set<String> distinctUserIds = new HashSet<>();
            List<String> deptIds = include.getDeptIds();      
            switch (receiverType) {
            case MqMessageEventConstants.ReceiverTypes.USER:
	            // 1. 直接指定的用户ID列表
            	List<String> loginIds = include.getLoginIds();
	            if (CollUtil.isNotEmpty(loginIds)) {
	            	messageReceiver = fetchUsersByIds(loginIds, distinctUserIds, remoteUserService, appKeys);   
	            	// 超过阈值就不再继续了
	            	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
	            		return messageReceiver;
	            	}
	            }
	            // 5. 角色代码列表下的所有用户
	            List<String> roleCodes = include.getRoleCodes();
	            if (CollUtil.isNotEmpty(roleCodes)) { 
	            	MessageReceiver mrByRoles = fetchUsersByRoleCodes(roleCodes, distinctUserIds, remoteUserService, appKeys);
	            	if (messageReceiver == null) {
	            		messageReceiver = mrByRoles;
	                	// 超过阈值就不再继续了
	                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
	                		return messageReceiver;
	                	}
	            	} else {
		            	// 超过阈值就不再继续了
		            	if (mrByRoles.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
		            		return messageReceiver;
		            	}
		            	messageReceiver.getRecipients().addAll(mrByRoles.getRecipients());
		            	messageReceiver.setTotalRecipientCount(mrByRoles.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount());
	            	}
	            }
	            break;
            case MqMessageEventConstants.ReceiverTypes.DEPT:
	            // 2. 部门ID列表下的所有用户
                if (CollUtil.isNotEmpty(deptIds)) { 
                	messageReceiver = fetchUsersByDeptIds(deptIds, distinctUserIds, remoteUserService, appKeys);
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
	            }
	            break;
            case MqMessageEventConstants.ReceiverTypes.ORG:
	            // 3. 组织代码列表下的所有用户
                if (CollUtil.isNotEmpty(deptIds)) {    
                	messageReceiver = fetchUsersByOrgCodes(deptIds, distinctUserIds, remoteUserService, appKeys);
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
	            }
	            break;
            case MqMessageEventConstants.ReceiverTypes.AREA:
	            // 4. 行政区划代码列表下的所有用户
                List<String> divisionCodes = include.getDivisionCodes();
                if (CollUtil.isNotEmpty(divisionCodes)) {    
                	messageReceiver = fetchUsersByDivisionCodes(divisionCodes, distinctUserIds, remoteUserService, appKeys);
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
	            }
	            break;

	        default:
	        	log.warn("未知的接收者类型: {}", receiverType);
            }
        } catch (Exception e) {
            log.error("解析接收者失败",  e);
            throw new RuntimeException("解析全体接收者失败: " + e.getMessage(), e);
        }
        return messageReceiver;
    }
    
    /**
     * 解析全体接收者（RECEIVER_TYPE = ALL）
     * 根据 receivingScope 中的 include 条件，聚合所有匹配的用户
     */
	static public MessageReceiver resolveAllReceivers(ScopeReceiver include, RemoteUniqueUserService remoteUserService, List<String> appKeys) {
        MessageReceiver messageReceiver = null;
        try {
            // 使用 Set 对用户ID去重，避免重复添加同一用户
            Set<String> distinctUserIds = new HashSet<>();

            // 1. 直接指定的用户ID列表
            List<String> loginIds = include.getLoginIds();
            if (CollUtil.isNotEmpty(loginIds)) {                
            	messageReceiver = fetchUsersByIds(loginIds, distinctUserIds, remoteUserService, appKeys);  
            	// 超过阈值就不再继续了
            	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
            		return messageReceiver;
            	}
            }

            // 2. 部门ID列表下的所有用户
            List<String> deptIds = include.getDeptIds();
            if (CollUtil.isNotEmpty(deptIds)) {                
            	MessageReceiver mrByDepts = fetchUsersByDeptIds(deptIds, distinctUserIds, remoteUserService, appKeys);
            	if (messageReceiver == null) {
            		messageReceiver = mrByDepts;
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
            	} else {
	            	// 超过阈值就不再继续了
	            	if (mrByDepts.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
	            		return messageReceiver;
	            	}
	            	messageReceiver.getRecipients().addAll(mrByDepts.getRecipients());
	            	messageReceiver.setTotalRecipientCount(mrByDepts.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount());
            	}
            }

            // 3. 组织代码列表下的所有用户
            if (CollUtil.isNotEmpty(deptIds)) {                
            	MessageReceiver mrByOrgs = fetchUsersByOrgCodes(deptIds, distinctUserIds, remoteUserService, appKeys);
            	if (messageReceiver == null) {
            		messageReceiver = mrByOrgs;
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
            	} else {
	            	// 超过阈值就不再继续了
	            	if (mrByOrgs.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
	            		return messageReceiver;
	            	}
	            	messageReceiver.getRecipients().addAll(mrByOrgs.getRecipients());
	            	messageReceiver.setTotalRecipientCount(mrByOrgs.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount());
            	}
            }

            // 4. 行政区划代码列表下的所有用户
            List<String> divisionCodes = include.getDivisionCodes();
            if (CollUtil.isNotEmpty(divisionCodes)) {                
            	MessageReceiver mrByDivisions = fetchUsersByDivisionCodes(divisionCodes, distinctUserIds, remoteUserService, appKeys);
            	if (messageReceiver == null) {
            		messageReceiver = mrByDivisions;
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
            	} else {
	            	// 超过阈值就不再继续了
	            	if (mrByDivisions.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
	            		return messageReceiver;
	            	}
	            	messageReceiver.getRecipients().addAll(mrByDivisions.getRecipients());
	            	messageReceiver.setTotalRecipientCount(mrByDivisions.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount());
            	}
            }

            // 5. 角色代码列表下的所有用户
            List<String> roleCodes = include.getRoleCodes();
            if (CollUtil.isNotEmpty(roleCodes)) {                
            	MessageReceiver mrByRoles = fetchUsersByRoleCodes(roleCodes, distinctUserIds, remoteUserService, appKeys);
            	if (messageReceiver == null) {
            		messageReceiver = mrByRoles;
                	// 超过阈值就不再继续了
                	if (messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
                		return messageReceiver;
                	}
            	} else {
	            	// 超过阈值就不再继续了
	            	if (mrByRoles.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
	            		return messageReceiver;
	            	}
	            	messageReceiver.getRecipients().addAll(mrByRoles.getRecipients());
	            	messageReceiver.setTotalRecipientCount(mrByRoles.getTotalRecipientCount() + messageReceiver.getTotalRecipientCount());
            	}
            }
        } catch (Exception e) {
            log.error("解析全体接收者失败",  e);
            throw new RuntimeException("解析全体接收者失败: " + e.getMessage(), e);
        }
        return messageReceiver;
    }
    
    
	static public void filterReceivers(MessageReceiver receivers, ScopeReceiver exclude) {
        if (CollectionUtils.isEmpty(receivers.getRecipients()) || exclude == null) {
            return;
        }
        
        // 将可能为 null 的列表转为安全的空列表
        List<String> excludeLoginIds = exclude.getLoginIds() != null ? exclude.getLoginIds() : Collections.emptyList();
        List<String> excludeDeptIds = exclude.getDeptIds() != null ? exclude.getDeptIds() : Collections.emptyList();
        List<String> excludeRoleCodes = exclude.getRoleCodes() != null ? exclude.getRoleCodes() : Collections.emptyList();
        
        // 如果所有排除列表都为空，则无需过滤
        if (excludeLoginIds.isEmpty() && excludeDeptIds.isEmpty() && excludeRoleCodes.isEmpty()) {
            return;
        }
        
        List<MessageRecipient> filtered = receivers.getRecipients().stream()
                .filter(receiver -> {
                    String loginId = receiver.getReceiverId();
                    if (excludeLoginIds.contains(loginId)) {
                        return false;
                    }
                    String deptId = receiver.getReceivingUnitId();
                    if (deptId != null && excludeDeptIds.contains(deptId)) {
                        return false;
                    }
                    List<String> roleCodes = getStringList(receiver.getRoleCodes());
                    if (roleCodes != null && !Collections.disjoint(roleCodes, excludeRoleCodes)) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        receivers.setRecipients(filtered);
    }

	static public Set<String> getExcludeSet(Map<String, Object> exclude, String key) {
        Object value = exclude.get(key);
        if (value instanceof List) {
            return new HashSet<>((List<String>) value);
        }
        return Collections.emptySet();
    }
    
	static public MessageRecipient createUserReceiver(UniqueUser user, List<String> appKeys) {
        if (BeanUtil.isEmpty(user)) {
            return null;
        }
        String userId = user.getId();
        String username = user.getLoginId();
        if (!StringUtils.hasText(userId)) {
            log.warn("用户信息缺少ID字段: {}", user);
            return null;
        }
        MessageRecipient receiver = new MessageRecipient();
        receiver.setReceiverId(userId);
        receiver.setLoginId(username);
        receiver.setReceiverType(MqMessageEventConstants.ReceiverTypes.USER);
        receiver.setReceiverName(user.getName());
        receiver.setReceiverIdNumber(user.getIdCard());
        receiver.setAppKeys(appKeys);
        
        if (StrUtil.isNotBlank(user.getAgencyCode())) {
        	// 机关
            receiver.setReceivingUnitCode(user.getAgencyCode());
        } else {
        	// 组织/公司
        	receiver.setReceivingUnitCode(user.getUniqueOrgCode());
        }
        if (StrUtil.isNotBlank(user.getUniqueRoles())) {
        	// 角色
            receiver.setRoleCodes(user.getUniqueRoles());
        }
        if (StrUtil.isNotBlank(user.getDivisionCode())) {
        	// 区域
            receiver.setDivisionCode(user.getDivisionCode());
        }
        return receiver;
    }
	
    // 根据用户ID列表获取用户（去重）
	static public MessageReceiver fetchUsersByIds(List<String> userIds, Set<String> distinctUserIds, 
			RemoteUniqueUserService remoteUserService, List<String> appKeys) {		
		MessageReceiver messageReceiver = new MessageReceiver();
    	messageReceiver.setTotalRecipientCount(0);
    	messageReceiver.setRecipients(null);
    	messageReceiver.setUnits(null);    	

        if (CollectionUtils.isEmpty(userIds)) return messageReceiver;
        
        List<MessageRecipient> users = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += CommonConstants.BATCH_QUERY_SIZE) {
            int end = Math.min(i + CommonConstants.BATCH_QUERY_SIZE, userIds.size());
            List<String> batch = userIds.subList(i, end);
            List<Map<String, Object>> result = remoteUserService.listByIds(batch);
            if (result != null && result.size() > 0) {
                for (Map<String, Object> user : result) {
                	UniqueUser uniqueUser = BeanUtil.toBean(user, UniqueUser.class);
                    String userId = uniqueUser.getId();
                    if (!distinctUserIds.contains(userId)) {
                        distinctUserIds.add(userId);
                        users.add(createUserReceiver(uniqueUser, appKeys));
                    }
                }
            } else {
                log.warn("根据用户ID列表获取用户失败，batch: {}, result: {}", batch, result);
            }
        }
        
        messageReceiver.setRecipients(users);
        return messageReceiver;
    }

    // 根据部门ID列表获取所有用户
	static public MessageReceiver fetchUsersByDeptIds(
			List<String> deptIds, 
			Set<String> distinctUserIds, 
			RemoteUniqueUserService remoteUserService, 
			List<String> appKeys) {	
		MessageReceiver messageReceiver = new MessageReceiver();
    	messageReceiver.setTotalRecipientCount(0);
    	messageReceiver.setRecipients(null);
    	messageReceiver.setUnits(null);
        if (CollectionUtils.isEmpty(deptIds)) {
        	return messageReceiver;
        }
        
        List<MessageRecipient> users = new ArrayList<>();

        for (int i = 0; i < deptIds.size(); i += CommonConstants.BATCH_QUERY_SIZE) {
            int end = Math.min(i + CommonConstants.BATCH_QUERY_SIZE, deptIds.size());
            List<String> batch = deptIds.subList(i, end);
            
            Integer count = remoteUserService.countByDeptIds(batch);
            messageReceiver.setTotalRecipientCount(messageReceiver.getTotalRecipientCount() + count);
            
            List<Map<String, Object>> result = remoteUserService.listByDeptIds(batch);
            if (result != null && result.size() > 0) {
                for (Map<String, Object> user : result) {
                	UniqueUser uniqueUser = BeanUtil.toBean(user, UniqueUser.class);
                    String userId = uniqueUser.getId();
                    if (!distinctUserIds.contains(userId)) {
                        distinctUserIds.add(userId);
                        users.add(createUserReceiver(uniqueUser, appKeys));
                    }
                }
            } else {
                log.warn("根据部门ID列表获取用户失败，batch: {}, result: {}", batch, result);
            }
            
            // 大于广播阈值，将会转为广播，不继续处理
            if (users.size() > MqMessageEventConstants.Thresholds.BROADCAST_THRESHOLD) {
            	break;
            }
        }
        
        messageReceiver.setRecipients(users);
        return messageReceiver;
    }

    // 根据组织代码列表获取所有用户
	static public MessageReceiver fetchUsersByOrgCodes(List<String> orgCodes, Set<String> distinctUserIds, 
			RemoteUniqueUserService remoteUserService, List<String> appKeys) {
		MessageReceiver messageReceiver = new MessageReceiver();
    	messageReceiver.setTotalRecipientCount(0);
    	messageReceiver.setRecipients(null);
    	messageReceiver.setUnits(null);

        if (CollectionUtils.isEmpty(orgCodes)) return messageReceiver;
        
        List<MessageRecipient> users = new ArrayList<>();

        for (int i = 0; i < orgCodes.size(); i += CommonConstants.BATCH_QUERY_SIZE) {
            int end = Math.min(i + CommonConstants.BATCH_QUERY_SIZE, orgCodes.size());
            List<String> batch = orgCodes.subList(i, end);
            
            Integer count = remoteUserService.countByOrgCodes(batch);
            messageReceiver.setTotalRecipientCount(messageReceiver.getTotalRecipientCount() + count);
            
            List<Map<String, Object>> result = remoteUserService.listByOrgCodes(batch);
            if (result != null && result.size() > 0) {
                for (Map<String, Object> user : result) {
                	UniqueUser uniqueUser = BeanUtil.toBean(user, UniqueUser.class);
                    String userId = uniqueUser.getId();
                    if (!distinctUserIds.contains(userId)) {
                        distinctUserIds.add(userId);
                        users.add(createUserReceiver(uniqueUser, appKeys));
                    }
                }
            } else {
                log.warn("根据组织代码列表获取用户失败，batch: {}, result: {}", batch, result);
            }
        }
        
        messageReceiver.setRecipients(users);
        return messageReceiver;
    }

    // 根据行政区划代码列表获取所有用户
	static public MessageReceiver fetchUsersByDivisionCodes(List<String> divisionCodes, Set<String> distinctUserIds, 
			RemoteUniqueUserService remoteUserService, List<String> appKeys) {
		MessageReceiver messageReceiver = new MessageReceiver();
    	messageReceiver.setTotalRecipientCount(0);
    	messageReceiver.setRecipients(null);
    	messageReceiver.setUnits(null);    	
        
        if (CollectionUtils.isEmpty(divisionCodes)) return messageReceiver;
        
        List<MessageRecipient> users = new ArrayList<>();

        for (int i = 0; i < divisionCodes.size(); i += CommonConstants.BATCH_QUERY_SIZE) {
            int end = Math.min(i + CommonConstants.BATCH_QUERY_SIZE, divisionCodes.size());
            List<String> batch = divisionCodes.subList(i, end);
            
            Integer count = remoteUserService.countByDivisionCodes(batch);
            messageReceiver.setTotalRecipientCount(messageReceiver.getTotalRecipientCount() + count);
            
            List<Map<String, Object>> result = remoteUserService.listByDivisionCodes(batch);
            if (result != null && result.size() > 0) {
                for (Map<String, Object> user : result) {
                	UniqueUser uniqueUser = BeanUtil.toBean(user, UniqueUser.class);
                    String userId = uniqueUser.getId();
                    if (!distinctUserIds.contains(userId)) {
                        distinctUserIds.add(userId);
                        users.add(createUserReceiver(uniqueUser, appKeys));
                    }
                }
            } else {
                log.warn("根据行政区划代码列表获取用户失败，batch: {}, result: {}", batch, result);
            }
        }
        
        messageReceiver.setRecipients(users);
        return messageReceiver;
    }

    // 根据角色代码列表获取所有用户
	static public MessageReceiver fetchUsersByRoleCodes(List<String> roleCodes, Set<String> distinctUserIds, 
			RemoteUniqueUserService remoteUserService, List<String> appKeys) {
		MessageReceiver messageReceiver = new MessageReceiver();
    	messageReceiver.setTotalRecipientCount(0);
    	messageReceiver.setRecipients(null);
    	messageReceiver.setUnits(null);       	

        if (CollectionUtils.isEmpty(roleCodes)) return messageReceiver;
        
        List<MessageRecipient> users = new ArrayList<>();

        for (int i = 0; i < roleCodes.size(); i += CommonConstants.BATCH_QUERY_SIZE) {
            int end = Math.min(i + CommonConstants.BATCH_QUERY_SIZE, roleCodes.size());
            List<String> batch = roleCodes.subList(i, end);
            
            Integer count = remoteUserService.countByRoleCodes(batch);
            messageReceiver.setTotalRecipientCount(messageReceiver.getTotalRecipientCount() + count);
            
            List<Map<String, Object>> result = remoteUserService.listByRoleCodes(batch);
            if (result != null && result.size() > 0) {
                for (Map<String, Object> user : result) {
                	UniqueUser uniqueUser = BeanUtil.toBean(user, UniqueUser.class);
                    String userId = uniqueUser.getId();
                    if (!distinctUserIds.contains(userId)) {
                        distinctUserIds.add(userId);
                        users.add(createUserReceiver(uniqueUser, appKeys));
                    }
                }
            } else {
                log.warn("根据角色代码列表获取用户失败，batch: {}, result: {}", batch, result);
            }
        }
        
        messageReceiver.setRecipients(users);
        return messageReceiver;
    }
    
	static public String determineBroadcastType(String receiverType) {
        if (MqMessageEventConstants.ReceiverTypes.ALL.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.ALL;
        } else if (MqMessageEventConstants.ReceiverTypes.AREA.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.AREA;
        } else if (MqMessageEventConstants.ReceiverTypes.ORG.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.ORG;
        } else if (MqMessageEventConstants.ReceiverTypes.DEPT.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.DEPT;
        } else if (MqMessageEventConstants.ReceiverTypes.CUSTOM.equals(receiverType)) {
            return MqMessageEventConstants.BroadcastTypes.CUSTOM;
        } else {
            return MqMessageEventConstants.BroadcastTypes.ALL;
        }
    }


	static public String generateTargetDescription(String receiverType, ReceivingScope receivingScope) {
        if (BeanUtil.isEmpty(receivingScope)) {
            log.error("接收者范围为空");
            throw new BusinessException(BusinessEnum.UMP_TYPE_INVALID.getCode(), "接收者范围不能为空");
        }
        
        switch (receiverType) {
            case MqMessageEventConstants.ReceiverTypes.USER:
            	List<String> userIds = receivingScope.getInclude().getLoginIds();
                if (CollUtil.isNotEmpty(userIds)) {
                    return "指定用户: " + userIds.size() + "人";
                }
                break;
            case MqMessageEventConstants.ReceiverTypes.DEPT:
            	List<String> deptIds = receivingScope.getInclude().getDeptIds();
                if (CollUtil.isNotEmpty(deptIds)) {
                    return "指定部门: " + deptIds.size() + "个";
                }
                break;
            case MqMessageEventConstants.ReceiverTypes.ALL:
                return "全体用户";
        }
        return "自定义接收者";
    }
	
	/**
	 * 从 Map 中安全获取 String 列表
	 * @param map 源 Map（可能为 null）
	 * @param key 要获取的键
	 * @return 非 null 的 List&lt;String&gt;，如果值不存在、类型不匹配或元素不是 String 类型，则返回空列表（不会包含 null 元素）
	 */
	public static List<String> getStringList(Map<String, Object> map, String key) {
	    if (map == null) {
	        return Collections.emptyList();
	    }
	    Object value = map.get(key);
	    return getStringList(value);
	}
	
	public static List<String> getStringList(Object value) {
	    if (value instanceof List<?>) {
	        return ((List<?>) value).stream()
	                .filter(String.class::isInstance)
	                .map(String.class::cast)
	                .collect(Collectors.toList());
	    }
	    return Collections.emptyList();
	}
	
	static public MessageReceivingUnitDTO buildReceivingUnit(String sendTargetType, String receivingUnitCode, 
			RemoteUniqueUserService remoteUniqueUserService, RemoteUniqueDeptService remoteUniqueDeptService) {
    	if (!sendTargetType.equals(MqMessageEventConstants.ReceiverTypes.DEPT)) {
    		return null;
    	}
    	if (StrUtil.isBlank(receivingUnitCode)) {
    		log.error("buildReceivingUnit 入参receivingUnitCode为空");
    		return null;
    	}    	
    	MessageReceivingUnitDTO unit = new MessageReceivingUnitDTO();

    	unit.setReceiverType(sendTargetType); // 接收者类型（USER/DEPT/CUSTOM/ALL）
        
    	GovAgency dept = null;
    	String agencyCode = receivingUnitCode;
    	if (StrUtil.isNotBlank(agencyCode)) {
    		dept = remoteUniqueDeptService.getAgencyByCode(agencyCode);
    		log.debug("buildUnitFromDTO dept: {}", dept);
    		if (dept != null) {
	    		unit.setReceivingUnitId(dept.getId());
	    		unit.setReceivingUnitCode(dept.getCode());
	    		unit.setReceivingUnitName(dept.getName());
	    		
	    		// 获取数量
	    		List<String> listDepts = new ArrayList<>();
	    		listDepts.add(dept.getId());
	    		Integer count = remoteUniqueUserService.countByDeptIds(listDepts);
	    		unit.setMemberCount(count);
    		} else {
    			log.warn("接收者部门 [{}] 不存在", agencyCode);
    		}
    	}

        return unit;
    }
    
	static public MessageRecipientDTO buildRecipient(String sendTargetType, String receiverIdNumber, 
			RemoteUniqueUserService remoteUniqueUserService, RemoteUniqueDeptService remoteUniqueDeptService) {
    	if (!sendTargetType.equals(MqMessageEventConstants.ReceiverTypes.USER)) {
    		return null;
    	}
    	if (StrUtil.isBlank(receiverIdNumber)) {
    		log.error("buildRecipient 入参receiverIdNumber为空");
    		return null;
    	}    	
    	MessageRecipientDTO recipient = new MessageRecipientDTO();

    	recipient.setReceiverType(sendTargetType); // 接收者类型（USER/DEPT/CUSTOM/ALL）
        
        // 根据身份证号查询用户ID
    	UniqueUser dto = new UniqueUser();
    	dto.setIdCard(receiverIdNumber);
    	UniqueUser user = remoteUniqueUserService.info(dto);
    	
    	log.debug("buildRecipientFromDTO user: {}", user);
    	if (user == null) {
    		log.warn("接收者身份证号 [{}] 对应的用户不存在", receiverIdNumber);
    	    //throw new BusinessException(BusinessEnum.UMP_ID_CARD_INVALID.getCode(), "接收者身份证号 [" + sendDTO.getReceiverIdNumber() + "] 对应的用户不存在");
    	} else {
        	recipient.setReceiverId(user.getId());
        	recipient.setReceiverIdNumber(receiverIdNumber);
        	recipient.setReceiverName(user.getName());
        	recipient.setReceiverPhone(user.getMobile());
        	recipient.setLoginId(user.getLoginId());
        	recipient.setDivisionCode(user.getDivisionCode());
        	
        	GovAgency dept = null;
        	String agencyCode = user.getAgencyCode();
        	if (StrUtil.isNotBlank(agencyCode)) {
        		dept = remoteUniqueDeptService.getAgencyByCode(agencyCode);
        		log.debug("buildRecipientFromDTO dept: {}", dept);
        		if (dept != null) {
	        		recipient.setReceivingUnitId(dept.getId());
	        		recipient.setReceivingUnitCode(dept.getCode());
	        		recipient.setReceivingUnitName(dept.getName());
        		}
        	}
    	}
        
        return recipient;
    }
	
	/**
	 * 更新接收信息
	 * @param unit 接收单位（回传）
	 * @param recipient 接收个人（回传）
	 * @param receiver 多个接收者（回传）
	 * @param sendTargetType 入参：发送对象类型/接收类型
	 * @param receivingUnitCode 入参：接收单位代码
	 * @param receiverIdNumber 入参：接收个人身份证号码
	 * @param rawReceivingScope 入参：原始接收者范围
	 * @param message 入参：消息主表记录
	 * @param umpTopicSubscriptionService 入参：订阅服务
	 * @param remoteUniqueUserService 入参：远程统一用户服务
	 * @param remoteUniqueDeptService 入参：远程统一部门（机关）服务
	 */
	static public ReceivingInfoResult updateReceivingInfo(MessageReceivingUnit unit, MessageRecipient recipient, MessageReceiver receiver,
			String sendTargetType, String receivingUnitCode, String receiverIdNumber, 
			Map<String, Object> rawReceivingScope, UmpMsgMain message, 	
			UmpMsgBroadcastMapper umpMsgBroadcastMapper, UmpMsgInboxMapper umpMsgInboxMapper,
			UmpTopicSubscriptionMapper umpTopicSubscriptionMapper, RemoteUniqueUserService remoteUniqueUserService,
			RemoteUniqueDeptService remoteUniqueDeptService) {
		
        String msgId = message.getId();
        
        if (StrUtil.isBlank(sendTargetType)) {
    		log.error("发送对象类型为空，消息ID:", msgId);
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "发送对象类型为空,消息ID：" + msgId);
    	}
    	
        ReceivingScope receivingScope = null;
        Integer totalReceivers = 0;

        List<String> appKeys = ContentUtil.getReceiverAppKeys(message.getTopicCode(), umpTopicSubscriptionMapper);
        
        switch(sendTargetType) {
        case MqMessageEventConstants.ReceiverTypes.DEPT:
        	// 部门（机关或者组织机构）
            if (StrUtil.isBlank(receivingUnitCode)) {
            	UmpMsgBroadcast umpMsgBroadcast = umpMsgBroadcastMapper.selectByMsgId(msgId);
                if (BeanUtil.isEmpty(umpMsgBroadcast)) {
                    log.error("找不到广播筒记录，消息ID: {}", msgId);
                    throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "找不到广播筒记录，消息ID: " + msgId);
                }  
                receivingUnitCode = umpMsgBroadcast.getReceivingUnitCode();
            } 
        	unit = ReceiverUtil.buildReceivingUnit(sendTargetType, receivingUnitCode, 
        			remoteUniqueUserService, remoteUniqueDeptService);
            if (unit != null && CollUtil.isEmpty(unit.getAppKeys())) {
            	unit.setAppKeys(appKeys);
            }
            if (unit != null) {
            	totalReceivers = unit.getMemberCount();
            }
            break;
        case MqMessageEventConstants.ReceiverTypes.USER:
        	// 个人
            if (StrUtil.isBlank(receiverIdNumber)) {
        		UmpMsgInbox umpMsgInbox = umpMsgInboxMapper.selectByMsgId(msgId).get(0);
                if (BeanUtil.isEmpty(umpMsgInbox)) {
                    log.error("找不到收件箱，消息ID: {}", msgId);
                    throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "找不到收件箱，消息ID: " + msgId);
                }  
                receiverIdNumber = umpMsgInbox.getReceiverIdNumber();
            }
        	recipient = ReceiverUtil.buildRecipient(sendTargetType, receiverIdNumber, 
        			remoteUniqueUserService, remoteUniqueDeptService);
            if (recipient != null && CollUtil.isEmpty(recipient.getAppKeys())) {
            	recipient.setAppKeys(appKeys);
            }
            totalReceivers = 1;
            break;
        case MqMessageEventConstants.ReceiverTypes.CUSTOM:
        case MqMessageEventConstants.ReceiverTypes.AREA:
        case MqMessageEventConstants.ReceiverTypes.ORG:
        case MqMessageEventConstants.ReceiverTypes.ALL:
            // 自定义：多个接收者
        	if (CollUtil.isEmpty(rawReceivingScope)) {
            	UmpMsgBroadcast umpMsgBroadcast = umpMsgBroadcastMapper.selectByMsgId(msgId);
                if (BeanUtil.isEmpty(umpMsgBroadcast)) {
                    log.error("找不到广播筒记录，消息ID: {}", msgId);
                    throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "找不到广播筒记录，消息ID: " + msgId);
                } 
                rawReceivingScope = BeanUtil.beanToMap(umpMsgBroadcast.getReceivingScope());
        	}
            receivingScope = BeanUtil.toBean(rawReceivingScope, ReceivingScope.class);
            receiver = ReceiverUtil.resolveReceivers(sendTargetType, receivingScope, remoteUniqueUserService, appKeys); 
            if (receiver != null && CollUtil.isEmpty(receiver.getAppKeys())) {
            	receiver.setAppKeys(appKeys);
            }
            if (receiver != null) {
            	totalReceivers = receiver.getTotalRecipientCount();
            }
            break;
        default:
            log.error("发送对象类型不对");
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "发送对象类型不正确");
        }
        
        return new ReceivingInfoResult(unit, recipient, receiver, totalReceivers);
	}
	
	/**
	 * 构建接收信息
	 * @param unit 接收单位（回传）
	 * @param recipient 接收个人（回传）
	 * @param receiver 多个接收者（回传）
	 * @param sendTargetType 入参：发送对象类型/接收类型
	 * @param receivingUnitCode 入参：接收单位代码
	 * @param receiverIdNumber 入参：接收个人身份证号码
	 * @param rawReceivingScope 入参：原始接收者范围
	 * @param umpTopicSubscriptionService 入参：订阅服务
	 * @param remoteUniqueUserService 入参：远程统一用户服务
	 * @param remoteUniqueDeptService 入参：远程统一部门（机关）服务
	 */
	static public ReceivingInfoResult buildReceivingInfo(MessageReceivingUnit unit, MessageRecipient recipient, MessageReceiver receiver,
			String sendTargetType, String receivingUnitCode, String receiverIdNumber, 
			Map<String, Object> rawReceivingScope, String topicCode,
			UmpMsgBroadcastMapper umpMsgBroadcastMapper, UmpMsgInboxMapper umpMsgInboxMapper,
			UmpTopicSubscriptionMapper umpTopicSubscriptionMapper, RemoteUniqueUserService remoteUniqueUserService,
			RemoteUniqueDeptService remoteUniqueDeptService) {
        if (StrUtil.isBlank(sendTargetType)) {
    		log.error("发送对象类型为空");
    		throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "发送对象类型为空");
    	}
    	
        ReceivingScope receivingScope = null;
        Integer totalReceivers = 0;

        List<String> appKeys = ContentUtil.getReceiverAppKeys(topicCode, umpTopicSubscriptionMapper);
        
        switch(sendTargetType) {
        case MqMessageEventConstants.ReceiverTypes.DEPT:
        	// 部门（机关或者组织机构）
        	unit = ReceiverUtil.buildReceivingUnit(sendTargetType, receivingUnitCode, 
        			remoteUniqueUserService, remoteUniqueDeptService);
            if (unit != null && CollUtil.isEmpty(unit.getAppKeys())) {
            	unit.setAppKeys(appKeys);
            }
            if (unit != null) {
            	totalReceivers = unit.getMemberCount();
            }
            break;
        case MqMessageEventConstants.ReceiverTypes.USER:
        	// 个人
        	recipient = ReceiverUtil.buildRecipient(sendTargetType, receiverIdNumber, 
        			remoteUniqueUserService, remoteUniqueDeptService);
            if (recipient != null && CollUtil.isEmpty(recipient.getAppKeys())) {
            	recipient.setAppKeys(appKeys);
            }
            totalReceivers = 1;
            break;
        case MqMessageEventConstants.ReceiverTypes.CUSTOM:
        case MqMessageEventConstants.ReceiverTypes.AREA:
        case MqMessageEventConstants.ReceiverTypes.ORG:
        case MqMessageEventConstants.ReceiverTypes.ALL:
            // 自定义：多个接收者
            receivingScope = BeanUtil.toBean(rawReceivingScope, ReceivingScope.class);
            receiver = ReceiverUtil.resolveReceivers(sendTargetType, receivingScope, remoteUniqueUserService, appKeys); 
            if (receiver != null && CollUtil.isEmpty(receiver.getAppKeys())) {
            	receiver.setAppKeys(appKeys);
            }
            if (receiver != null) {
            	totalReceivers = receiver.getTotalRecipientCount();
            }
            break;
        default:
            log.error("发送对象类型不对");
            throw new BusinessException(BusinessEnum.UMP_PARAM_MISSING.getCode(), "发送对象类型不正确");
        }
        
        return new ReceivingInfoResult(unit, recipient, receiver, totalReceivers);
	}
	
	@Data
	@AllArgsConstructor
	public static class ReceivingInfoResult {
	    private MessageReceivingUnit unit;
	    private MessageRecipient recipient;
	    private MessageReceiver receiver;
	    private Integer totalReceivers;
	}
}
