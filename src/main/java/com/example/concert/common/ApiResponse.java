package com.example.concert.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T> {

    private T data;
    private String message;
    private HttpStatus status;

    public static <T> ApiResponse<T> ok (T data, String message) {
        return new ApiResponse<>(data, message, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(null, message, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(data, message,HttpStatus.BAD_REQUEST);
    }
}
