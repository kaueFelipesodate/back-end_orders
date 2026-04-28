package br.com.cotefacil_api1.modules.auth.service.impl;

import br.com.cotefacil_api1.modules.auth.dto.AuthDTO;
import br.com.cotefacil_api1.modules.auth.dto.UserDTO;
import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.service.AuthService;
import br.com.cotefacil_api1.modules.auth.service.TokenService;
import br.com.cotefacil_api1.shared.web.responses.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final TokenService tokenService;

    private final AuthenticationManager authenticationManager;

    @Override
    public Response login(AuthDTO authDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        UserDTO userDTO = UserDTO.toDTO(user);
        userDTO.setToken(tokenService.generateToken(user));
        log.info("Login successful.");
        return Response.success("Login successful.", userDTO);
    }
}
