package br.com.cotefacil_api1.shared.config;

import br.com.cotefacil_api1.shared.security.SqlInjectionProtection;
import br.com.cotefacil_api1.shared.web.interceptors.SqlInjectionValidationInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebConfigTest {

    @Test
    void addInterceptors_deveRegistrarInterceptor_quandoConfiguracaoForExecutada() {
        WebConfig webConfig = new WebConfig(new SqlInjectionValidationInterceptor(new SqlInjectionProtection()));
        InterceptorRegistry registry = new InterceptorRegistry();

        webConfig.addInterceptors(registry);

        @SuppressWarnings("unchecked")
        List<Object> registrations = (List<Object>) ReflectionTestUtils.getField(registry, "registrations");
        assertEquals(1, registrations.size());
    }
}
