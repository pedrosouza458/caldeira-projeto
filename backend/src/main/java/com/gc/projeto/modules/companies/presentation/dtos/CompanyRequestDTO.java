package com.gc.projeto.modules.companies.presentation.dtos;

import com.gc.projeto.modules.companies.application.dtos.CreateCompanyInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Schema(
        description = "Dados necessários para a criação de uma nova empresa",
        example = """
        {
          "name": "Empresa Exemplo LTDA",
          "logo": "https://cdn.projeto.com/logos/empresa.png",
          "isResident": true
        }
        """
)
public record CompanyRequestDTO(
        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(max = 255, message = "O nome pode ter no máximo 255 caracteres.")
        @Schema(description = "Nome comercial ou razão social da empresa (deve ser único)", example = "Empresa Exemplo LTDA")
        String name,

        @NotBlank(message = "A URL do logo é obrigatória.")
        @URL(message = "A URL do logo informada é inválida.")
        @Size(max = 500, message = "A URL do logo pode ter no máximo 500 caracteres.")
        @Schema(description = "URL completa de hospedagem da imagem do logotipo", example = "https://cdn.projeto.com/logos/empresa.png")
        String logo,

        @NotNull(message = "O campo isResident é obrigatório.")
        @Schema(description = "Indica se a empresa opera como residente no ecossistema", example = "true")
        Boolean isResident
) {
    public CreateCompanyInput toInput() {
        return new CreateCompanyInput(name, logo, isResident);
    }
}