package com.rohankumar.easylodge.dtos.wrapper;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer status;
    private Boolean success;
    private String message;
    private LocalDateTime timestamp;
    private T data;

    public static ApiResponse<Void> success(Integer status, String message) {

        return new ApiResponseBuilder<Void>()
                .status(status)
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(Integer status, String message, T data) {

        return new ApiResponseBuilder<T>()
                .status(status)
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

    }
}
