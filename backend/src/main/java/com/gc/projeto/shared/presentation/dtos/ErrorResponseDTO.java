package com.gc.projeto.shared.presentation.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Estrutura padrão para retornos de erro da aplicação")
public record ErrorResponseDTO(
        @Schema(description = "Código do status HTTP do erro", example = "400")
        int status,

        @Schema(description = "Mensagem descritiva do erro global", example = "Dados de entrada inválidos.")
        String message,

        @Schema(description = "Data e hora exata em que o erro ocorreu (padrão ISO-8601)", example = "2026-06-09T23:56:15Z")
        Instant timestamp,

        @Schema(
                description = "Lista detalhada contendo os erros específicos por campo. Retorna nulo para erros que não envolvem validação de formulário.",
                example = "[\"O nome da empresa é obrigatório.\", \"A URL do logo é obrigatória.\"]"
        )
        List<String> errors
) {
    /** Usado por exceções sem detalhes de campo (ex: 409, 500). */
    public ErrorResponseDTO(int status, String message, Instant timestamp) {
        this(status, message, timestamp, null);
    }
}