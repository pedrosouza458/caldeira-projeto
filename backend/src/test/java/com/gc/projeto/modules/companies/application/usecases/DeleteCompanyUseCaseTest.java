package com.gc.projeto.modules.companies.application.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
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
class DeleteCompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private DeleteCompanyUseCase deleteCompanyUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve deletar a empresa com sucesso quando o ID existir")
    void execute_shouldDeleteCompany_whenIdExists() {
        var id = UUID.randomUUID();
        log.info("[ARRANGE] preparando deleção do id='{}'", id);

        when(companyRepository.existsById(id)).thenReturn(true);
        doNothing().when(companyRepository).delete(id);

        deleteCompanyUseCase.execute(id);

        verify(companyRepository, times(1)).delete(id);
        log.info("[RESULT] empresa deletada e companyRepository.delete() invocado perfeitamente");
    }

    // ─── não encontrado ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar CompanyNotFoundException e não deletar se o ID não existir")
    void execute_shouldThrowCompanyNotFoundException_whenIdDoesNotExist() {
        var id = UUID.randomUUID();
        log.info("[ARRANGE] tentando deletar id inexistente='{}'", id);

        when(companyRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> deleteCompanyUseCase.execute(id))
                .isInstanceOf(CompanyNotFoundException.class)
                .satisfies(ex -> log.info("[RESULT] exceção lançada -> '{}'", ex.getClass().getSimpleName()));

        verify(companyRepository, never()).delete(any());
        log.info("[RESULT] garantia de que companyRepository.delete() NÃO foi chamado");
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção se a verificação de existência falhar")
    void execute_shouldPropagateException_whenExistsByIdThrows() {
        var id = UUID.randomUUID();
        log.info("[ARRANGE] simulando erro no método existsById para o id='{}'", id);

        when(companyRepository.existsById(id)).thenThrow(new RuntimeException("Table locked"));

        assertThatThrownBy(() -> deleteCompanyUseCase.execute(id))
                .isInstanceOf(RuntimeException.class);

        verify(companyRepository, never()).delete(any());
        log.info("[RESULT] execução interrompida antes do método delete ser chamado");
    }
}