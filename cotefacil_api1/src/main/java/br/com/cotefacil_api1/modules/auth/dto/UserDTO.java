package br.com.cotefacil_api1.modules.auth.dto;

import br.com.cotefacil_api1.modules.auth.enums.Role;
import br.com.cotefacil_api1.modules.auth.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private String username;

    @Schema(description = "User password", example = "Senha@123")
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
