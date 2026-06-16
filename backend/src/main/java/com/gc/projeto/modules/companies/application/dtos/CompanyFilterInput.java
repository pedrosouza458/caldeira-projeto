package com.gc.projeto.modules.companies.application.dtos;

public record CompanyFilterInput(
        String name,
        Boolean isResident
) {}
