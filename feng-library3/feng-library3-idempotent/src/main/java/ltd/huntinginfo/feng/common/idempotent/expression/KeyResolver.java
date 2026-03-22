package ltd.huntinginfo.feng.common.idempotent.expression;

import ltd.huntinginfo.feng.common.idempotent.annotation.Idempotent;
import org.aspectj.lang.JoinPoint;

/*
 *
 * @Description: 唯一标志处理器
 * @author edison
 * @date 2022/5/23
 */
public interface KeyResolver {

	/**
	 * 解析处理 key
	 * @param idempotent 接口注解标识
	 * @param point 接口切点信息
	 * @return 处理结果
	 */
	String resolver(Idempotent idempotent, JoinPoint point);

}
