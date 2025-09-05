package com.example.concert.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // JWT 만료
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwt(ExpiredJwtException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "ACCESS_TOKEN_EXPIRED", "Access token has expired");
    }

    // JWT 일반 예외
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_JWT", "Invalid JWT token");
    }

    // TOKEN 헤더 파싱 예외
    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<?> handleInvalidAuthorizationHeaderException(InvalidAuthorizationHeaderException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_AUTHORIZATION", "Invalid Authorization header");
    }

    // DTO @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errorMessage);
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", e.getMessage());
    }

    // 공통 응답 빌더
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String errorCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", errorCode);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
