package com.gc.projeto.modules.companies.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.gc.projeto.modules.companies.application.dtos.UpdateCompanyInput;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
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
class UpdateCompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private UpdateCompanyUseCase updateCompanyUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar e retornar a empresa quando o ID existe e o novo nome está disponível")
    void execute_shouldUpdateAndReturnCompany_whenIdExistsAndNameIsAvailable() {
        var companyId = UUID.randomUUID();
        var input = new UpdateCompanyInput(companyId, "Novo Nome LTDA", "https://novo-logo.png", false);
        log.info("[ARRANGE] input -> id='{}', name='{}', logo='{}', isResident={}", input.id(), input.name(), input.logo(), input.isResident());

        var existingCompany = Company.builder()
                .id(companyId)
                .name("Nome Antigo LTDA")
                .logo("https://antigo-logo.png")
                .isResident(true)
                .createdAt(Instant.now().minusSeconds(3600))
                .updatedAt(Instant.now().minusSeconds(3600))
                .version(1L)
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.findByName(input.name())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        var result = updateCompanyUseCase.execute(input);

        log.info("[RESULT] atualizado -> id='{}', name='{}', logo='{}', isResident={}",
                result.getId(), result.getName(), result.getLogo(), result.getIsResident());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(companyId);
        assertThat(result.getName()).isEqualTo(input.name());
        assertThat(result.getLogo()).isEqualTo(input.logo());
        assertThat(result.getIsResident()).isEqualTo(input.isResident());
        assertThat(result.getCreatedAt()).isEqualTo(existingCompany.getCreatedAt());

        assertThat(result.getVersion()).isEqualTo(existingCompany.getVersion());
    }

    @Test
    @DisplayName("Deve permitir a atualização se o nome encontrado no banco pertencer à própria empresa sendo atualizada")
    void execute_shouldAllowUpdate_whenNameBelongsToTheSameCompany() {
        var companyId = UUID.randomUUID();
        var input = new UpdateCompanyInput(companyId, "Mesmo Nome S/A", "https://novo-logo.png", true);
        log.info("[ARRANGE] mantendo o mesmo nome da empresa -> id='{}', name='{}'", input.id(), input.name());

        var existingCompany = Company.builder()
                .id(companyId)
                .name("Mesmo Nome S/A")
                .logo("https://antigo-logo.png")
                .isResident(true)
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.findByName(input.name())).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        var result = updateCompanyUseCase.execute(input);

        log.info("[RESULT] atualização concluída com sucesso mantendo o nome original");
        assertThat(result).isNotNull();
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    @DisplayName("Deve chamar save exatamente uma vez ao atualizar empresa")
    void execute_shouldCallSaveOnce_whenUpdateIsSuccessful() {
        var companyId = UUID.randomUUID();
        var input = new UpdateCompanyInput(companyId, "Empresa Atualizada", "https://logo.png", true);
        log.info("[ARRANGE] preparando verificação de invocação do save para o id='{}'", companyId);

        var existingCompany = Company.builder().id(companyId).name("Nome Antigo").build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.findByName(input.name())).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        updateCompanyUseCase.execute(input);

        verify(companyRepository, times(1)).save(any(Company.class));
        log.info("[RESULT] companyRepository.save() invocado exatamente 1 vez — verificação passou");
    }

    // ─── exceções de negócio ─────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar CompanyNotFoundException quando o ID da empresa não existir")
    void execute_shouldThrowCompanyNotFoundException_whenIdDoesNotExist() {
        var input = new UpdateCompanyInput(UUID.randomUUID(), "Nome Qualquer", "https://logo.png", true);
        log.info("[ARRANGE] tentando atualizar ID inexistente='{}'", input.id());

        when(companyRepository.findById(input.id())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateCompanyUseCase.execute(input))
                .isInstanceOf(CompanyNotFoundException.class)
                .satisfies(ex -> log.info("[RESULT] exceção lançada -> '{}'", ex.getClass().getSimpleName()));

        verify(companyRepository, never()).findByName(any());
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar CompanyNameAlreadyExistsException quando o nome já estiver em uso por outra empresa")
    void execute_shouldThrowCompanyNameAlreadyExistsException_whenNameIsUsedByAnotherCompany() {
        var myCompanyId = UUID.randomUUID();
        var otherCompanyId = UUID.randomUUID();
        var input = new UpdateCompanyInput(myCompanyId, "Nome Conflitante LTDA", "https://logo.png", true);

        log.info("[ARRANGE] conflito de nome: minha empresa='{}', outra empresa='{}', nome pretendido='{}'",
                myCompanyId, otherCompanyId, input.name());

        var myCompany = Company.builder().id(myCompanyId).name("Meu Nome Antigo").build();
        var otherCompany = Company.builder().id(otherCompanyId).name("Nome Conflitante LTDA").build();

        when(companyRepository.findById(myCompanyId)).thenReturn(Optional.of(myCompany));
        when(companyRepository.findByName(input.name())).thenReturn(Optional.of(otherCompany));

        assertThatThrownBy(() -> updateCompanyUseCase.execute(input))
                .isInstanceOf(CompanyNameAlreadyExistsException.class)
                .satisfies(ex -> log.info("[RESULT] exceção lançada -> '{}'", ex.getClass().getSimpleName()));

        verify(companyRepository, never()).save(any());
        log.info("[RESULT] garantia de que o save NÃO foi acionado devido ao conflito de nomes");
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha na busca por nome")
    void execute_shouldPropagateException_whenFindByNameThrows() {
        var companyId = UUID.randomUUID();
        var input = new UpdateCompanyInput(companyId, "Nome Teste", "https://logo.png", true);
        log.info("[ARRANGE] simulando falha de infra no findByName para o id='{}'", companyId);

        var existingCompany = Company.builder().id(companyId).name("Nome Antigo").build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.findByName(input.name())).thenThrow(new RuntimeException("Query timed out"));

        assertThatThrownBy(() -> updateCompanyUseCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));

        verify(companyRepository, never()).save(any());
    }
}