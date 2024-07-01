package com.neobis.lorby.exception;


import java.time.LocalDateTime;

public record ApiErrorResponse(
        String path,
        String message,
        int statusCode,
        LocalDateTime timestamp
) {
}
