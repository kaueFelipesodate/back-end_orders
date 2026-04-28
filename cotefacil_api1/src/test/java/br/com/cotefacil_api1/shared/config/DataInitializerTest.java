package br.com.cotefacil_api1.shared.config;

import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_deveSalvarUsuarioPadrao_quandoUsuarioNaoExistir() {
        when(userRepository.findByUsername("usuario")).thenReturn(null);
        when(passwordEncoder.encode("Senha@123")).thenReturn("encoded-password");

        dataInitializer.run();

        verify(passwordEncoder).encode("Senha@123");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("usuario", saved.getUsername());
        assertEquals("encoded-password", saved.getPassword());
        assertEquals(1, saved.getRoles().size());
    }

    @Test
    void run_deveIgnorarCriacao_quandoUsuarioJaExistir() {
        when(userRepository.findByUsername("usuario")).thenReturn(new User());

        dataInitializer.run();

        verify(passwordEncoder, never()).encode("Senha@123");
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
