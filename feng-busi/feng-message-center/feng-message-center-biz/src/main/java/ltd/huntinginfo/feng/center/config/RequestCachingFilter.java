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
package ltd.huntinginfo.feng.center.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求缓存过滤器
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 确保它拥有最高优先级，最先执行
public class RequestCachingFilter extends OncePerRequestFilter {
    // 设置默认缓存上限为 10MB 
    private static final int DEFAULT_CACHE_LIMIT = 10 * 1024 * 1024;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 仅对 content-type 为 json 的请求进行包装，避免不必要的开销
        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            // 尝试获取 Content-Length，若不存在或无效则使用默认上限
            int contentLength = request.getContentLength();
            int cacheLimit = (contentLength > 0) ? contentLength : DEFAULT_CACHE_LIMIT;
            
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, cacheLimit);
            // 将包装后的请求继续传递
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
