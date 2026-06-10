package com.gc.projeto.modules.companies.presentation.dtos;

import com.gc.projeto.modules.companies.application.dtos.UpdateCompanyInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "Dados permitidos para a atualização parcial ou total de uma empresa")
public record CompanyUpdateRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 255, message = "O nome pode ter no máximo 255 caracteres.")
        @Schema(description = "Novo nome ou nome mantido da empresa", example = "Empresa Exemplo Atualizada LTDA")
        String name,

        @NotBlank(message = "A URL do logo é obrigatória.")
        @Schema(description = "Nova URL do logotipo da empresa", example = "https://cdn.projeto.com/logos/nova-empresa.png")
        String logo,

        @NotNull(message = "O campo isResident é obrigatório.")
        @Schema(description = "Novo status de residência da empresa", example = "false")
        Boolean isResident
) {
    public UpdateCompanyInput toInput(UUID id) {
        return new UpdateCompanyInput(id, name, logo, isResident);
    }
}