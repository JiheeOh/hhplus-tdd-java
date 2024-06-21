package io.hhplus.tdd;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus code,
        String message
) {
}
