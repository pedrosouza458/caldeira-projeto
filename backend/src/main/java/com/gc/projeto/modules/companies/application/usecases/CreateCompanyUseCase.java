package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.application.dtos.CreateCompanyInput;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .id(input.id())
                .name(input.name())
                .logo(input.logo())
                .isResident(input.isResident())
                .build();

        var savedCompany = companyRepository.save(company);

        return savedCompany;
    }
}
