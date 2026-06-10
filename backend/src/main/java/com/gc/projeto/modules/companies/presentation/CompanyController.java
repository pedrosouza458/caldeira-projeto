package com.gc.projeto.modules.companies.presentation;

import com.gc.projeto.modules.companies.application.usecases.*;
import com.gc.projeto.modules.companies.presentation.dtos.CompanyRequestDTO;
import com.gc.projeto.modules.companies.presentation.dtos.CompanyResponseDTO;
import com.gc.projeto.modules.companies.presentation.dtos.CompanyUpdateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController implements CompanyAPI {

    private final CreateCompanyUseCase createCompanyUseCase;
    private final UpdateCompanyUseCase updateCompanyUseCase;
    private final ListCompaniesUseCase listCompaniesUseCase;
    private final GetCompanyByIdUseCase getCompanyByIdUseCase;
    private final DeleteCompanyUseCase deleteCompanyUseCase;

    @Override
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @Valid @RequestBody CompanyRequestDTO request) {
        var company = createCompanyUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CompanyResponseDTO(company));
    }

    @Override
    public ResponseEntity<Page<CompanyResponseDTO>> listCompanies(Boolean isResident, Pageable pageable) {
        var companies = listCompaniesUseCase.execute(isResident, pageable);
        Page<CompanyResponseDTO> responsePage = companies.map(CompanyResponseDTO::new);
        return ResponseEntity.ok(responsePage);
    }

    @Override
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable UUID id) {
        var company = getCompanyByIdUseCase.execute(id);
        return ResponseEntity.ok(new CompanyResponseDTO(company));
    }

    @Override
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyUpdateRequestDTO request) {
        var company = updateCompanyUseCase.execute(request.toInput(id));
        return ResponseEntity.ok(new CompanyResponseDTO(company));
    }

    @Override
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id) {
        deleteCompanyUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}