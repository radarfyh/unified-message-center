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
package ltd.huntinginfo.feng.center.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "权限统计VO")
public class PermissionStatisticsVO {
    
    @Schema(description = "总权限数")
    private Long totalCount;
    
    @Schema(description = "已启用权限数")
    private Long enabledCount;
    
    @Schema(description = "已禁用权限数")
    private Long disabledCount;
    
    @Schema(description = "读权限数")
    private Long readCount;
    
    @Schema(description = "写权限数")
    private Long writeCount;
    
    @Schema(description = "所有操作权限数")
    private Long allOperationCount;
    
    @Schema(description = "启用率")
    private Double enableRate;
    
    @Schema(description = "读权限占比")
    private Double readRate;
    
    @Schema(description = "写权限占比")
    private Double writeRate;
    
    @Schema(description = "所有操作权限占比")
    private Double allOperationRate;
}