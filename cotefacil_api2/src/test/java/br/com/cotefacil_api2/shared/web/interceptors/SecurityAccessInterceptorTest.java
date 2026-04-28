package br.com.cotefacil_api2.shared.web.interceptors;

import br.com.cotefacil_api2.shared.security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAccessInterceptorTest {

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SecurityAccessInterceptor interceptor;

    @BeforeEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_deveAutenticarUsername_quandoBearerTokenForValido() throws Exception {
        ReflectionTestUtils.setField(interceptor, "tokenService", tokenService);
        when(tokenService.isValidToken("token")).thenReturn(true);
        when(tokenService.getUsernameFromToken("token")).thenReturn("usuario");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.doFilter(request, response, new MockFilterChain());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertSame("usuario", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_deveIgnorarAutenticacao_quandoHeaderNaoForBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.doFilter(request, response, new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
