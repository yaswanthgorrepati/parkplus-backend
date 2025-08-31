package com.parkplus.common.error;

import java.time.Instant;
import java.util.Map;

public record ApiError(String code, String message, Integer status, Map<String, String> fieldErrors, Instant timestamp) {

    public static ApiError of(String code, String message, int status, Map<String, String> fieldErrors) {
        return new ApiError(code, message, status, fieldErrors, Instant.now());
    }
}