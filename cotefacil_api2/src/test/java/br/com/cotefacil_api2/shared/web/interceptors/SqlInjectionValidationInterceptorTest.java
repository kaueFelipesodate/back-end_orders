package br.com.cotefacil_api2.shared.web.interceptors;

import br.com.cotefacil_api2.shared.security.SqlInjectionProtection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SqlInjectionValidationInterceptorTest {

    @Mock
    private SqlInjectionProtection sqlInjectionProtection;

    @InjectMocks
    private SqlInjectionValidationInterceptor interceptor;

    @Test
    void preHandle_deveBloquearRequisicao_quandoParametroForInseguro() throws Exception {
        when(sqlInjectionProtection.isSafeInput("SELECT * FROM users")).thenReturn(false);
        ReflectionTestUtils.setField(interceptor, "sqlInjectionProtection", sqlInjectionProtection);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("q", "SELECT * FROM users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        assertTrue(response.getStatus() == 400);
    }

    @Test
    void preHandle_devePermitirRequisicao_quandoParametroForSeguro() throws Exception {
        when(sqlInjectionProtection.isSafeInput("consulta")).thenReturn(true);
        ReflectionTestUtils.setField(interceptor, "sqlInjectionProtection", sqlInjectionProtection);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("q", "consulta");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
    }
}
