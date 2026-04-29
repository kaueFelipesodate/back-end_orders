package br.com.cotefacil_api1.shared.security;

import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.service.TokenService;
import br.com.cotefacil_api1.modules.auth.service.UserService;
import br.com.cotefacil_api1.shared.config.WebConfig;
import br.com.cotefacil_api1.shared.web.interceptors.LoginRateLimitingFilter;
import br.com.cotefacil_api1.shared.web.interceptors.SecurityAccessInterceptor;
import br.com.cotefacil_api1.shared.web.interceptors.SqlInjectionValidationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityInfraTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private SqlInjectionProtection sqlInjectionProtection;

    private SqlInjectionValidationInterceptor sqlInjectionValidationInterceptor;

    private LoginRateLimitingFilter loginRateLimitingFilter;

    private SecurityAccessInterceptor securityAccessInterceptor;

    private RateLimiterService rateLimiterService;

    private SecurityConfig securityConfig;

    private WebConfig webConfig;

    private final br.com.cotefacil_api1.shared.config.Api2ClientConfig api2ClientConfig =
            new br.com.cotefacil_api1.shared.config.Api2ClientConfig();

    @BeforeEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
        rateLimiterService = new RateLimiterService();
        sqlInjectionValidationInterceptor = new SqlInjectionValidationInterceptor(sqlInjectionProtection);
        loginRateLimitingFilter = new LoginRateLimitingFilter(rateLimiterService);
        securityAccessInterceptor = new SecurityAccessInterceptor(tokenService, userService);
        securityConfig = new SecurityConfig(securityAccessInterceptor, loginRateLimitingFilter);
        webConfig = new WebConfig(sqlInjectionValidationInterceptor);
    }

    @Test
    void passwordEncoder_deveSerBCrypt_quandoBeanForCriado() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder.encode("senha"));
        assertTrue(passwordEncoder.matches("senha", passwordEncoder.encode("senha")));
    }

    @Test
    void corsConfigurationSource_deveAdicionarOrigens_quandoConfiguracaoForCriada() {
        var source = securityConfig.corsConfigurationSource("https://app1.com,https://app2.com");
        var configuration = source.getCorsConfiguration(new MockHttpServletRequest());

        assertTrue(configuration.getAllowedOriginPatterns().contains("https://app1.com"));
        assertTrue(configuration.getAllowedOriginPatterns().contains("https://app2.com"));
        assertTrue(configuration.getAllowedOriginPatterns().contains("http://localhost:3000"));
    }

    @Test
    void api2RestTemplate_deveNaoTratarErro_quandoBeanForCriado() throws IOException {
        RestTemplate restTemplate = api2ClientConfig.api2RestTemplate();
        ClientHttpResponse response = new MockClientHttpResponse(new byte[0], HttpStatus.OK);

        assertFalse(restTemplate.getErrorHandler().hasError(response));
        restTemplate.getErrorHandler().handleError(response);
    }

    @Test
    void allowLoginForKey_deveBloquearSegundaRequisicao_quandoCapacidadeForUm() {
        ReflectionTestUtils.setField(rateLimiterService, "loginCapacityPerMinute", 1);

        assertTrue(rateLimiterService.allowLoginForKey("login:127.0.0.1"));
        assertFalse(rateLimiterService.allowLoginForKey("login:127.0.0.1"));
    }

    @Test
    void doFilterInternal_deveAutenticarUsuario_quandoBearerTokenForValido() throws Exception {
        User user = new User();
        user.setUsername("usuario");
        user.setRoles(EnumSet.of(br.com.cotefacil_api1.modules.auth.enums.Role.ROLE_USER));

        when(tokenService.isValidToken("token")).thenReturn(true);
        when(tokenService.getUsernameFromToken("token")).thenReturn("usuario");
        when(userService.findByUsername("usuario")).thenReturn(user);
        ReflectionTestUtils.setField(securityAccessInterceptor, "tokenService", tokenService);
        ReflectionTestUtils.setField(securityAccessInterceptor, "userService", userService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityAccessInterceptor.doFilter(request, response, new MockFilterChain());

        assertSame(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(tokenService).isValidToken("token");
        verify(userService).findByUsername("usuario");
    }

    @Test
    void preHandle_deveBloquearRequisicao_quandoParametroForInseguro() throws Exception {
        when(sqlInjectionProtection.isSafeInput("SELECT * FROM users")).thenReturn(false);
        ReflectionTestUtils.setField(sqlInjectionValidationInterceptor, "sqlInjectionProtection", sqlInjectionProtection);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("q", "SELECT * FROM users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = sqlInjectionValidationInterceptor.preHandle(request, response, new Object());

        assertFalse(allowed);
        assertEquals(400, response.getStatus());
    }

    @Test
    void preHandle_devePermitirRequisicao_quandoParametrosForemSeguros() throws Exception {
        when(sqlInjectionProtection.isSafeInput("consulta")).thenReturn(true);
        ReflectionTestUtils.setField(sqlInjectionValidationInterceptor, "sqlInjectionProtection", sqlInjectionProtection);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("q", "consulta");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = sqlInjectionValidationInterceptor.preHandle(request, response, new Object());

        assertTrue(allowed);
        verify(sqlInjectionProtection).isSafeInput("consulta");
    }
}
