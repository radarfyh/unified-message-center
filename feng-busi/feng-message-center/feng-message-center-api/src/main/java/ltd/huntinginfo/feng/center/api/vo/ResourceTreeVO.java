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

import java.util.List;

/**
 * 资源树视图对象
 */
@Data
@Schema(description = "应用资源树信息")
public class ResourceTreeVO {

    @Schema(description = "应用标识")
    private String appKey;

    @Schema(description = "资源总数")
    private Integer totalResources;

    @Schema(description = "资源节点列表")
    private List<ResourceNodeVO> resources;

    /**
     * 资源节点内部类
     */
    @Data
    @Schema(description = "资源节点")
    public static class ResourceNodeVO {

        @Schema(description = "资源代码")
        private String resourceCode;

        @Schema(description = "资源名称")
        private String resourceName;
    }
}