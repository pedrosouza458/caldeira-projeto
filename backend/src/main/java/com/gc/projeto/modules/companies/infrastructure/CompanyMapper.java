package com.gc.projeto.modules.companies.infrastructure;

import org.springframework.stereotype.Component;
import com.gc.projeto.modules.companies.domain.Company;

@Component
public class CompanyMapper {

    public CompanyEntity toEntity(Company domain) {
        if (domain == null) {
            return null;
        }

        return CompanyEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .logo(domain.getLogo())
                .isResident(domain.isResident())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Company toDomain(CompanyEntity entity) {
        if (entity == null) {
            return null;
        }

        return Company.builder()
                .id(entity.getId())
                .name(entity.getName())
                .logo(entity.getLogo())
                .isResident(entity.isResident())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}