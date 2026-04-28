package br.com.cotefacil_api1.shared.security;

import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_deveRetornarUsuario_quandoUsernameExistir() {
        User user = new User();
        user.setUsername("usuario");
        when(userService.findByUsername("usuario")).thenReturn(user);

        assertSame(user, customUserDetailsService.loadUserByUsername("usuario"));
        verify(userService).findByUsername("usuario");
    }

    @Test
    void loadUserByUsername_deveLancarExcecao_quandoUsuarioNaoExistir() {
        when(userService.findByUsername("inexistente")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("inexistente"));
    }
}
