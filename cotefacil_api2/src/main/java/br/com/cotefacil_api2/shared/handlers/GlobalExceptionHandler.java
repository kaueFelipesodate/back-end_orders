package br.com.cotefacil_api2.shared.handlers;

import br.com.cotefacil_api2.shared.exceptions.ServiceException;
import br.com.cotefacil_api2.shared.web.responses.Response;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    public static final String MALFORMED_JSON = "Malformed JSON";
    public static final String UNKNOWN = "unknown";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            addValidationError(validationErrors, fieldName, errorMessage);
        });

        String path = request.getRequestURI();
        String summary = String.format("Validation failed for %d field(s)", validationErrors.size());
        Response response = Response.validationError(summary, path, validationErrors);

        log.warn("Validation error on path {}: {}", path, validationErrors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String fieldPath = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "";
            String fieldName = fieldPath.contains(".") ? fieldPath.substring(fieldPath.lastIndexOf('.') + 1) : fieldPath;
            String errorMessage = violation.getMessage();
            addValidationError(validationErrors, fieldName, errorMessage);
        }

        String path = request.getRequestURI();
        String summary = String.format("Validation failed for %d parameter(s)", validationErrors.size());
        Response response = Response.validationError(summary, path, validationErrors);

        log.warn("Constraint violation on path {}: {}", path, validationErrors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Response> handleServiceException(
            ServiceException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = Response.error(
                HttpStatus.BAD_REQUEST.value(),
                "Business Rule Error",
                ex.getMessage(),
                path
        );

        log.warn("Service exception on path {}: {}", path, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<Response> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = Response.unauthorizedError("Authentication error: " + ex.getMessage(), path);

        log.warn("Authentication error on path {}: {}", path, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<Response> handleAccessDeniedException(
            Exception ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = Response.forbiddenError("Access denied. You don't have permission to access this resource", path);

        log.warn("Access denied on path {}: {}", path, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = Response.error(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage(),
                path
        );

        log.warn("Illegal argument on path {}: {}", path, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response> handleMissingParams(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String message = String.format("Required parameter '%s' is missing (type: %s)", ex.getParameterName(), ex.getParameterType());

        Response response = Response.error(
                HttpStatus.BAD_REQUEST.value(),
                "Missing Parameter",
                message,
                path
        );

        log.warn("Missing parameter on path {}: {}", path, ex.getParameterName());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : UNKNOWN;
        String provided = ex.getValue() != null ? String.valueOf(ex.getValue()) : "null";
        String message = String.format("Parameter '%s' with value '%s' must be of type %s",
                ex.getName(), provided, requiredType);
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] constants = ex.getRequiredType().getEnumConstants();
            if (constants != null && constants.length > 0) {
                message += String.format(" (allowed: %s)", java.util.Arrays.toString(constants));
            }
        }

        Response response = Response.error(
                HttpStatus.BAD_REQUEST.value(),
                "Type Mismatch",
                message,
                path
        );

        log.warn("Type mismatch on path {}: {}", path, message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String supported = ex.getSupportedHttpMethods() != null ? ex.getSupportedHttpMethods().toString() : "[]";
        String message = String.format("Method %s is not supported for this endpoint. Supported: %s", ex.getMethod(), supported);

        Response response = Response.error(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                message,
                path
        );

        log.warn("Method not supported on path {}: {}", path, ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = buildJsonParseError(ex, path);

        log.warn("Malformed JSON on path {}: {}", path, ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String message = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());
        Response response = Response.notFoundError(message, path);

        log.warn("No handler found for path: {}", path);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String supported = !ex.getSupportedMediaTypes().isEmpty()
                ? ex.getSupportedMediaTypes().toString() : "[]";
        String message = String.format("Content-Type '%s' is not supported. Supported: %s",
                ex.getContentType() != null ? ex.getContentType() : MediaType.ALL, supported);

        Response response = Response.error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Unsupported Media Type",
                message,
                path
        );

        log.warn("Unsupported Media Type on path {}: {}", path, ex.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<Response> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        String supported = !ex.getSupportedMediaTypes().isEmpty()
                ? ex.getSupportedMediaTypes().toString() : "[]";
        String message = String.format("Not acceptable. Supported response media types: %s", supported);

        Response response = Response.error(
                HttpStatus.NOT_ACCEPTABLE.value(),
                "Not Acceptable",
                message,
                path
        );

        log.warn("Not acceptable on path {}: supported {}", path, supported);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = Response.internalServerError(
                "Internal server error",
                path
        );

        log.error("Runtime exception on path {}: {}", path, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        String path = request.getRequestURI();
        Response response = Response.internalServerError(
                "Unexpected server error",
                path
        );

        log.error("Unexpected exception on path {}: {}", path, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private Response buildJsonParseError(HttpMessageNotReadableException ex, String path) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String field = pathFromJackson(ife);
            String target = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : UNKNOWN;
            String value = String.valueOf(ife.getValue());
            String message = String.format("Invalid value '%s' for field '%s' (expected %s)", value, field, target);
            return Response.error(HttpStatus.BAD_REQUEST.value(), MALFORMED_JSON, message, path);
        }
        if (cause instanceof MismatchedInputException mie) {
            String field = pathFromJackson(mie);
            String target = mie.getTargetType() != null ? mie.getTargetType().getSimpleName() : UNKNOWN;
            String message = String.format("Invalid structure for field '%s' (expected %s)", field, target);
            return Response.error(HttpStatus.BAD_REQUEST.value(), MALFORMED_JSON, message, path);
        }
        if (cause instanceof JsonParseException jpe) {
            String message = String.format("Malformed JSON at %s", jpe.getLocalizedMessage());
            return Response.error(HttpStatus.BAD_REQUEST.value(), MALFORMED_JSON, message, path);
        }
        return Response.error(HttpStatus.BAD_REQUEST.value(), MALFORMED_JSON, "Invalid or malformed JSON format", path);
    }

    private String pathFromJackson(MismatchedInputException ex) {
        if (ex.getPath() != null && !ex.getPath().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            ex.getPath().forEach(ref -> {
                if (ref.getFieldName() != null) sb.append('.').append(ref.getFieldName());
                else if (ref.getIndex() != -1) sb.append('[').append(ref.getIndex()).append(']');
            });
            String s = sb.toString();
            return s.startsWith(".") ? s.substring(1) : s;
        }
        return UNKNOWN;
    }

    private void addValidationError(Map<String, String> validationErrors, String field, String message) {
        String current = validationErrors.get(field);
        if (current == null || current.isBlank()) {
            validationErrors.put(field, message);
            return;
        }
        validationErrors.put(field, current + ", " + message);
    }
}
