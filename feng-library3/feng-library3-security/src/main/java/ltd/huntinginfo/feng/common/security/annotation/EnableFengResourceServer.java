/*
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
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
 *  Author: lengleng
 *
 *  Modified by radarfyh(Edison.Feng) on 2025-12-30.
 *  Copyright (c) 2026 radarfyh(Edison.Feng). All rights reserved.
 *
 *  This file is part of UnifiedMessageCenter and is distributed under the
 *  same license terms as the original work, with additional modifications
 *  as noted above.
 */
package ltd.huntinginfo.feng.common.security.annotation;

import ltd.huntinginfo.feng.common.security.component.FengResourceServerAutoConfiguration;
import ltd.huntinginfo.feng.common.security.component.FengResourceServerConfiguration;
import ltd.huntinginfo.feng.common.security.feign.FengFeignClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Feng资源服务器注解
 * <p>
 * 通过导入相关配置类启用Feng资源服务器功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Import({ FengResourceServerAutoConfiguration.class, FengResourceServerConfiguration.class,
		FengFeignClientConfiguration.class })
public @interface EnableFengResourceServer {

}
