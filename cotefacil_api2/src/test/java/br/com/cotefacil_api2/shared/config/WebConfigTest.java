package br.com.cotefacil_api2.shared.config;

import br.com.cotefacil_api2.shared.web.interceptors.SqlInjectionValidationInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private SqlInjectionValidationInterceptor sqlInjectionValidationInterceptor;

    @InjectMocks
    private WebConfig webConfig;

    @Test
    void addInterceptors_deveRegistrarInterceptor_quandoConfiguracaoForExecutada() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        when(registry.addInterceptor(sqlInjectionValidationInterceptor)).thenReturn(registration);
        when(registration.addPathPatterns("/**")).thenReturn(registration);

        webConfig.addInterceptors(registry);

        verify(registry).addInterceptor(sqlInjectionValidationInterceptor);
        verify(registration).addPathPatterns("/**");
    }
}
