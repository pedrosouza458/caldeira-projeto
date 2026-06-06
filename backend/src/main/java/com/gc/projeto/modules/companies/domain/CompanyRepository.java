package com.gc.projeto.modules.companies.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Company save(Company company);
    void delete(UUID id);
    Optional<Company> findById(UUID id);
    Optional<Company> findByName(String name);
    List<Company> findAll();
    List<Company> findByIsResident(boolean isResident);
    boolean existsById(UUID id);
    boolean existsByName(String name);
}
