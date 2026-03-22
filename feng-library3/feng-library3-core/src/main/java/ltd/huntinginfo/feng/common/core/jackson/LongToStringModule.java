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
 package ltd.huntinginfo.feng.common.core.jackson;

import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serial;

/**
 * Long类型序列化为字符串模块，用于解决JavaScript中Long精度丢失问题
 *
 * @author lengleng
 * @date 2025/05/30
 */
public class LongToStringModule extends SimpleModule {

	@Serial
	private static final long serialVersionUID = 1L;

	public LongToStringModule() {
		super(PackageVersion.VERSION);
		// Long类型序列化为字符串
		this.addSerializer(Long.class, ToStringSerializer.instance);
		this.addSerializer(Long.TYPE, ToStringSerializer.instance);
	}

}
