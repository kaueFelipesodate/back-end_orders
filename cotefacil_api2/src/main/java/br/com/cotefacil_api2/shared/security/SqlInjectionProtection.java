package br.com.cotefacil_api2.shared.security;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class SqlInjectionProtection {

    private static final List<Pattern> SQL_INJECTION_PATTERNS = Arrays.asList(
            Pattern.compile("('|(\\-\\-)|(;)|(\\|)|(\\*)|(%))", Pattern.CASE_INSENSITIVE),

            Pattern.compile("\\b(union|select|insert|delete|update|drop|create|alter|exec|execute)\\b", Pattern.CASE_INSENSITIVE),

            Pattern.compile("\\b(script|javascript|vbscript|onload|onerror|onclick)\\b", Pattern.CASE_INSENSITIVE),

            Pattern.compile("(<|>|&lt;|&gt;)", Pattern.CASE_INSENSITIVE),

            Pattern.compile("(\\bor\\b|\\band\\b)\\s*\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),

            Pattern.compile("(\\bor\\b|\\band\\b)\\s*['\"]\\s*['\"]", Pattern.CASE_INSENSITIVE)
    );

    private static final String[] DANGEROUS_CHARS = {"'", "\"", ";", "--", "/*", "*/", "xp_", "sp_", "\\", "<", ">", "&", "|"};

    public boolean isSafeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return true;
        }

        String cleanInput = input.trim().toLowerCase();

        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(cleanInput).find()) {
                return false;
            }
        }

        return true;
    }

    public String sanitizeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        String sanitized = input;

        for (String dangerousChar : DANGEROUS_CHARS) {
            sanitized = sanitized.replace(dangerousChar, "");
        }

        sanitized = sanitized.replaceAll("\\s+", " ").trim();

        return sanitized;
    }

    public String escapeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public String validateAndSanitize(String input) {
        if (!isSafeInput(input)) {
            throw new IllegalArgumentException("Input contains characters or patterns suspicious for SQL Injection");
        }

        return sanitizeInput(input);
    }

    public boolean validateMultipleInputs(String... inputs) {
        if (inputs == null || inputs.length == 0) {
            return true;
        }

        for (String input : inputs) {
            if (!isSafeInput(input)) {
                return false;
            }
        }

        return true;
    }

    public String sanitizeForLikeQuery(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        return input.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").replace("'", "\\'");
    }
}
