package br.com.cotefacil_api1.shared.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlInjectionProtectionTest {

    private final SqlInjectionProtection protection = new SqlInjectionProtection();

    @Test
    void isSafeInput_deveRetornarFalso_quandoHouverPadraoMalicioso() {
        assertFalse(protection.isSafeInput("SELECT * FROM users"));
    }

    @Test
    void isSafeInput_deveRetornarVerdadeiro_quandoTextoForSeguro() {
        assertTrue(protection.isSafeInput("Consulta simples"));
    }

    @Test
    void sanitizeInput_deveRemoverCaracteresPerigosos_quandoTextoForSujeitoAAtencao() {
        assertEquals("Hello world", protection.sanitizeInput(" Hello; -- world "));
    }

    @Test
    void escapeInput_deveEscaparCaracteres_quandoTextoContiverSequenciasEspeciais() {
        assertEquals("a\\\\b\\'c\\\"d", protection.escapeInput("a\\b'c\"d"));
    }

    @Test
    void validateAndSanitize_deveLancarExcecao_quandoEntradaForInsegura() {
        assertThrows(IllegalArgumentException.class, () -> protection.validateAndSanitize("1 OR 1=1"));
    }

    @Test
    void validateMultipleInputs_deveRetornarFalso_quandoAlgumaEntradaForInsegura() {
        assertFalse(protection.validateMultipleInputs("ok", "DROP TABLE users"));
    }

    @Test
    void sanitizeForLikeQuery_deveEscaparWildcards_quandoTextoPossuirCaracteresReservados() {
        assertEquals("a\\\\b\\%c\\_d\\'e", protection.sanitizeForLikeQuery("a\\b%c_d'e"));
    }
}
