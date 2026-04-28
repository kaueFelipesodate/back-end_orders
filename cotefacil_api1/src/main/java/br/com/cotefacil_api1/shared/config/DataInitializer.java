package br.com.cotefacil_api1.shared.config;

import br.com.cotefacil_api1.modules.auth.enums.Role;
import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("usuario") != null) {
            return;
        }

        User user = new User();
        user.setUsername("usuario");
        user.setPassword(passwordEncoder.encode("Senha@123"));
        user.setRoles(EnumSet.of(Role.ROLE_USER));

        userRepository.save(user);
    }
}
