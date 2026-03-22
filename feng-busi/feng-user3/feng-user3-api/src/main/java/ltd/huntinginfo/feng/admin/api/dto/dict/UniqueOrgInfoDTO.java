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
package ltd.huntinginfo.feng.admin.api.dto.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 统一机构信息数据传输对象
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@Schema(name = "统一机构信息DTO", 
       description = "统一机构信息数据传输对象，包含机构基础信息和关联信息")
public class UniqueOrgInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "机构编号", 
           example = "ORG001")
    private String id;

    @Schema(description = "机构名称", 
           example = "朝阳大队")
    private String orgName;

    @Schema(description = "机构代码", 
           example = "330100500100")
    private String orgCode;

    @Schema(description = "简称", 
           example = "朝阳")
    private String sname;

    @Schema(description = "全称", 
           example = "北京市朝阳区朝阳大队")
    private String fname;

    @Schema(description = "省份编号", 
           example = "33")
    private String proId;

    @Schema(description = "省份名称", 
           example = "浙江省")
    private String proName;

    @Schema(description = "城市编号", 
           example = "3301")
    private String cityId;

    @Schema(description = "城市名称", 
           example = "杭州市")
    private String cityName;

    @Schema(description = "区域编号", 
           example = "330106")
    private String regId;

    @Schema(description = "区域名称", 
           example = "西湖区")
    private String regName;

    @Schema(description = "上级机构编号", 
           example = "330000500000")
    private String parentId;

    @Schema(description = "地址", 
           example = "杭州市西湖区文三路359号")
    private String address;

    @Schema(description = "办公电话", 
           example = "0571-87000000")
    private String officeTel;

    @Schema(description = "电子邮件", 
           example = "office@hzga.gov.cn")
    private String email;

    @Schema(description = "排序ID", 
           example = "1")
    private String orderId;
    
    @Schema(description = "数据操作类型，增：create，删：delete，改：update", 
            example = "create")
    private String operationType;
    
    @Schema(description = "数据类型，增：人员user，机构：org，角色：role", 
            example = "org")
    private String dataType;
    
    @Schema(description = "Secret 令牌", 
            example = "org")
    private String secret;
}

