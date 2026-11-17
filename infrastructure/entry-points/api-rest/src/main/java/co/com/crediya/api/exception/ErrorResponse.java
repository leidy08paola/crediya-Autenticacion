package co.com.crediya.api.exception;

import java.time.LocalDateTime;

public record ErrorResponse(String code, String message, LocalDateTime timestamp) {
}