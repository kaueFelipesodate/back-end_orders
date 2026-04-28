package br.com.cotefacil_api1.modules.auth.service;

import br.com.cotefacil_api1.modules.auth.enums.Role;
import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.service.impl.TokenServiceImpl;
import br.com.cotefacil_api1.shared.exceptions.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceImplTest {

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
        ReflectionTestUtils.setField(tokenService, "secretKey", "nao-base64-secret-key-12345678901234567890");
        ReflectionTestUtils.setField(tokenService, "jwtExpiration", 3_600_000L);
        ReflectionTestUtils.setField(tokenService, "issuer", "cotefacil-issuer");
        ReflectionTestUtils.setField(tokenService, "audience", "backend-auths");
    }

    @Test
    void generateToken_devePermitirLeitura_quandoUsuarioForValido() {
        User user = new User();
        user.setId(1L);
        user.setUsername("usuario");
        user.setRoles(EnumSet.of(Role.ROLE_USER));

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertEquals("usuario", tokenService.getUsernameFromToken(token));
        assertFalse(tokenService.getUsernameFromToken(token).isBlank());
        assertFalse(tokenService.isValidToken("token-invalido"));
    }

    @Test
    void generateToken_deveLancarServiceException_quandoFalharNaAssinatura() {
        ReflectionTestUtils.setField(tokenService, "secretKey", "");

        User user = new User();
        user.setUsername("usuario");
        user.setRoles(EnumSet.of(Role.ROLE_USER));

        assertThrows(ServiceException.class, () -> tokenService.generateToken(user));
    }
}
