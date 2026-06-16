package com.gc.projeto.modules.companies.presentation;

import com.gc.projeto.modules.companies.application.usecases.*;
import com.gc.projeto.modules.companies.domain.Company;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNameAlreadyExistsException;
import com.gc.projeto.modules.companies.domain.exceptions.CompanyNotFoundException;
import com.gc.projeto.shared.presentation.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockitoBean
    private UpdateCompanyUseCase updateCompanyUseCase;

    @MockitoBean
    private ListCompaniesUseCase listCompaniesUseCase;

    @MockitoBean
    private GetCompanyByIdUseCase getCompanyByIdUseCase;

    @MockitoBean
    private DeleteCompanyUseCase deleteCompanyUseCase;

    // ─── POST /companies (Criar Empresa) ──────────────────────────────────────

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

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

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

        when(createCompanyUseCase.execute(any())).thenThrow(new CompanyNameAlreadyExistsException());

        log.info("[ARRANGE] POST /companies -> name='Empresa Duplicada' (já cadastrada)");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    // ─── GET /companies (Listar Empresas Paginadas) ───────────────────────────

    @Test
    @DisplayName("GET /companies → 200 com lista paginada de empresas (Sem Filtros)")
    void listCompanies_shouldReturn200_withCompaniesList() throws Exception {
        var company = Company.builder()
                .id(UUID.randomUUID())
                .name("Empresa Listada")
                .logo("https://logo.png")
                .isResident(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(listCompaniesUseCase.execute(any(com.gc.projeto.modules.companies.application.dtos.CompanyFilterInput.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(company)));

        log.info("[ARRANGE] GET /companies (sem filtros)");

        var result = mockMvc.perform(get("/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Empresa Listada"))
                .andExpect(jsonPath("$.content[0].isResident").value(true))
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("GET /companies?isResident=true&name=Residente → 200 filtrando por empresas residentes e nome")
    void listCompanies_shouldReturn200_withFilteredCompaniesList() throws Exception {
        var company = Company.builder()
                .id(UUID.randomUUID())
                .name("Empresa Residente")
                .logo("https://logo.png")
                .isResident(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(listCompaniesUseCase.execute(any(com.gc.projeto.modules.companies.application.dtos.CompanyFilterInput.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(company)));

        log.info("[ARRANGE] GET /companies?isResident=true&name=Residente");

        var result = mockMvc.perform(get("/companies")
                        .param("isResident", "true")
                        .param("name", "Residente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Empresa Residente"))
                .andExpect(jsonPath("$.content[0].isResident").value(true))
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    // ─── GET /companies/{id} (Buscar por ID) ──────────────────────────────────

    @Test
    @DisplayName("GET /companies/{id} → 200 quando ID existe")
    void getCompanyById_shouldReturn200_whenIdExists() throws Exception {
        var id = UUID.randomUUID();
        var company = Company.builder()
                .id(id)
                .name("Empresa Encontrada")
                .logo("https://logo.png")
                .isResident(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(getCompanyByIdUseCase.execute(eq(id))).thenReturn(company);

        log.info("[ARRANGE] GET /companies/{}", id);

        var result = mockMvc.perform(get("/companies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Empresa Encontrada"))
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("GET /companies/{id} → 404 quando ID não existe")
    void getCompanyById_shouldReturn404_whenIdDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        when(getCompanyByIdUseCase.execute(eq(id))).thenThrow(new CompanyNotFoundException());

        log.info("[ARRANGE] GET /companies/{} (ID inexistente)", id);

        var result = mockMvc.perform(get("/companies/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    // ─── PUT /companies/{id} (Atualizar Empresa) ──────────────────────────────

    @Test
    @DisplayName("PUT /companies/{id} → 200 com empresa atualizada")
    void updateCompany_shouldReturn200_whenInputIsValidAndIdExists() throws Exception {
        var id = UUID.randomUUID();
        var body = """
                {
                    "name": "Nome Atualizado",
                    "logo": "https://novo-logo.png",
                    "isResident": false
                }
                """;

        var updatedCompany = Company.builder()
                .id(id)
                .name("Nome Atualizado")
                .logo("https://novo-logo.png")
                .isResident(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(updateCompanyUseCase.execute(any())).thenReturn(updatedCompany);

        log.info("[ARRANGE] PUT /companies/{} -> body={}", id, body.strip());

        var result = mockMvc.perform(put("/companies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Atualizado"))
                .andExpect(jsonPath("$.isResident").value(false))
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("PUT /companies/{id} → 404 quando ID não existe no update")
    void updateCompany_shouldReturn404_whenIdDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        var body = """
                {
                    "name": "Qualquer Nome",
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        when(updateCompanyUseCase.execute(any())).thenThrow(new CompanyNotFoundException());

        log.info("[ARRANGE] PUT /companies/{} -> tentando atualizar ID inexistente", id);

        var result = mockMvc.perform(put("/companies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("PUT /companies/{id} → 409 quando nome atualizado já está em uso (Adicionado)")
    void updateCompany_shouldReturn409_whenNameAlreadyExists() throws Exception {
        var id = UUID.randomUUID();
        var body = """
                {
                    "name": "Nome Duplicado",
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        when(updateCompanyUseCase.execute(any())).thenThrow(new CompanyNameAlreadyExistsException());

        log.info("[ARRANGE] PUT /companies/{} -> Nome Duplicado", id);

        var result = mockMvc.perform(put("/companies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    // ─── DELETE /companies/{id} (Deletar Empresa) ────────────────────────────

    @Test
    @DisplayName("DELETE /companies/{id} → 204 No Content quando deletado com sucesso")
    void deleteCompany_shouldReturn204_whenIdExists() throws Exception {
        var id = UUID.randomUUID();
        doNothing().when(deleteCompanyUseCase).execute(eq(id));

        log.info("[ARRANGE] DELETE /companies/{}", id);

        var result = mockMvc.perform(delete("/companies/{id}", id))
                .andExpect(status().isNoContent())
                .andReturn();

        log.info("[RESULT] status={}", result.getResponse().getStatus());
    }

    @Test
    @DisplayName("DELETE /companies/{id} → 404 quando ID não existe na deleção")
    void deleteCompany_shouldReturn404_whenIdDoesNotExist() throws Exception {
        var id = UUID.randomUUID();
        doThrow(new CompanyNotFoundException()).when(deleteCompanyUseCase).execute(eq(id));

        log.info("[ARRANGE] DELETE /companies/{} (ID inexistente)", id);

        var result = mockMvc.perform(delete("/companies/{id}", id))
                .andExpect(status().isNotFound())
                .andReturn();

        log.info("[RESULT] status={}, body={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    // ─── Validações de Payload (400 Bad Request) ──────────────────────────────

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

        log.info("[ARRANGE] POST /companies -> name=''");

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors", hasItem("name: O nome da empresa é obrigatório.")))
                .andReturn();

        log.info("[RESULT] status={}, errors={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando o logo está em branco (Adicionado)")
    void createCompany_shouldReturn400_whenLogoIsBlank() throws Exception {
        var body = """
                {
                    "name": "Empresa Valida",
                    "logo": "",
                    "isResident": true
                }
                """;

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("logo: A URL do logo é obrigatória.")))
                .andReturn();

        log.info("[RESULT] status={}, errors={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando a URL do logo é inválida (Adicionado)")
    void createCompany_shouldReturn400_whenLogoIsInvalidURL() throws Exception {
        var body = """
                {
                    "name": "Empresa Valida",
                    "logo": "link-invalido-qualquer",
                    "isResident": true
                }
                """;

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("logo: A URL do logo informada é inválida.")))
                .andReturn();

        log.info("[RESULT] status={}, errors={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("POST /companies → 400 quando isResident é nulo (Adicionado)")
    void createCompany_shouldReturn400_whenIsResidentIsNull() throws Exception {
        var body = """
                {
                    "name": "Empresa Valida",
                    "logo": "https://logo.png",
                    "isResident": null
                }
                """;

        var result = mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("isResident: O campo isResident é obrigatório.")))
                .andReturn();

        log.info("[RESULT] status={}, errors={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("PUT /companies/{id} → 400 quando campos enviados estão em branco (Adicionado)")
    void updateCompany_shouldReturn400_whenFieldsAreBlank() throws Exception {
        var id = UUID.randomUUID();
        var body = """
                {
                    "name": "",
                    "logo": "https://logo.png",
                    "isResident": true
                }
                """;

        var result = mockMvc.perform(put("/companies/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("name: O nome da empresa é obrigatório.")))
                .andReturn();

        log.info("[RESULT] status={}, errors={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
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
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andReturn();

        log.info("[RESULT] status={}, errors={}", result.getResponse().getStatus(), result.getResponse().getContentAsString());
    }
}