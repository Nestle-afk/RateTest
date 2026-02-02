package com.arca.rate.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record ErrorResponse(String code,
                            String message,
                            int status,
                            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime timestamp) {
    public ErrorResponse(String code, String message, int status) {
        this(code, message, status, OffsetDateTime.now());
    }
}
