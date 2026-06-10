package com.gc.projeto.modules.companies.application.dtos;

import java.util.UUID;

public record UpdateCompanyInput(
        UUID id,
        String name,
        String logo,
        Boolean isResident
) {}
