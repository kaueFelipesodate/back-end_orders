package br.com.cotefacil_api1.modules.auth.service;

import br.com.cotefacil_api1.modules.auth.model.User;

public interface TokenService {

    String generateToken(User user);

    String getUsernameFromToken(String token);

    boolean isValidToken(String token);
}
