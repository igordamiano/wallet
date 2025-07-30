package com.recargapay.wallet.exception;

import com.recargapay.wallet.model.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
@Log4j2
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDTO> handleConflict(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Type mismatch: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid parameter type", request.getRequestURI());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorDTO> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        log.warn("Response status error: {}", ex.getReason());
        return buildErrorResponse(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleOther(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request.getRequestURI());
    }

    private ResponseEntity<ApiErrorDTO> buildErrorResponse(HttpStatus status, String message, String path) {
        String traceId = MDC.get("traceId");
        ApiErrorDTO error = new ApiErrorDTO(message, path, LocalDateTime.now(), traceId);
        return ResponseEntity.status(status).body(error);
    }
}
