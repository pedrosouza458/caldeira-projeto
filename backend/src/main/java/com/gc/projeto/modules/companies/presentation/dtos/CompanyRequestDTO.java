package com.gc.projeto.modules.companies.presentation.dtos;

import com.gc.projeto.modules.companies.application.dtos.CreateCompanyInput;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record CompanyRequestDTO(
        @Schema(example = "c3d537ac-aaa2-4d4f-a000-9a97db69163c")
        UUID id,

        @Schema(example = "Empresa Exemplo LTDA")
        String name,

        @Schema(example = "https://cdn.projeto.com/logos/empresa.png")
        String logo,

        @Schema(example = "true")
        Boolean isResident
) {
    public CreateCompanyInput toInput() {
        return new CreateCompanyInput(id, name, logo, isResident);
    }
}