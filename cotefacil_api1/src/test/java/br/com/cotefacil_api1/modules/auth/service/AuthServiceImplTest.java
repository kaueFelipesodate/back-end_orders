package br.com.cotefacil_api1.modules.auth.service;

import br.com.cotefacil_api1.modules.auth.dto.AuthDTO;
import br.com.cotefacil_api1.modules.auth.dto.UserDTO;
import br.com.cotefacil_api1.modules.auth.enums.Role;
import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.service.impl.AuthServiceImpl;
import br.com.cotefacil_api1.shared.web.responses.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_deveRetornarTokenEUsuario_quandoCredenciaisForemValidas() {
        User user = new User();
        user.setId(1L);
        user.setUsername("usuario");
        user.setPassword("senha");
        user.setRoles(EnumSet.of(Role.ROLE_USER));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(user)).thenReturn("token-123");

        Response response = authService.login(new AuthDTO("usuario", "senha"));

        assertTrue(response.getSuccess());
        assertEquals("Login successful.", response.getMessage());
        UserDTO dto = (UserDTO) response.getData();
        assertEquals("usuario", dto.getUsername());
        assertEquals("token-123", dto.getToken());
        verify(authenticationManager).authenticate(any());
        verify(tokenService).generateToken(user);
    }
}
