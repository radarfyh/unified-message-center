package ltd.huntinginfo.feng.common.security.component;

import ltd.huntinginfo.feng.common.security.service.FengUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

/**
 * 自定义 JWT 认证令牌，将 FengUser 作为 principal
 */
@Transient
public class FengJwtAuthenticationToken extends JwtAuthenticationToken {

    private final FengUser fengUser;

    public FengJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, FengUser fengUser) {
        super(jwt, authorities, fengUser.getUsername());
        this.fengUser = fengUser;
        setDetails(fengUser); // 将 FengUser 同时放入 details
    }

    @Override
    public Object getPrincipal() {
        return this.fengUser;
    }

    public FengUser getFengUser() {
        return fengUser;
    }
}