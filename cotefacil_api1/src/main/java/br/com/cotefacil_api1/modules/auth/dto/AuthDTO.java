package br.com.cotefacil_api1.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Credentials used to log in to API 1.")
public class AuthDTO {

    @Schema(description = "Username", example = "usuario")
    @NotBlank(message = "Enter your username.")
    private String username;

    @Schema(description = "User password", example = "Senha@123")
    @NotBlank(message = "Enter your password.")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit and one special character.")
    private String password;
}
