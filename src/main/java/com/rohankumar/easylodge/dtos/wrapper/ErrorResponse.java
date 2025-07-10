package com.rohankumar.easylodge.dtos.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Boolean success;
    private Integer status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private String method;
    private Map<String, Object> errors;

    public static ErrorResponse error(int status, String message) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();

        return ErrorResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse error(int status, String message, Map<String, Object> errors) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();

        return ErrorResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .errors(errors)
                .path(request.getRequestURI())
                .method(request.getMethod())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
