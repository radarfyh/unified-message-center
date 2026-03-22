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
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 行政区划代码表实体类(GB/T 2260-2013)
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@TableName("dict_administrative_division")
public class DictAdministrativeDivision implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
     * 主键id自增
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 行政区划名称
     */
    private String name;

    /**
     * 行政区划代码(GB/T 2260-2013标准)
     */
    private String code;

    /**
     * 行政级别(1:省级,2:市级,3:县级)
     */
    private Integer level;

    /**
     * 上级行政区划代码
     */
    private String parentCode;
    
    /**
     * 子级行政区划（非数据库字段）
     */
    @TableField(exist = false)
    private List<DictAdministrativeDivision> children;

    /**
     * 备注说明
     */
    private String remark;

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