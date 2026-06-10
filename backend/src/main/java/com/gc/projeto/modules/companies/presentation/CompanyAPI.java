package com.gc.projeto.modules.companies.presentation;

import com.gc.projeto.modules.companies.presentation.dtos.CompanyRequestDTO;
import com.gc.projeto.modules.companies.presentation.dtos.CompanyResponseDTO;
import com.gc.projeto.modules.companies.presentation.dtos.CompanyUpdateRequestDTO;
import com.gc.projeto.shared.presentation.dtos.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Companies", description = "Operações relacionadas ao gerenciamento de empresas")
public interface CompanyAPI {

    @Operation(
            summary = "Criar empresa",
            description = "Cadastra uma nova empresa no sistema. O nome informado deve ser único."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Empresa criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "message": "Dados de entrada inválidos.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": ["O nome da empresa é obrigatório.", "A URL do logo é obrigatória."]
                                    }
                                    """))),
            @ApiResponse(responseCode = "409", description = "Nome da empresa já está em uso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 409,
                                      "message": "O nome da empresa já está em uso.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": null
                                    }
                                    """)))
    })
    @PostMapping
    ResponseEntity<CompanyResponseDTO> createCompany(
            @Valid @RequestBody CompanyRequestDTO request
    );

    @Operation(
            summary = "Listar empresas",
            description = "Retorna todas as empresas cadastradas. Aceita filtro opcional pelo status de residente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CompanyResponseDTO.class))
                    )
            )
    })
    @GetMapping
    ResponseEntity<List<CompanyResponseDTO>> listCompanies(
            @Parameter(description = "Filtra por empresas residentes (true) ou não residentes (false). Omitir para listar todas.")
            @RequestParam(required = false) Boolean isResident
    );

    @Operation(
            summary = "Buscar empresa por ID",
            description = "Retorna os dados detalhados de uma empresa pelo seu identificador único (UUID)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "message": "Empresa não encontrada com o ID informado.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": null
                                    }
                                    """)))
    })
    @GetMapping("/{id}")
    ResponseEntity<CompanyResponseDTO> getCompanyById(
            @Parameter(
                    description = "Identificador único da empresa (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID id
    );

    @Operation(
            summary = "Atualizar empresa",
            description = "Atualiza os dados de uma empresa existente pelo seu identificador único (UUID)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empresa atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "message": "Dados de entrada inválidos.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": ["O nome pode ter no máximo 255 caracteres."]
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "message": "Empresa não encontrada com o ID informado.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": null
                                    }
                                    """))),
            @ApiResponse(responseCode = "409", description = "Nome da empresa já está em uso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 409,
                                      "message": "O nome da empresa já está em uso.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": null
                                    }
                                    """)))
    })
    @PutMapping("/{id}")
    ResponseEntity<CompanyResponseDTO> updateCompany(
            @Parameter(
                    description = "Identificador único da empresa (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID id,
            @Valid @RequestBody CompanyUpdateRequestDTO request
    );

    @Operation(
            summary = "Remover empresa",
            description = "Remove permanentemente uma empresa pelo seu identificador único (UUID)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Empresa removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empresa não encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 404,
                                      "message": "Empresa não encontrada com o ID informado.",
                                      "timestamp": "2026-06-10T00:00:00Z",
                                      "errors": null
                                    }
                                    """)))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCompany(
            @Parameter(
                    description = "Identificador único da empresa (UUID)",
                    required = true,
                    example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
            )
            @PathVariable UUID id
    );
}