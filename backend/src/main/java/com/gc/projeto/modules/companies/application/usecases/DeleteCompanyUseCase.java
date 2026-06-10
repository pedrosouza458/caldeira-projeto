package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCompanyUseCase {

    private final CompanyRepository companyRepository;

    public void execute(UUID id) {
        if (!companyRepository.existsById(id)) {
            throw new CompanyNotFoundException();
        }
        companyRepository.delete(id);
    }
}