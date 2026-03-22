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
package ltd.huntinginfo.feng.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * yml配置属性
 * @author radarfyh
 * @date 2024/12/30
 */
@Data
@Validated
@ConfigurationProperties(prefix = "redis.embedded")
public class EmbeddedRedisProperties {
    
    @NotNull
    private Boolean enabled = true;
    
    @Min(1024)
    private int port = 6370;
    
    @NotBlank
    private String maxMemory = "64MB";
    
    private String password;
    
    private Boolean requirepass = false;
    
    private String ip = "127.0.0.1";
    
    private Boolean protectedMode = false;
    
    private String configFile = "/feng-cloud/redis.conf";
}