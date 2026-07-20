package cn.zane.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 安全配置。
 *
 * <p>配置 Spring Security WebFlux 安全过滤链，当前放行所有请求并禁用 CSRF。</p>
 *
 * @author Zane
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * 配置安全过滤器链（全部放行 + 禁用 CSRF）。
     *
     * <p>Currently permits all requests and disables CSRF protection for API usage.</p>
     *
     * @param http the {@link ServerHttpSecurity} to configure
     * @return the built {@link SecurityWebFilterChain}
     */
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(e -> e.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
