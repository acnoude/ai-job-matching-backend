package com.anu.aijobmatching.common.api;

import java.time.Instant;
import java.util.Map;

public record ApiError(
    Instant timestamp,
    String error,
    String path,
    String traceId, 
    Map<String, String> fieldErrors,
    String message,
    int status) {

        public static ApiError of(
            String error,
            String path,
            String traceId,
            Map<String, String> fieldErrors,
            String message,
            int status) {
                return new ApiError(    
                    Instant.now(),
                    error,
                    path,
                    traceId,
                    fieldErrors,
                    message,
                    status
                );     
            } 
}
