package br.com.cotefacil_api1.shared.security;

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

    /**
     * Validates whether a string contains SQL Injection attempts
     *
     * @param input String to be validated
     * @return true if the string is safe, false otherwise
     */
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

    /**
     * Sanitizes a string by removing dangerous characters
     *
     * @param input String to be sanitized
     * @return Sanitized string
     */
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

    /**
     * Escapes special characters for safe use in queries
     *
     * @param input String to be escaped
     * @return String with escaped characters
     */
    public String escapeInput(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        return input.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    /**
     * Validates and sanitizes an input string
     *
     * @param input String to be processed
     * @return Processed and safe string
     * @throws IllegalArgumentException if the input contains SQL injection
     */
    public String validateAndSanitize(String input) {
        if (!isSafeInput(input)) {
            throw new IllegalArgumentException("Input contains characters or patterns suspicious for SQL Injection");
        }

        return sanitizeInput(input);
    }

    /**
     * Validates multiple input strings
     *
     * @param inputs Array of strings to be validated
     * @return true if all strings are safe
     */
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

    /**
     * Cleans a string for use in LIKE queries
     *
     * @param input String to be cleaned
     * @return String cleaned for use in LIKE
     */
    public String sanitizeForLikeQuery(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        return input.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_").replace("'", "\\'");
    }
}
