package com.gc.projeto.modules.companies.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.gc.projeto.modules.companies.application.dtos.CompanyFilterInput;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ListCompaniesUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ListCompaniesUseCase listCompaniesUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar página de todas as empresas quando os filtros forem nulos")
    void execute_shouldReturnAllCompanies_whenFiltersAreNull() {
        log.info("[ARRANGE] criando dados e definindo paginação sem filtros");

        var pageable = PageRequest.of(0, 10);
        var filter = new CompanyFilterInput(null, null);

        var company1 = Company.builder().id(UUID.randomUUID()).name("Empresa A").isResident(true).build();
        var company2 = Company.builder().id(UUID.randomUUID()).name("Empresa B").isResident(false).build();
        var companyPage = new PageImpl<>(List.of(company1, company2));

        when(companyRepository.findAll(eq(filter), any(Pageable.class))).thenReturn(companyPage);

        var result = listCompaniesUseCase.execute(filter, pageable);

        log.info("[RESULT] quantidade de empresas na página: {}", result.getContent().size());

        assertThat(result.getContent()).hasSize(2).containsExactly(company1, company2);
        assertThat(result.getTotalElements()).isEqualTo(2L);

        verify(companyRepository, times(1)).findAll(filter, pageable);
    }

    @Test
    @DisplayName("Deve retornar página de empresas repassando os filtros preenchidos")
    void execute_shouldReturnFilteredCompanies_whenFiltersAreProvided() {
        log.info("[ARRANGE] simulando filtros de nome e residente com paginação");

        var pageable = PageRequest.of(0, 10);
        var filter = new CompanyFilterInput("Tech", true);

        var company = Company.builder().id(UUID.randomUUID()).name("Tech Residente").isResident(true).build();
        var companyPage = new PageImpl<>(List.of(company));

        when(companyRepository.findAll(eq(filter), any(Pageable.class))).thenReturn(companyPage);

        var result = listCompaniesUseCase.execute(filter, pageable);

        log.info("[RESULT] quantidade de empresas filtradas na página: {}", result.getContent().size());

        assertThat(result.getContent()).hasSize(1).containsExactly(company);
        verify(companyRepository, times(1)).findAll(filter, pageable);
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha ao listar com paginação e filtros")
    void execute_shouldPropagateException_whenFindAllThrows() {
        log.info("[ARRANGE] simulando falha de banco no findAll paginado");
        var pageable = PageRequest.of(0, 10);
        var filter = new CompanyFilterInput(null, null);

        when(companyRepository.findAll(eq(filter), any(Pageable.class))).thenThrow(new RuntimeException("Database timeout"));

        assertThatThrownBy(() -> listCompaniesUseCase.execute(filter, pageable))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));
    }
}