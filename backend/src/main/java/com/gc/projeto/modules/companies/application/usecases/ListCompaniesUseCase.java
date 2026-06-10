package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListCompaniesUseCase {

    private final CompanyRepository companyRepository;

    public List<Company> execute(Boolean isResident) {
        if (isResident != null) {
            return companyRepository.findByIsResident(isResident);
        }
        return companyRepository.findAll();
    }
}