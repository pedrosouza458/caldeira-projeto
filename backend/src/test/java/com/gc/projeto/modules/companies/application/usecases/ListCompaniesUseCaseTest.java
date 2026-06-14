package com.gc.projeto.modules.companies.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.PageImpl; // ← Importado
import org.springframework.data.domain.PageRequest; // ← Importado
import org.springframework.data.domain.Pageable; // ← Importado

@Slf4j
@ExtendWith(MockitoExtension.class)
class ListCompaniesUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ListCompaniesUseCase listCompaniesUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar página de todas as empresas quando o filtro isResident for nulo")
    void execute_shouldReturnAllCompanies_whenIsResidentIsNull() {
        log.info("[ARRANGE] criando dados e definindo paginação");

        var pageable = PageRequest.of(0, 10);
        var company1 = Company.builder().id(UUID.randomUUID()).name("Empresa A").isResident(true).build();
        var company2 = Company.builder().id(UUID.randomUUID()).name("Empresa B").isResident(false).build();
        var companyPage = new PageImpl<>(List.of(company1, company2));

        when(companyRepository.findAll(any(Pageable.class))).thenReturn(companyPage);

        var result = listCompaniesUseCase.execute(null, pageable);

        log.info("[RESULT] quantidade de empresas na página: {}", result.getContent().size());

        assertThat(result.getContent()).hasSize(2).containsExactly(company1, company2);
        assertThat(result.getTotalElements()).isEqualTo(2L);

        verify(companyRepository, times(1)).findAll(pageable);
        verify(companyRepository, never()).findByIsResident(anyBoolean(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar apenas empresas residentes de forma paginada quando o filtro isResident for verdadeiro")
    void execute_shouldReturnOnlyResidentCompanies_whenIsResidentIsTrue() {
        log.info("[ARRANGE] simulando filtro de residentes com paginação");

        var pageable = PageRequest.of(0, 10);
        var company = Company.builder().id(UUID.randomUUID()).name("Empresa Residente").isResident(true).build();
        var companyPage = new PageImpl<>(List.of(company));

        when(companyRepository.findByIsResident(eq(true), any(Pageable.class))).thenReturn(companyPage);

        var result = listCompaniesUseCase.execute(true, pageable);

        log.info("[RESULT] quantidade de empresas residentes na página: {}", result.getContent().size());

        assertThat(result.getContent()).hasSize(1).containsExactly(company);
        verify(companyRepository, times(1)).findByIsResident(true, pageable);
        verify(companyRepository, never()).findAll(any(Pageable.class));
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha ao listar tudo com paginação")
    void execute_shouldPropagateException_whenFindAllThrows() {
        log.info("[ARRANGE] simulando falha de banco no findAll paginado");
        var pageable = PageRequest.of(0, 10);

        when(companyRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Database timeout"));

        assertThatThrownBy(() -> listCompaniesUseCase.execute(null, pageable))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));
    }
}