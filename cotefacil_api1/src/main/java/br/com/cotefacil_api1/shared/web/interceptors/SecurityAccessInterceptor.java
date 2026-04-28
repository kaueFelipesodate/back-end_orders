package br.com.cotefacil_api1.shared.web.interceptors;

import br.com.cotefacil_api1.modules.auth.model.User;
import br.com.cotefacil_api1.modules.auth.service.TokenService;
import br.com.cotefacil_api1.modules.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityAccessInterceptor extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                if (StringUtils.hasText(token) && tokenService.isValidToken(token)) {
                    String username = tokenService.getUsernameFromToken(token);

                    if (StringUtils.hasText(username)) {
                        User user = userService.findByUsername(username);

                        if (user != null && user.isEnabled()) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.debug("User authenticated successfully: {}", username);
                        } else {
                            log.warn("User not found or disabled: {}", username);
                        }
                    }
                } else {
                    log.debug("Invalid or expired token");
                }
            }
        } catch (Exception e) {
            log.error("Error during authentication: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}


