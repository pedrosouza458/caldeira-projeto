package com.gc.projeto.modules.users.application.dtos;

import java.util.UUID;

public record UserFilterInput(String username, UUID companyId, UUID featuredProgramId) {};