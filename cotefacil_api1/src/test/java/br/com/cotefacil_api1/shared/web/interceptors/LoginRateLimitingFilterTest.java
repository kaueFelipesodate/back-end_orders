package br.com.cotefacil_api1.shared.web.interceptors;

import br.com.cotefacil_api1.shared.security.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRateLimitingFilterTest {

    @Test
    void doFilterInternal_deveResponder429_quandoLimiteForExcedido() throws Exception {
        RateLimiterService rateLimiterService = new RateLimiterService();
        ReflectionTestUtils.setField(rateLimiterService, "loginCapacityPerMinute", 1);
        LoginRateLimitingFilter filter = new LoginRateLimitingFilter(rateLimiterService);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");
        request.setRemoteAddr("127.0.0.1");

        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        filter.doFilter(request, firstResponse, new MockFilterChain());
        assertEquals(200, firstResponse.getStatus());

        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        filter.doFilter(request, secondResponse, new MockFilterChain());
        assertEquals(429, secondResponse.getStatus());
        assertEquals("60", secondResponse.getHeader("Retry-After"));
    }
}