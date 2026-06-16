package com.gc.projeto.modules.companies.infrastructure;

import com.gc.projeto.modules.companies.infrastructure.specifications.CompanySpecifications;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
class CompanyJpaRepositoryTest {

    @Autowired
    private CompanyJpaRepository repository;

    private CompanyEntity residentCompany;
    private CompanyEntity nonResidentCompany;

    @BeforeEach
    void setUp() {
        residentCompany = CompanyEntity.builder()
                .id(UUID.randomUUID())
                .name("Resident Tech")
                .logo("https://logo.com/resident.png")
                .isResident(true)
                .isNew(true)
                .build();

        nonResidentCompany = CompanyEntity.builder()
                .id(UUID.randomUUID())
                .name("External Corp")
                .logo("https://logo.com/external.png")
                .isResident(false)
                .isNew(true)
                .build();

        repository.save(residentCompany);
        repository.save(nonResidentCompany);
        log.info("[SETUP] Empresas de teste inseridas no banco H2 (Resident Tech, External Corp)");
    }

    // ─── buscas por nome ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve encontrar empresa pelo nome ignorando case (maiúsculas/minúsculas)")
    void findByNameIgnoreCase_shouldReturnCompany_whenNameMatchesIgnoringCase() {
        Optional<CompanyEntity> found = repository.findByNameIgnoreCase("rESIDENT teCH");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(residentCompany.getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por um nome inexistente")
    void findByNameIgnoreCase_shouldReturnEmpty_whenNameDoesNotExist() {
        Optional<CompanyEntity> found = repository.findByNameIgnoreCase("Unknown");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar se a empresa existe pelo nome ignorando case")
    void existsByNameIgnoreCase_shouldReturnTrue_whenNameMatchesIgnoringCase() {
        assertThat(repository.existsByNameIgnoreCase("RESIDENT TECH")).isTrue();
        assertThat(repository.existsByNameIgnoreCase("Ghost")).isFalse();
    }

    // ─── paginação e specifications (Testes Adicionados) ──────────────────────

    @Test
    @DisplayName("Deve buscar empresas utilizando Specification para nome parcial (ignorando case)")
    void findAll_shouldReturnPaginatedCompanies_whenFilteringByNameSpec() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Specification<CompanyEntity> spec = Specification.where(CompanySpecifications.nameContainsIgnoreCase("tech"));

        Page<CompanyEntity> resultPage = repository.findAll(spec, pageRequest);

        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getName()).isEqualTo("Resident Tech");
    }

    @Test
    @DisplayName("Deve buscar empresas utilizando Specification para isResident")
    void findAll_shouldReturnPaginatedCompanies_whenFilteringByIsResidentSpec() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Specification<CompanyEntity> spec = Specification.where(CompanySpecifications.isResidentEquals(false));

        Page<CompanyEntity> resultPage = repository.findAll(spec, pageRequest);

        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getName()).isEqualTo("External Corp");
    }

    @Test
    @DisplayName("Deve retornar todas as empresas quando as Specifications forem geradas a partir de parâmetros nulos")
    void findAll_shouldReturnAllCompanies_whenSpecsAreNull() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Specification<CompanyEntity> spec = Specification.where(CompanySpecifications.nameContainsIgnoreCase(null))
                .and(CompanySpecifications.isResidentEquals(null));

        Page<CompanyEntity> resultPage = repository.findAll(spec, pageRequest);

        assertThat(resultPage.getTotalElements()).isEqualTo(2);
    }
}