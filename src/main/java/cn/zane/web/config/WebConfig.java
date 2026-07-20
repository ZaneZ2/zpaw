package cn.zane.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Web 配置。
 *
 * <p>配置 WebFlux CORS 映射，允许跨域访问 API 端点。</p>
 *
 * @author Zane
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

    /**
     * Configure CORS mappings for the application.
     *
     * <p>Allows all origins and standard HTTP methods on {@code /api/**} paths.</p>
     *
     * @param registry the {@link CorsRegistry} to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
