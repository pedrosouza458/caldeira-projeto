package com.gc.projeto.modules.companies.presentation.dtos;

import com.gc.projeto.modules.companies.application.dtos.CreateCompanyInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompanyRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 255, message = "O nome pode ter no máximo 255 caracteres.")
        @Schema(example = "Empresa Exemplo LTDA")
        String name,

        @NotBlank(message = "A URL do logo é obrigatória.")
        @Schema(example = "https://cdn.projeto.com/logos/empresa.png")
        String logo,

        @NotNull(message = "O campo isResident é obrigatório.")
        @Schema(example = "true")
        Boolean isResident
) {
    public CreateCompanyInput toInput() {
        return new CreateCompanyInput(name, logo, isResident);
    }
}