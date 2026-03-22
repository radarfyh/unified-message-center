//package ltd.huntinginfo.feng.admin.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class ResourceServerConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(authorize -> authorize
//                // 放行内部 Feign 调用接口
//                .requestMatchers("/ump/app/credential/key/**").permitAll()
//                // 其他需要放行的路径（如监控、文档等）
//                .requestMatchers("/error", "/actuator/**", "/v2/api-docs", "/druid/**").permitAll()
//                // 其余所有请求需要认证
//                .anyRequest().authenticated()
//            )
//            // 使用默认 JWT 配置（需要存在 JwtDecoder Bean）
//            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//            // 对于无状态 API，通常禁用 CSRF
//            .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }
//}
