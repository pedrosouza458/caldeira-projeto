package com.gc.projeto.modules.users.presentation.dtos;

import java.util.UUID;

import com.gc.projeto.modules.users.application.dtos.UserFilterInput;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserFilterRequestDTO(
    @Schema(description = "Username filter (partial match)", example = "fulano.silva") 
    String username,

    @Schema(description = "Filter by company ID", example = "c3d537ac-aaa2-4d4f-a000-9a97db69163c") 
    UUID companyId,

    @Schema(description = "Filter by featured program ID", example = "7e9dd187-0157-4c44-926e-43dc01a66db6") 
    UUID featuredProgramId
) {        
    public UserFilterInput toInput() {
        return new UserFilterInput(username, companyId, featuredProgramId);
    }
}
