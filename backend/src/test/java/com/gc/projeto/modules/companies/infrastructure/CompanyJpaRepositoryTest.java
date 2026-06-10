package com.gc.projeto.modules.companies.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        log.info("[ARRANGE] buscando por -> 'rESIDENT teCH'");

        Optional<CompanyEntity> found = repository.findByNameIgnoreCase("rESIDENT teCH");

        log.info("[RESULT] empresa encontrada -> presente={}", found.isPresent());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(residentCompany.getId());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por um nome inexistente")
    void findByNameIgnoreCase_shouldReturnEmpty_whenNameDoesNotExist() {
        log.info("[ARRANGE] buscando por -> 'Unknown'");

        Optional<CompanyEntity> found = repository.findByNameIgnoreCase("Unknown");

        log.info("[RESULT] empresa encontrada -> presente={}", found.isPresent());

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar se a empresa existe pelo nome ignorando case")
    void existsByNameIgnoreCase_shouldReturnTrue_whenNameMatchesIgnoringCase() {
        log.info("[ARRANGE] verificando existência de -> 'RESIDENT TECH', 'external corp' e 'Ghost'");

        boolean existsResident = repository.existsByNameIgnoreCase("RESIDENT TECH");
        boolean existsExternal = repository.existsByNameIgnoreCase("external corp");
        boolean notExists = repository.existsByNameIgnoreCase("Ghost");

        log.info("[RESULT] existsResident={}, existsExternal={}, notExists={}", existsResident, existsExternal, notExists);

        assertThat(existsResident).isTrue();
        assertThat(existsExternal).isTrue();
        assertThat(notExists).isFalse();
    }

    // ─── paginação e filtros ──────────────────────────────────────────────────

    @Test
    @DisplayName("Deve buscar empresas filtrando por status de residente com paginação")
    void findByIsResident_shouldReturnPaginatedCompanies_whenFilteringByStatus() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        log.info("[ARRANGE] buscando empresas residentes=true e residentes=false, página 0, tamanho 10");

        Page<CompanyEntity> residentPage = repository.findByIsResident(true, pageRequest);
        Page<CompanyEntity> nonResidentPage = repository.findByIsResident(false, pageRequest);

        log.info("[RESULT] residentes encontrados={}, não-residentes encontrados={}", 
                 residentPage.getTotalElements(), nonResidentPage.getTotalElements());

        assertThat(residentPage.getContent()).hasSize(1);
        assertThat(residentPage.getContent().get(0).getId()).isEqualTo(residentCompany.getId());

        assertThat(nonResidentPage.getContent()).hasSize(1);
        assertThat(nonResidentPage.getContent().get(0).getId()).isEqualTo(nonResidentCompany.getId());
    }
}