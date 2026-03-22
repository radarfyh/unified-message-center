package ltd.huntinginfo.feng.common.idempotent;

import ltd.huntinginfo.feng.common.idempotent.aspect.IdempotentAspect;
import ltd.huntinginfo.feng.common.idempotent.expression.ExpressionResolver;
import ltd.huntinginfo.feng.common.idempotent.expression.KeyResolver;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 *
 * @Description: 幂等插件初始化
 * @author edison
 * @date 2022/5/23
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataRedisAutoConfiguration.class)
public class IdempotentAutoConfiguration {

    /**
     * 切面 拦截处理所有 @Idempotent
     *
     * @return Aspect
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * key 解析器
     *
     * @return KeyResolver
     */
    @Bean
    @ConditionalOnMissingBean(KeyResolver.class)
    public KeyResolver keyResolver() {
        return new ExpressionResolver();
    }

}
