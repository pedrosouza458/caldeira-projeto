package com.gc.projeto.modules.companies.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, UUID> {
    Optional<CompanyEntity> findByName(String name);
    List<CompanyEntity> findByIsResident(boolean isResident);
    boolean existsByName(String name);
}
