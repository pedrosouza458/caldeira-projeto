package com.gc.projeto.modules.companies.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {
    Optional<CompanyEntity> findByNameIgnoreCase(String name);
    Page<CompanyEntity> findByIsResident(boolean isResident, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);
}
