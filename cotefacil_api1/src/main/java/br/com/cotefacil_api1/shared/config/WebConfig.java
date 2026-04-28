package br.com.cotefacil_api1.shared.config;

import br.com.cotefacil_api1.shared.web.interceptors.SqlInjectionValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SqlInjectionValidationInterceptor sqlInjectionValidationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sqlInjectionValidationInterceptor)
                .addPathPatterns("/**");
    }
}
