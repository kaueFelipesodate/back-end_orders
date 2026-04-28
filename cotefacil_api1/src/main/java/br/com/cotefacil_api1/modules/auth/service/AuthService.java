package br.com.cotefacil_api1.modules.auth.service;

import br.com.cotefacil_api1.modules.auth.dto.AuthDTO;
import br.com.cotefacil_api1.shared.web.responses.Response;

public interface AuthService {

    Response login(AuthDTO authDTO);

}
