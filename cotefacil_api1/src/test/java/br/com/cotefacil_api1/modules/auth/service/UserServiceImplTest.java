package br.com.cotefacil_api1.modules.auth.service;

import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.repository.UserRepository;
import br.com.cotefacil_api1.modules.auth.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByUsername_deveDelegarAoRepositorio_quandoUsernameForInformado() {
        User user = new User();
        user.setUsername("usuario");
        when(userRepository.findByUsername("usuario")).thenReturn(user);

        User result = userService.findByUsername("usuario");

        assertSame(user, result);
        verify(userRepository).findByUsername("usuario");
    }
}
