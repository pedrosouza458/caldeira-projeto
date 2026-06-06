package com.gc.projeto.modules.companies.presentation.dtos;

import java.time.Instant;
import java.util.UUID;
import com.gc.projeto.modules.companies.domain.Company;

public record CompanyResponseDTO(
        UUID id,
        String name,
        String logo,
        boolean isResident,
        Instant createdAt,
        Instant updatedAt
) {
    public CompanyResponseDTO(Company company) {
        this(
                company.getId(),
                company.getName(),
                company.getLogo(),
                company.isResident(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}