package com.gc.projeto.modules.companies.presentation;

import com.gc.projeto.modules.companies.application.usecases.CreateCompanyUseCase;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import com.gc.projeto.shared.presentation.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(CompanyController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCompanyUseCase createCompanyUseCase;

    // ─── 201 Created ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /companies → 201 com corpo da empresa criada")
    void createCompany_shouldReturn201_whenInputIsValid() throws Exception {
        var body = """
                {
                    "name": "Empresa Teste",
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        var company = Company.builder()
                .id(UUID.randomUUID())
                .name("Empresa Teste")
                .logo("https://logo.png")
                .isResident(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(createCompanyUseCase.execute(any())).thenReturn(company);

        log.info("[ARRANGE] POST /companies -> body={}", body.strip());

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Empresa Teste"))
                .andExpect(jsonPath("$.logo").value("https://logo.png"))
                .andExpect(jsonPath("$.isResident").value(true))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andReturn();

        log.info("[RESULT] status={}, body={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    // ─── 409 Conflict ────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /companies → 409 quando o nome já existe")
    void createCompany_shouldReturn409_whenNameAlreadyExists() throws Exception {
        var body = """
                {
                    "name": "Empresa Duplicada",
                    "logo": "https://logo.png",
                    "isResident": false
                }
                """;

        when(createCompanyUseCase.execute(any()))
                .thenThrow(new CompanyNameAlreadyExistsException());

        log.info("[ARRANGE] POST /companies -> name='Empresa Duplicada' (já cadastrada)");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn();

        log.info("[RESULT] status={}, body={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    // ─── 500 Internal Server Error ────────────────────────────────────────────

    @Test
    @DisplayName("POST /companies → 500 quando o repositório lança exceção inesperada")
    void createCompany_shouldReturn500_whenRepositoryThrowsUnexpectedException() throws Exception {
        var body = """
                {
                    "name": "Empresa Teste",
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        when(createCompanyUseCase.execute(any()))
                .thenThrow(new RuntimeException("DB connection lost"));

        log.info("[ARRANGE] POST /companies -> simulando falha inesperada no repositório");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andReturn();

        log.info("[RESULT] status={}, body={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando o nome está em branco")
    void createCompany_shouldReturn400_whenNameIsBlank() throws Exception {
        var body = """
                {
                    "name": "",
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        log.info("[ARRANGE] POST /companies -> name='' (campo obrigatório em branco)");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@=~ /.*name.*/)]").exists())
                .andReturn();

        log.info("[RESULT] status={}, errors={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando o nome está ausente")
    void createCompany_shouldReturn400_whenNameIsNull() throws Exception {
        var body = """
                {
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        log.info("[ARRANGE] POST /companies -> campo 'name' ausente no payload");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@=~ /.*name.*/)]").exists())
                .andReturn();

        log.info("[RESULT] status={}, errors={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando o logo está ausente")
    void createCompany_shouldReturn400_whenLogoIsNull() throws Exception {
        var body = """
                {
                    "name": "Empresa Teste",
                    "isResident": true
                }
                """;

        log.info("[ARRANGE] POST /companies -> campo 'logo' ausente no payload");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@=~ /.*logo.*/)]").exists())
                .andReturn();

        log.info("[RESULT] status={}, errors={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando isResident está ausente")
    void createCompany_shouldReturn400_whenIsResidentIsNull() throws Exception {
        var body = """
                {
                    "name": "Empresa Teste",
                    "logo": "https://logo.png"
                }
                """;

        log.info("[ARRANGE] POST /companies -> campo 'isResident' ausente no payload");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@=~ /.*isResident.*/)]").exists())
                .andReturn();

        log.info("[RESULT] status={}, errors={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 com múltiplos erros quando todos os campos estão ausentes")
    void createCompany_shouldReturn400WithMultipleErrors_whenBodyIsEmpty() throws Exception {
        var body = "{}";

        log.info("[ARRANGE] POST /companies -> body vazio {}", body);

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andReturn();

        log.info("[RESULT] status={}, errors={}",
                result.getResponse().getStatus(),
                result.getResponse().getContentAsString());
    }
}