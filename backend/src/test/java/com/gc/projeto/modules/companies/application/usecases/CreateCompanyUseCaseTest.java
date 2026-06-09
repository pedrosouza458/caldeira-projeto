package com.gc.projeto.modules.companies.application.usecases;

import com.gc.projeto.modules.companies.application.dtos.CreateCompanyInput;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.CompanyRepository;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CreateCompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CreateCompanyUseCase createCompanyUseCase;

    // ─── happy path ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar e retornar a empresa quando o nome não existe")
    void execute_shouldCreateAndReturnCompany_whenNameIsAvailable() {
        var input = new CreateCompanyInput("Empresa Teste", "https://logo.png", true);
        log.info("[ARRANGE] input -> name='{}', logo='{}', isResident={}", input.name(), input.logo(), input.isResident());

        var savedCompany = Company.builder()
                .id(UUID.randomUUID())
                .name(input.name())
                .logo(input.logo())
                .isResident(input.isResident())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(companyRepository.existsByName(input.name())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);

        var result = createCompanyUseCase.execute(input);

        log.info("[RESULT] id='{}', name='{}', logo='{}', isResident={}, createdAt={}, updatedAt={}",
                result.getId(), result.getName(), result.getLogo(),
                result.isResident(), result.getCreatedAt(), result.getUpdatedAt());

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(input.name());
        assertThat(result.getLogo()).isEqualTo(input.logo());
        assertThat(result.isResident()).isEqualTo(input.isResident());
        assertThat(result.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve gerar um UUID único para cada empresa criada")
    void execute_shouldGenerateUniqueId_forEachCompany() {
        var input = new CreateCompanyInput("Empresa Teste", "https://logo.png", false);
        log.info("[ARRANGE] input -> name='{}'", input.name());

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        when(companyRepository.existsByName(any())).thenReturn(false);
        when(companyRepository.save(companyCaptor.capture())).thenAnswer(i -> i.getArgument(0));

        createCompanyUseCase.execute(input);

        var captured = companyCaptor.getValue();
        log.info("[RESULT] UUID gerado -> '{}'", captured.getId());

        assertThat(captured.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve chamar save exatamente uma vez ao criar empresa")
    void execute_shouldCallSaveOnce_whenNameIsAvailable() {
        var input = new CreateCompanyInput("Empresa Teste", "https://logo.png", true);
        log.info("[ARRANGE] input -> name='{}'", input.name());

        when(companyRepository.existsByName(input.name())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        createCompanyUseCase.execute(input);

        verify(companyRepository, times(1)).save(any(Company.class));
        log.info("[RESULT] companyRepository.save() invocado exatamente 1 vez — verificação passou");
    }

    @Test
    @DisplayName("Deve persistir exatamente os dados recebidos no input")
    void execute_shouldPersistExactInputData() {
        var input = new CreateCompanyInput("Empresa XYZ", "https://logo-xyz.png", false);
        log.info("[ARRANGE] input -> name='{}', logo='{}', isResident={}", input.name(), input.logo(), input.isResident());

        ArgumentCaptor<Company> captor = ArgumentCaptor.forClass(Company.class);
        when(companyRepository.existsByName(any())).thenReturn(false);
        when(companyRepository.save(captor.capture())).thenAnswer(i -> i.getArgument(0));

        createCompanyUseCase.execute(input);

        var persisted = captor.getValue();
        log.info("[RESULT] dados persistidos -> name='{}', logo='{}', isResident={}",
                persisted.getName(), persisted.getLogo(), persisted.isResident());

        assertThat(persisted.getName()).isEqualTo("Empresa XYZ");
        assertThat(persisted.getLogo()).isEqualTo("https://logo-xyz.png");
        assertThat(persisted.isResident()).isFalse();
    }

    // ─── nome duplicado ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar CompanyNameAlreadyExistsException quando o nome já existe")
    void execute_shouldThrowCompanyNameAlreadyExistsException_whenNameAlreadyExists() {
        var input = new CreateCompanyInput("Empresa Duplicada", "https://logo.png", true);
        log.info("[ARRANGE] input -> name='{}' (nome já existente no repositório)", input.name());

        when(companyRepository.existsByName(input.name())).thenReturn(true);

        assertThatThrownBy(() -> createCompanyUseCase.execute(input))
                .isInstanceOf(CompanyNameAlreadyExistsException.class)
                .satisfies(ex -> log.info("[RESULT] exceção lançada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));
    }

    @Test
    @DisplayName("Não deve chamar save quando o nome já existe")
    void execute_shouldNotCallSave_whenNameAlreadyExists() {
        var input = new CreateCompanyInput("Empresa Duplicada", "https://logo.png", true);
        log.info("[ARRANGE] input -> name='{}' (nome já existente no repositório)", input.name());

        when(companyRepository.existsByName(input.name())).thenReturn(true);

        assertThatThrownBy(() -> createCompanyUseCase.execute(input))
                .isInstanceOf(CompanyNameAlreadyExistsException.class);

        verify(companyRepository, never()).save(any());
        log.info("[RESULT] companyRepository.save() não foi invocado — verificação passou");
    }

    // ─── falhas de infraestrutura ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha ao verificar o nome")
    void execute_shouldPropagateException_whenExistsByNameThrows() {
        var input = new CreateCompanyInput("Empresa Teste", "https://logo.png", true);
        log.info("[ARRANGE] input -> name='{}' (simulando falha em existsByName)", input.name());

        when(companyRepository.existsByName(input.name()))
                .thenThrow(new RuntimeException("DB connection lost"));

        assertThatThrownBy(() -> createCompanyUseCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));

        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falha ao salvar")
    void execute_shouldPropagateException_whenSaveThrows() {
        var input = new CreateCompanyInput("Empresa Teste", "https://logo.png", true);
        log.info("[ARRANGE] input -> name='{}' (simulando falha em save)", input.name());

        when(companyRepository.existsByName(input.name())).thenReturn(false);
        when(companyRepository.save(any(Company.class)))
                .thenThrow(new RuntimeException("Constraint violation"));

        assertThatThrownBy(() -> createCompanyUseCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .satisfies(ex -> log.info("[RESULT] exceção propagada -> '{}': {}",
                        ex.getClass().getSimpleName(), ex.getMessage()));
    }
}