package br.com.cotefacil_api1.modules.auth.dto;

import br.com.cotefacil_api1.modules.auth.enums.Role;
import br.com.cotefacil_api1.modules.auth.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User authenticated and token returned upon login.")
public class UserDTO {

    @Schema(description = "Username", example = "usuario")
    @NotBlank(message = "Enter your username.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    @Pattern(
            regexp = "^[A-Za-z0-9._-]+$",
            message = "Username can contain only letters, numbers, dot, underscore and hyphen."
    )
    private String username;

    @Schema(description = "User password", example = "Senha@123")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Enter your password.")
    @Size(min = 3, max = 72, message = "Password must be between 3 and 72 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, one digit and one special character.")
    private String password;

    @Schema(description = "User access profiles")
    private Set<Role> roles;

    @Schema(description = "JWT token generated upon login.")
    private String token;

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto, "id");
        return dto;
    }
}
