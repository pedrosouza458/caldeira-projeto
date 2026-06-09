package com.gc.projeto.shared.presentation.dtos;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDTO(
        int status,
        String message,
        Instant timestamp,
        List<String> errors
) {
    /** Usado por exceções sem detalhes de campo (ex: 409, 500). */
    public ErrorResponseDTO(int status, String message, Instant timestamp) {
        this(status, message, timestamp, null);
    }
}