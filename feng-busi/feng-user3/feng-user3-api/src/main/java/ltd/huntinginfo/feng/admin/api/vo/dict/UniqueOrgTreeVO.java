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
package ltd.huntinginfo.feng.admin.api.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一机构树形 VO
 * @author Edison.Feng
 * @date 2025/12/30
 */
@Data
@Schema(description = "统一机构树节点")
public class UniqueOrgTreeVO {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "机构编号")
    private String orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构代码")
    private String orgCode;

    @Schema(description = "上级机构ID")
    private String parentId;

    @Schema(description = "排序号")
    private String orderId;

    @Schema(description = "子机构列表")
    private List<UniqueOrgTreeVO> children;
}
