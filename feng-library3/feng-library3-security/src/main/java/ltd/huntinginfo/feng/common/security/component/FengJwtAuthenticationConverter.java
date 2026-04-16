package ltd.huntinginfo.feng.common.security.component;

import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import ltd.huntinginfo.feng.common.security.service.FengUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 自定义JWT认证转换器，将JWT转换为包含FengUser的Authentication
 */
@Slf4j
public class FengJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final UserDetailsService userDetailsService;

    public FengJwtAuthenticationConverter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        log.debug("FengJwtAuthenticationConverter.convert JWT claims: {}", claims);

        // 1. 从 JWT 中提取用户名
        String username = null;
        // 优先从 user_info 中获取（password 模式）
        Map<String, Object> userMap = (Map<String, Object>) claims.get(SecurityConstants.DETAILS_USER);
        
        log.debug("FengJwtAuthenticationConverter.convert userMap: {}", userMap);
        
        if (userMap != null) {
            username = (String) userMap.get("username");
        }
        // 如果 user_info 中没有，尝试从独立的 username claim 获取
        if (username == null) {
            username = jwt.getClaimAsString(SecurityConstants.USERNAME);
        }
        // 最后使用 subject 作为后备（客户端模式或 app_key 模式）
        if (username == null) {
            username = jwt.getSubject();
        }

        if (username == null || username.isEmpty()) {
            log.error("Cannot extract username from JWT. Claims: {}", claims);
            throw new IllegalArgumentException("Unable to determine username from JWT");
        }

        // 2. 判断是否为用户模式（存在 user_info 或独立的 username claim）
        boolean isUserMode = (userMap != null) || (jwt.getClaimAsString(SecurityConstants.USERNAME) != null);

        FengUser fengUser;
        if (isUserMode) {
            // 用户模式：通过 UserDetailsService 加载完整用户信息（包含角色、权限）
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!(userDetails instanceof FengUser)) {
                log.error("Loaded user is not a FengUser: {}", userDetails.getClass());
                throw new IllegalStateException("UserDetails must be a FengUser");
            }
            fengUser = (FengUser) userDetails;
        } else {
            // 客户端模式（app_key）：构造一个虚拟 FengUser，只包含用户名（应用标识）
            Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
            fengUser = new FengUser(null, null, username, "", null,
                    true, true, true, true, authorities);
        }
        fengUser.getAttributes().putAll(claims);

        return new FengJwtAuthenticationToken(jwt, fengUser.getAuthorities(), fengUser);
    }
    
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> scopes = jwt.getClaimAsStringList("scope");
        if (scopes == null) {
            return Collections.emptyList();
        }
        return scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}