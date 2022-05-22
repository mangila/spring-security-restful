package com.github.mangila.springsecurityrestful.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@AllArgsConstructor
public class ErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper json;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var status = HttpStatus.BAD_REQUEST;
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(status).body(new ErrorDto(errors.toString(), status, status.value(), ex.getClass().getName()));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var status = HttpStatus.UNAUTHORIZED;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getOutputStream()
                .println(json.writeValueAsString(ErrorDto.getInstance(status, authException)));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        var status = HttpStatus.FORBIDDEN;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getOutputStream()
                .println(json.writeValueAsString(ErrorDto.getInstance(status, accessDeniedException)));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException exception, WebRequest request) {
        var status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(ErrorDto.getInstance(status, exception));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception exception, WebRequest request) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ErrorDto.getInstance(status, exception));
    }

    private record ErrorDto(String message, HttpStatus status, int value, String name) {
        private static ErrorDto getInstance(HttpStatus status, Throwable throwable) {
            return new ErrorDto(throwable.getMessage(), status, status.value(), throwable.getClass().getName());
        }
    }
}
