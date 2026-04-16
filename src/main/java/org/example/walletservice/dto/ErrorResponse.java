package org.example.walletservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}