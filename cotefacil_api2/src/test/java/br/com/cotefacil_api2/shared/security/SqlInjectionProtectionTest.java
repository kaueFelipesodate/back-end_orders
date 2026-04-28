package br.com.cotefacil_api2.shared.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
