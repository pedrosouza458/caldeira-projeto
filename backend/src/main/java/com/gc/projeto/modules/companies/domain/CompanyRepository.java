package com.gc.projeto.modules.companies.domain;

import com.gc.projeto.modules.companies.application.dtos.CompanyFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Company save(Company company);
    void delete(UUID id);
    Optional<Company> findById(UUID id);
    Optional<Company> findByName(String name);
    List<Company> findAll();
    Page<Company> findAll(CompanyFilterInput filter, Pageable pageable);
    boolean existsById(UUID id);
    boolean existsByName(String name);
}
