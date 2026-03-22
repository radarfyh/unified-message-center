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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 接收单位信息详情
 */
@Data
public class MessageReceivingUnit {
    /**
     * 接收单位ID，取自gov_agency的ID
     */
	@Schema(description = "接收单位ID")
    private String receivingUnitId;
	
    /**
     * 接收者类型，即发送对象类型
     * USER-个人 DEPT-部门 CUSTOM-自定义 ALL-全体 
     * DEPT:要么Agency机关，要么Org组织/公司，暂时只支持Agency机关
     * CUSTOM：暂时支持USER和DEPT的组合，后续扩展支持USER，DEPT，ORG，DIVISION的组合
     */
	@Schema(description = "接收者类型: USER-个人 DEPT-部门 CUSTOM-自定义 ALL-全体", example = "DEPT")
    private String receiverType;
	
    /**
     * 接收者业务系统appKey，根据主表的topic_code查询主题订阅表ump_topic_subscription来获取，注意不是发送者appkey
     */
	@Schema(description = "接收者所在业务系统的appKey")
    private List<String> appKeys;
	
    /**
     * 接收单位所在行政区划代码
     */
    @Schema(description = "接收单位所在行政区划代码", example = "100000")
    private String divisionCode;
    
    /**
     * 接收单位名称
     */
    @Schema(description = "接收单位名称", example = "重庆市秀山县消防救援大队")
    private String receivingUnitName;
    
    /**
     * 接收单位代码
     */
    @Schema(description = "接收单位代码", example = "500241000001")
    private String receivingUnitCode;
    
	/**
	 * 单位成员（用户）数量
	 */
	Integer memberCount;
}
