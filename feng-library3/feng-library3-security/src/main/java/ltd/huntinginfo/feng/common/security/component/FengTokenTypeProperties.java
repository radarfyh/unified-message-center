package ltd.huntinginfo.feng.common.security.component;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 密码认证模式令牌相关配置属性
 *
 * @author radarfyh
 * @date 2026/03/30
 */
@Data
@ConfigurationProperties(prefix = "security.password")
public class FengTokenTypeProperties {
    /**
     * 令牌类型: REFERENCE(不透明令牌), SELF_CONTAINED(JWT)
     */
    private String tokenType = "REFERENCE";
}
