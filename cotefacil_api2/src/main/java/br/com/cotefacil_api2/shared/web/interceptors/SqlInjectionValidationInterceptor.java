package br.com.cotefacil_api2.shared.web.interceptors;

import br.com.cotefacil_api2.shared.security.SqlInjectionProtection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;

@Component
@RequiredArgsConstructor
public class SqlInjectionValidationInterceptor implements HandlerInterceptor {

    private static final Logger log = LogManager.getLogger(SqlInjectionValidationInterceptor.class);

    private final SqlInjectionProtection sqlInjectionProtection;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!validateQueryParameters(request)) {
            log.warn("SQL Injection attempt detected in query parameters from IP: {}", getClientIpAddress(request));
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid request parameters\"}");
            return false;
        }

        return true;
    }

    private boolean validateQueryParameters(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            if (paramValues != null) {
                for (String paramValue : paramValues) {
                    if (!sqlInjectionProtection.isSafeInput(paramValue)) {
                        log.debug("Unsafe parameter detected: {} = {}", paramName, paramValue);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
