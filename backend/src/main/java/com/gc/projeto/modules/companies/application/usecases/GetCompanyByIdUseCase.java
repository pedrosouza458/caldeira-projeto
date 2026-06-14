package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetCompanyByIdUseCase {

    private final CompanyRepository companyRepository;

    public Company execute(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(CompanyNotFoundException::new);
    }
}