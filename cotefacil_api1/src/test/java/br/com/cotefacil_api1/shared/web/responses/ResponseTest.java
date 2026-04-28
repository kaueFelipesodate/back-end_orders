package br.com.cotefacil_api1.shared.web.responses;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
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
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put("customerName", "Nome é obrigatório");
        errors.put("customerEmail", "E-mail inválido");

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
        assertEquals("Falha na validação: Nome é obrigatório, E-mail inválido", response.getMessage());
        assertEquals("/api/orders", response.getPath());
        assertEquals(errors, response.getErrors());
    }

    @Test
    void validationError_devePreencherEstrutura_quandoErrosForemInformados() {
        Response response = Response.validationError(
                "Validation failed for 1 field(s)",
                "/api/orders",
                Map.of("customerName", "Nome é obrigatório")
        );

        assertFalse(response.getSuccess());
        assertEquals("Validation Error", response.getError());
        assertEquals("/api/orders", response.getPath());
        assertEquals("Validation failed for 1 field(s): Nome é obrigatório", response.getMessage());
    }
}
