package br.com.cotefacil_api2.shared.security;

public interface TokenService {

    String getUsernameFromToken(String token);

    boolean isValidToken(String token);
}
