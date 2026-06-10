package com.gc.projeto.modules.companies.domain;

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
    Page<Company> findAll(Pageable pageable);
    Page<Company> findByIsResident(boolean isResident, Pageable pageable);
    boolean existsById(UUID id);
    boolean existsByName(String name);
}
