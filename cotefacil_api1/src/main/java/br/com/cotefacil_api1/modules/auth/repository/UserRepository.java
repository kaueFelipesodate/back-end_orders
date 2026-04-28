package br.com.cotefacil_api1.modules.auth.repository;

import br.com.cotefacil_api1.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
