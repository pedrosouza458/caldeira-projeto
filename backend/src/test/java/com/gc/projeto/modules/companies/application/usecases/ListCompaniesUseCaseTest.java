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

@Slf4j
@ExtendWith(MockitoExtension.class)
class ListCompaniesUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private ListCompaniesUseCase listCompaniesUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar todas as empresas quando o filtro isResident for nulo")
    void execute_shouldReturnAllCompanies_whenIsResidentIsNull() {
        log.info("[ARRANGE] chamando listagem com isResident=null");

        var company1 = Company.builder().id(UUID.randomUUID()).name("Empresa A").isResident(true).build();
        var company2 = Company.builder().id(UUID.randomUUID()).name("Empresa B").isResident(false).build();

        when(companyRepository.findAll()).thenReturn(List.of(company1, company2));

        var result = listCompaniesUseCase.execute(null);

        log.info("[RESULT] quantidade de empresas retornadas: {}", result.size());
        assertThat(result).hasSize(2).containsExactly(company1, company2);
        verify(companyRepository, times(1)).findAll();
        verify(companyRepository, never()).findByIsResident(anyBoolean());
    }

    @Test
    @DisplayName("Deve retornar apenas empresas residentes quando o filtro isResident for verdadeiro")
    void execute_shouldReturnOnlyResidentCompanies_whenIsResidentIsTrue() {
        log.info("[ARRANGE] chamando listagem com isResident=true");

        var company = Company.builder().id(UUID.randomUUID()).name("Empresa Residente").isResident(true).build();
        when(companyRepository.findByIsResident(true)).thenReturn(List.of(company));

        var result = listCompaniesUseCase.execute(true);

        log.info("[RESULT] quantidade de empresas residentes retornadas: {}", result.size());
        assertThat(result).hasSize(1).containsExactly(company);
        verify(companyRepository, times(1)).findByIsResident(true);
        verify(companyRepository, never()).findAll();
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha ao listar tudo")
    void execute_shouldPropagateException_whenFindAllThrows() {
        log.info("[ARRANGE] simulando falha de banco no findAll");
        when(companyRepository.findAll()).thenThrow(new RuntimeException("Database timeout"));

        assertThatThrownBy(() -> listCompaniesUseCase.execute(null))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));
    }
}