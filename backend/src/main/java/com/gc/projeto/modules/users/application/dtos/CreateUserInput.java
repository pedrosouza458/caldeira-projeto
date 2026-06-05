package com.gc.projeto.modules.users.application.dtos;

import java.util.UUID;

public record CreateUserInput(
    UUID id,
    String name,
    String username,
    String email,
    String profilePictureUrl,
    UUID companyId,
    UUID workZoneId,
    UUID featuredProgramId
){}
