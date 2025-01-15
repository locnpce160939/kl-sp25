package com.ftcs.common.dto;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private int code = 200;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.message = "success";
        this.data = data;
        this.code = 200;
    }

    public ApiResponse(@Nullable String message,T data) {
        this.message = message;
        this.data = data;
        this.code = 200;
    }

    public ApiResponse(String message, T data, Integer code) {
        this.message = message;
        this.data = data;
        this.code = code != null ? code : 200;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().message("success").data(data).build();
    }
}
