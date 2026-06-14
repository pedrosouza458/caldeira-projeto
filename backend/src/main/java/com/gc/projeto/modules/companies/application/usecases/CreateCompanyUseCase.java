package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import com.gc.projeto.modules.companies.application.dtos.CreateCompanyInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCompanyUseCase {

    private final CompanyRepository companyRepository;

    @Transactional
    public Company execute(CreateCompanyInput input) {
        if (companyRepository.existsByName(input.name())) {
            throw new CompanyNameAlreadyExistsException();
        }

        var company = Company.builder()
                .id(UUID.randomUUID())
                .name(input.name())
                .logo(input.logo())
                .isResident(input.isResident())
                .build();

        return companyRepository.save(company);
    }
}