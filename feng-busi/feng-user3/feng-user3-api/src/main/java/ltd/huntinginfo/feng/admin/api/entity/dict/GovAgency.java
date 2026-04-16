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
package ltd.huntinginfo.feng.admin.api.entity.dict;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 机关代码表实体类（政府单位）
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@TableName("gov_agency")
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 空集合不序列化
public class GovAgency implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 主键id自增
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 政府机关名称
     */
    private String name;

    /**
     * 政府机关代码
     */
    private String code;
    
    @Schema(description = "简称",  example = "滨海法制")
    private String sname;

    @Schema(description = "全称",  example = "天津市滨海区新区公安分局法制大队")
    private String fname;
    
    @Schema(description = "行政区划代码",  example = "100000")
    private String divisionCode;
     
    /**
     * 上级政府机关代码
     */
    private String parentCode;

    /**
     * 政府机关级别
     */
    private Integer level;

    @Schema(description = "地址",  example = "天津市滨海区文三路359号")
    private String address;

    @Schema(description = "办公电话",  example = "022-87000000")
    private String officeTel;

    @Schema(description = "电子邮件",  example = "office@test.gov.cn")
    private String email;
    
     /**
     * 备注说明
     */
    private String remark;

    /**
     * 子政府机关列表(非数据库字段)
     */
    @TableField(exist = false)
    private List<GovAgency> children;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 逻辑删除标记(0:正常,1:删除)
     */
    @TableLogic
    private String delFlag;
}