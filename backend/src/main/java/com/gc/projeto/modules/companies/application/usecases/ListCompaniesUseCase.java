package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCompaniesUseCase {

    private final CompanyRepository companyRepository;

    public Page<Company> execute(Boolean isResident, Pageable pageable) {
        if (isResident != null) {
            return companyRepository.findByIsResident(isResident, pageable);
        }
        return companyRepository.findAll(pageable);
    }
}