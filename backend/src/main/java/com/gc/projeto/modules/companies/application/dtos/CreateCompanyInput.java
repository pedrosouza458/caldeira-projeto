package com.gc.projeto.modules.companies.application.dtos;

public record CreateCompanyInput(
        String name,
        String logo,
        Boolean isResident
) {}


