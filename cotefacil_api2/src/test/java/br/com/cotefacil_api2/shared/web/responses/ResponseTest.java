package br.com.cotefacil_api2.shared.web.responses;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest {

    @Test
    void success_deveSanitizarPath_quandoPathPossuirUriPrefixo() {
        Response response = Response.success(201, "Criado", "uri=/api/orders", Map.of("id", 10));

        assertTrue(response.getSuccess());
        assertEquals(201, response.getStatus());
        assertEquals("Criado", response.getMessage());
        assertEquals("/api/orders", response.getPath());
        assertEquals(Map.of(), response.getErrors());
    }

    @Test
    void error_deveMesclarMensagemComErros_quandoMapaDeErrosForInformado() {
        Map<String, String> errors = Map.of(
                "customerName", "Nome é obrigatório",
                "customerEmail", "E-mail inválido"
        );

        Response response = Response.error(
                400,
                "Validation Error",
                "Falha na validação",
                "uri=/api/orders",
                errors,
                null
        );

        assertFalse(response.getSuccess());
        assertEquals(400, response.getStatus());
        assertTrue(response.getMessage().contains("Falha na validação"));
        assertTrue(response.getMessage().contains("Nome é obrigatório"));
        assertTrue(response.getMessage().contains("E-mail inválido"));
        assertEquals("/api/orders", response.getPath());
        assertEquals(2, response.getErrors().size());
    }
}
