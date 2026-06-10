package com.gc.projeto.modules.companies.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
import java.time.Instant;
import java.util.Optional;
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
class GetCompanyByIdUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private GetCompanyByIdUseCase getCompanyByIdUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar a empresa quando o ID informado existir")
    void execute_shouldReturnCompany_whenIdExists() {
        var id = UUID.randomUUID();
        log.info("[ARRANGE] buscando empresa pelo id='{}'", id);

        var company = Company.builder()
                .id(id)
                .name("Empresa Encontrada")
                .logo("https://logo.png")
                .isResident(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));

        var result = getCompanyByIdUseCase.execute(id);

        log.info("[RESULT] empresa encontrada -> id='{}', name='{}'", result.getId(), result.getName());
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Empresa Encontrada");
    }

    // ─── não encontrado ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar CompanyNotFoundException quando o ID não existir")
    void execute_shouldThrowCompanyNotFoundException_whenIdDoesNotExist() {
        var id = UUID.randomUUID();
        log.info("[ARRANGE] buscando id inexistente='{}'", id);

        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getCompanyByIdUseCase.execute(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .satisfies(ex -> log.info("[RESULT] exceção lançada -> '{}'", ex.getClass().getSimpleName()));
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha ao buscar por ID")
    void execute_shouldPropagateException_whenFindByIdThrows() {
        var id = UUID.randomUUID();
        log.info("[ARRANGE] simulando falha de infraestrutura ao buscar id='{}'", id);

        when(companyRepository.findById(id)).thenThrow(new RuntimeException("Connection error"));

        assertThatThrownBy(() -> getCompanyByIdUseCase.execute(id))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));
    }
}