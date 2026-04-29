package br.com.cotefacil_api2.shared.web.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response envelope")
public class Response {

    @Schema(description = "Response time")
    private Instant timestamp;

    @Schema(description = "Code HTTP")
    private int status;

    @Schema(description = "Error type")
    private String error;

    @Schema(description = "Reply message")
    private String message;

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Validation errors")
    private Map<String, String> errors = new HashMap<>();

    @Schema(description = "Data returned")
    private Object data;

    @Schema(description = "Success indicator")
    private Boolean success;

    public static Response success(int status, String message, String path, Object data) {
        return Response.builder()
                .timestamp(Instant.now())
                .status(status)
                .message(message)
                .path(sanitizePath(path))
                .data(data)
                .success(true)
                .errors(new HashMap<>())
                .build();
    }

    public static Response success(String message) {
        return success(200, message, null, null);
    }

    public static Response success(Object data) {
        return success(200, null, null, data);
    }

    public static Response success(String message, Object data) {
        return success(200, message, null, data);
    }

    public static Response error(int status, String error, String message, String path, Map<String, String> errors, Object data) {
        message = mergeMessageWithErrors(message, errors);
        return Response.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .message(message)
                .path(sanitizePath(path))
                .errors(errors != null ? errors : new HashMap<>())
                .data(data)
                .success(false)
                .build();
    }

    public static Response error(int status, String error, String message, String path) {
        return error(status, error, message, path, new HashMap<>(), null);
    }

    public static Response validationError(String message, String path, Map<String, String> validationErrors) {
        return error(400, "Validation Error", message, path, validationErrors, null);
    }

    public static Response internalServerError(String message, String path) {
        return error(500, "Internal Server Error", message, path);
    }

    public static Response unauthorizedError(String message, String path) {
        return error(401, "Unauthorized", message, path);
    }

    public static Response forbiddenError(String message, String path) {
        return error(403, "Forbidden", message, path);
    }

    public static Response notFoundError(String message, String path) {
        return error(404, "Not found.", message, path);
    }

    private static String sanitizePath(String path) {
        if (path == null) return null;
        return path.replace("uri=", "").trim();
    }

    private static String mergeMessageWithErrors(String message, Map<String, String> errors) {
        if (errors == null || errors.isEmpty()) {
            return message;
        }
        String concatenated = errors.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        if (message == null || message.isBlank()) {
            return concatenated;
        }
        return message + ": " + concatenated;
    }
}
