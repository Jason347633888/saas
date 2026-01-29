package com.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 插件Web配置
 */
@Configuration
@RequiredArgsConstructor
public class PluginWebMvcConfiguration implements WebMvcConfigurer {

    private final PluginRateLimitInterceptor pluginRateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pluginRateLimitInterceptor)
                .addPathPatterns("/api/field-plugin/**")
                .excludePathPatterns(
                        "/api/field-plugin/list",
                        "/api/field-plugin/{type}"
                );
    }
}
