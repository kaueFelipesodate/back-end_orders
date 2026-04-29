package br.com.cotefacil_api1.shared.handlers;

import br.com.cotefacil_api1.shared.exceptions.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleServiceException_deveRetornarBadRequest_quandoRegraNegocioFalhar() {
        var response = handler.handleServiceException(new ServiceException("Order not found."), request("/api/orders/1"));

        assertEquals(400, response.getStatusCode().value());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Business Rule Error", response.getBody().getError());
    }

    @Test
    void handleAuthenticationException_deveRetornarUnauthorized_quandoCredencialForInvalida() {
        var response = handler.handleAuthenticationException(new BadCredentialsException("bad credentials"), request("/auth/login"));

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Unauthorized", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Authentication error"));
    }

    @Test
    void handleAccessDeniedException_deveRetornarForbidden_quandoPermissaoForNegada() {
        var response = handler.handleAccessDeniedException(new AccessDeniedException("denied"), request("/api/orders"));

        assertEquals(403, response.getStatusCode().value());
        assertEquals("Forbidden", response.getBody().getError());
    }

    @Test
    void handleRuntimeException_deveRetornarErroInterno_quandoFalhaNaoTratadaAcontecer() {
        var response = handler.handleRuntimeException(new RuntimeException("boom"), request("/api/orders"));

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Internal server error", response.getBody().getMessage());
    }

    private MockHttpServletRequest request(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        return request;
    }
}
