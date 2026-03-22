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
 package ltd.huntinginfo.feng.common.core.exception;

import lombok.NoArgsConstructor;

/**
 * 授权拒绝异常类
 *
 * @author lengleng
 * @date 2018/06/22
 */
@NoArgsConstructor
public class FengDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FengDeniedException(String message) {
		super(message);
	}

	public FengDeniedException(Throwable cause) {
		super(cause);
	}

	public FengDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FengDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
