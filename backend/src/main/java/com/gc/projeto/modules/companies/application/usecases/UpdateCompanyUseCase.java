package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.application.dtos.UpdateCompanyInput;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCompanyUseCase {

    private final CompanyRepository companyRepository;

    @Transactional
    public Company execute(UpdateCompanyInput input) {
        var existing = companyRepository.findById(input.id())
                .orElseThrow(CompanyNotFoundException::new);

        companyRepository.findByName(input.name())
                .filter(c -> !c.getId().equals(input.id()))
                .ifPresent(c -> { throw new CompanyNameAlreadyExistsException(); });

        var updated = Company.builder()
                .id(existing.getId())
                .name(input.name())
                .logo(input.logo())
                .isResident(input.isResident())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();

        return companyRepository.save(updated);
    }
}