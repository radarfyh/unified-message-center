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
package ltd.huntinginfo.feng.common.mybatis.plugins;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 分页拦截器实现类，用于处理分页查询逻辑
 * <p>
 * 当分页大小小于0时自动设置为0，防止全表查询
 *
 * @author lengleng
 * @date 2025/05/31
 * @since 2021年10月11日
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FengPaginationInnerInterceptor extends PaginationInnerInterceptor {

	/**
	 * 数据库类型
	 * <p>
	 * 查看 {@link #findIDialect(Executor)} 逻辑
	 */
	private DbType dbType;

	/**
	 * 方言实现类
	 * <p>
	 * 查看 {@link #findIDialect(Executor)} 逻辑
	 */
	private IDialect dialect;

	public FengPaginationInnerInterceptor(DbType dbType) {
		this.dbType = dbType;
		this.maxLimit = 1000L;
		this.setOverflow(true);
	}

	public FengPaginationInnerInterceptor(IDialect dialect) {
		this.dialect = dialect;
		this.maxLimit = 1000L;
		this.setOverflow(true);
	}

	/**
	 * 在执行查询前处理分页参数
	 * @param executor 执行器
	 * @param ms 映射语句
	 * @param parameter 参数对象
	 * @param rowBounds 行边界
	 * @param resultHandler 结果处理器
	 * @param boundSql 绑定SQL
	 */
	@Override
	public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
			ResultHandler resultHandler, BoundSql boundSql) {
		IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
		// size 小于 0 直接设置为 0 , 即不查询任何数据
		if (null != page && page.getSize() < 0) {
			page.setSize(0);
		}
		// 如果单页最大记录数大于1000，那么设置为1000
		if (null != page && page.getSize() > 1000L) {
			page.setSize(1000);
		}
		
		super.beforeQuery(executor, ms, page, rowBounds, resultHandler, boundSql);
	}

}
