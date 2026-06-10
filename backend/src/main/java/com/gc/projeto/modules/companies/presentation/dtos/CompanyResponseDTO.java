package com.gc.projeto.modules.companies.presentation.dtos;

import com.gc.projeto.modules.companies.domain.Company;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(
        description = "Modelo de resposta com os dados públicos da empresa",
        example = """
        {
          "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
          "name": "Empresa Exemplo LTDA",
          "logo": "https://cdn.projeto.com/logos/empresa.png",
          "isResident": true,
          "createdAt": "2026-06-09T23:58:00Z",
          "updatedAt": "2026-06-09T23:59:30Z"
        }
        """
)
public record CompanyResponseDTO(
        @Schema(description = "Identificador único gerado pelo sistema (UUID formato 8-4-4-4-12 hex digits)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Nome cadastrado da empresa", example = "Empresa Exemplo LTDA")
        String name,

        @Schema(description = "URL do logotipo da empresa (deve ser HTTPS)", example = "https://cdn.projeto.com/logos/empresa.png")
        String logo,

        @Schema(description = "Status de residência da empresa no ecossistema (true/false)", example = "true")
        boolean isResident,

        @Schema(description = "Data e hora em que a empresa foi cadastrada (formato ISO-8601 com timezone)", example = "2026-06-09T23:58:00Z")
        Instant createdAt,

        @Schema(description = "Data e hora da última alteração nos dados da empresa (formato ISO-8601 com timezone)", example = "2026-06-09T23:59:30Z")
        Instant updatedAt
) {
    public CompanyResponseDTO(Company company) {
        this(
                company.getId(),
                company.getName(),
                company.getLogo(),
                company.getIsResident(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}