package br.com.cotefacil_api1.modules.auth.model;

import br.com.cotefacil_api1.modules.auth.dto.AuthDTO;
import br.com.cotefacil_api1.modules.auth.dto.UserDTO;
import br.com.cotefacil_api1.modules.auth.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAndAuthDtosTest {

    @Test
    void getAuthorities_deveRetornarRolePadrao_quandoRolesForemNulas() {
        User user = new User();

        assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void getAuthorities_deveMapearRoles_quandoRolesForemInformadas() {
        User user = new User();
        user.setRoles(EnumSet.of(Role.ROLE_USER));

        assertTrue(user.getAuthorities().stream().anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));
    }

    @Test
    void toDTO_deveCopiarCampos_quandoUsuarioForConvertido() {
        User user = new User();
        user.setUsername("usuario");
        user.setPassword("senha");
        user.setRoles(EnumSet.of(Role.ROLE_USER));

        UserDTO dto = UserDTO.toDTO(user);

        assertEquals("usuario", dto.getUsername());
        assertEquals("senha", dto.getPassword());
        assertTrue(dto.getRoles().contains(Role.ROLE_USER));
    }

    @Test
    void construtorCompleto_deveArmazenarValores_quandoAuthDTOForCriado() {
        AuthDTO dto = new AuthDTO("usuario", "Senha@123");

        assertEquals("usuario", dto.getUsername());
        assertEquals("Senha@123", dto.getPassword());
    }
}
