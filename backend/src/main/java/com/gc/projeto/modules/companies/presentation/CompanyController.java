package com.gc.projeto.modules.companies.presentation;

import com.gc.projeto.modules.companies.presentation.dtos.CompanyResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gc.projeto.modules.companies.application.usecases.CreateCompanyUseCase;
import com.gc.projeto.modules.companies.presentation.dtos.CompanyRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CreateCompanyUseCase createCompanyUseCase;

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO request) {
        var company = createCompanyUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CompanyResponseDTO(company));
    }
}