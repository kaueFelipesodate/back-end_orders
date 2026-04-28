package br.com.cotefacil_api2.shared.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() != null
                        ? SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
                        : "system"
        );
    }
}
