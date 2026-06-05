package com.gc.projeto.modules.users.presentation.dtos;

import java.util.UUID;

import com.gc.projeto.modules.users.application.dtos.CreateUserInput;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRequestDTO(
    @Schema(example = "932e674b-6ca8-413f-9f94-e3e6027383d3")
    UUID id,

    @Schema(example = "Fulano da Silva")
    String name,

    @Schema(example = "fulano.silva")
    String username,

    @Schema(example = "fulanosilva@gmail.com")
    String email,

    @Schema(example = "fulanodasilva.jpg")
    String profilePictureUrl,

    @Schema(example = "c3d537ac-aaa2-4d4f-a000-9a97db69163c")
    UUID companyId,

    @Schema(example = "b2368eb9-59f5-4c39-ad71-cfa5d04b6b20")
    UUID workZoneId,

    @Schema(example = "7e9dd187-0157-4c44-926e-43dc01a66db6")
    UUID featuredProgramId
) {
     public CreateUserInput toInput() {
        return new CreateUserInput(id, name, username, email, profilePictureUrl, companyId, workZoneId, featuredProgramId);
    }
}
