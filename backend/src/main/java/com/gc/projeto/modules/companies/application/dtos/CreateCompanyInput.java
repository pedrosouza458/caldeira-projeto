package com.gc.projeto.modules.companies.application.dtos;

import java.util.UUID;

public record CreateCompanyInput(
        UUID id,
        String name,
        String logo,
        Boolean isResident
){}

