package br.com.cotefacil_api2.shared.security;

import br.com.cotefacil_api2.shared.security.impl.TokenServiceImpl;
import br.com.cotefacil_api2.shared.web.interceptors.SecurityAccessInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TokenAndSecurityTest {

    private static final String SECRET = "nao-base64-secret-key-12345678901234567890";

    @InjectMocks
    private SecurityAccessInterceptor securityAccessInterceptor;

    @InjectMocks
    private SecurityConfig securityConfig;

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        tokenService = new TokenServiceImpl();
        ReflectionTestUtils.setField(tokenService, "secretKey", SECRET);
        ReflectionTestUtils.setField(tokenService, "issuer", "cotefacil-issuer");
        ReflectionTestUtils.setField(tokenService, "audience", "backend-clients");
        ReflectionTestUtils.setField(securityConfig, "jwtAuthFilter", securityAccessInterceptor);
    }

    @Test
    void getUsernameFromToken_deveLerSubject_quandoTokenForValido() {
        String token = Jwts.builder()
                .issuer("cotefacil-issuer")
                .subject("usuario")
                .audience().add("backend-clients").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertEquals("usuario", tokenService.getUsernameFromToken(token));
        assertTrue(tokenService.isValidToken(token));
        assertFalse(tokenService.isValidToken("token-invalido"));
    }

    @Test
    void corsConfigurationSource_deveAdicionarLocalhost_quandoBeanForCriado() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource("https://app.com");
        CorsConfiguration configuration = source.getCorsConfiguration(new MockHttpServletRequest());

        assertTrue(configuration.getAllowedOriginPatterns().contains("https://app.com"));
        assertTrue(configuration.getAllowedOriginPatterns().contains("http://localhost:3000"));
    }

    @Test
    void doFilterInternal_deveAutenticarUsername_quandoBearerTokenForValido() throws Exception {
        String token = Jwts.builder()
                .issuer("cotefacil-issuer")
                .subject("usuario")
                .audience().add("backend-clients").and()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        ReflectionTestUtils.setField(securityAccessInterceptor, "tokenService", tokenService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityAccessInterceptor.doFilter(request, response, new MockFilterChain());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("usuario", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
