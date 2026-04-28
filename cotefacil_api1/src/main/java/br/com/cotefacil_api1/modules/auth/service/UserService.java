package br.com.cotefacil_api1.modules.auth.service;

import br.com.cotefacil_api1.modules.auth.model.User;

public interface UserService {
    User findByUsername(String username);
}
