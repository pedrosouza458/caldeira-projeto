package com.gc.projeto.modules.users.presentation;

import java.util.Map;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gc.projeto.modules.users.presentation.dtos.UserFilterRequestDTO;
import com.gc.projeto.modules.users.presentation.dtos.UserRequestDTO;
import com.gc.projeto.modules.users.presentation.dtos.UserResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Users", description = "Endpoints para gerenciamento de usuários")
public interface UserAPI {

    @Operation(summary = "Criar um novo usuário", description = "Cria um novo usuário no sistema. O ID fornecido deve ser o UUID gerado pelo Supabase Auth.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de negócio"),
            @ApiResponse(responseCode = "409", description = "E-mail ou nome de usuário já está em uso")
    })
    public ResponseEntity<UserResponseDTO> createUser(UserRequestDTO request);

    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico através do seu ID único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, UserResponseDTO>> getUserById(
            @Parameter(name = "id", description = "ID do usuário", example = "932e674b-6ca8-413f-9f94-e3e6027383d3", required = true) UUID id);

    @Operation(summary = "Listar usuários", description = "Retorna uma lista paginada de usuários, permitindo filtros por nome de usuário, empresa e programa em destaque.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    public ResponseEntity<Page<UserResponseDTO>> listUsers(
            @ParameterObject @ModelAttribute UserFilterRequestDTO filters,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable);
}
