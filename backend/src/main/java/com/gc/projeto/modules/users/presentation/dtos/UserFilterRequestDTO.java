package com.gc.projeto.modules.users.presentation.dtos;

import java.util.UUID;

import com.gc.projeto.modules.users.application.dtos.UserFilterInput;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserFilterRequestDTO (

    @Schema(description = "Username filter (partial match)", example = "fulano.silva")
    String username, 

    @Schema(description = "Filter by company ID", example = "c3d537ac-aaa2-4d4f-a000-9a97db69163c")
    UUID companyId,

    @Schema(description = "Filter by work zone ID", example = "b2368eb9-59f5-4c39-ad71-cfa5d04b6b20")
    UUID workZoneId
    ) {
    public UserFilterInput toInput() {
        return new UserFilterInput(username, companyId, workZoneId);
    }
    }
