package com.gc.projeto.modules.companies.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gc.projeto.modules.companies.application.dtos.CompanyFilterInput;
import com.gc.projeto.modules.companies.infrastructure.specifications.CompanySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryPostgres implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;
    private final CompanyMapper mapper;

    @Override
    public Company save(Company company) {
        CompanyEntity entityToSave = mapper.toEntity(company);
        CompanyEntity savedEntity = jpaRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Company> findByName(String name) {
        return jpaRepository.findByNameIgnoreCase(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<Company> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Company> findAll(CompanyFilterInput filter, Pageable pageable) {
        Specification<CompanyEntity> spec = Specification.where(CompanySpecifications.nameContainsIgnoreCase(filter.name()))
                .and(CompanySpecifications.isResidentEquals(filter.isResident()));

        return jpaRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}