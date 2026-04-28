package br.com.cotefacil_api1.modules.auth.controller;

import br.com.cotefacil_api1.modules.auth.dto.AuthDTO;
import br.com.cotefacil_api1.modules.auth.service.AuthService;
import br.com.cotefacil_api1.shared.web.responses.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "JWT login and authentication endpoints.")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates the user and returns the JWT token.")
    public Response login(@RequestBody @Valid AuthDTO authDTO) {
        return authService.login(authDTO);
    }
}
