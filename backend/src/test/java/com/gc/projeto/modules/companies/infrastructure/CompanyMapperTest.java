package com.gc.projeto.modules.companies.infrastructure;

import com.gc.projeto.modules.companies.domain.Company;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class CompanyMapperTest {

    private final CompanyMapper mapper = new CompanyMapper();

    // ─── mapeamento para entidade ─────────────────────────────────────────────

    @Test
    @DisplayName("Deve mapear domínio Company para CompanyEntity corretamente")
    void toEntity_shouldMapDomainToEntity_whenDomainIsValid() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Company domain = Company.builder()
                .id(id)
                .name("Tech Corp")
                .logo("https://logo.com/tech.png")
                .isResident(true)
                .createdAt(now)
                .updatedAt(now)
                .version(1L)
                .build();

        log.info("[ARRANGE] domain -> id='{}', name='{}', isResident={}", id, domain.getName(), domain.getIsResident());

        CompanyEntity entity = mapper.toEntity(domain);

        log.info("[RESULT] entity -> id='{}', name='{}', isNew={}", entity.getId(), entity.getName(), entity.isNew());

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Tech Corp");
        assertThat(entity.getLogo()).isEqualTo("https://logo.com/tech.png");
        assertThat(entity.getIsResident()).isTrue();
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
        assertThat(entity.getVersion()).isEqualTo(1L);
        assertThat(entity.isNew()).isFalse();
    }

    @Test
    @DisplayName("Deve definir isNew como verdadeiro quando createdAt for nulo")
    void toEntity_shouldMapIsNewToTrue_whenCreatedAtIsNull() {
        Company domain = Company.builder()
                .id(UUID.randomUUID())
                .name("New Corp")
                .createdAt(null)
                .build();

        log.info("[ARRANGE] domain -> createdAt=null");

        CompanyEntity entity = mapper.toEntity(domain);

        log.info("[RESULT] entity -> isNew={}", entity.isNew());

        assertThat(entity).isNotNull();
        assertThat(entity.isNew()).isTrue();
    }

    @Test
    @DisplayName("Deve retornar nulo quando o domínio Company for nulo")
    void toEntity_shouldReturnNull_whenDomainIsNull() {
        log.info("[ARRANGE] domain -> null");
        CompanyEntity entity = mapper.toEntity(null);
        log.info("[RESULT] entity -> {}", entity);
        assertThat(entity).isNull();
    }

    // ─── mapeamento para domínio ──────────────────────────────────────────────

    @Test
    @DisplayName("Deve mapear CompanyEntity para domínio Company corretamente")
    void toDomain_shouldMapEntityToDomain_whenEntityIsValid() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        CompanyEntity entity = CompanyEntity.builder()
                .id(id)
                .name("Entity Corp")
                .logo("https://logo.com/entity.png")
                .isResident(false)
                .createdAt(now)
                .updatedAt(now)
                .version(2L)
                .build();

        log.info("[ARRANGE] entity -> id='{}', name='{}', isResident={}", id, entity.getName(), entity.getIsResident());

        Company domain = mapper.toDomain(entity);

        log.info("[RESULT] domain -> id='{}', name='{}'", domain.getId(), domain.getName());

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Entity Corp");
        assertThat(domain.getLogo()).isEqualTo("https://logo.com/entity.png");
        assertThat(domain.getIsResident()).isFalse();
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now);
        assertThat(domain.getVersion()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Deve retornar nulo quando CompanyEntity for nula")
    void toDomain_shouldReturnNull_whenEntityIsNull() {
        log.info("[ARRANGE] entity -> null");
        Company domain = mapper.toDomain(null);
        log.info("[RESULT] domain -> {}", domain);
        assertThat(domain).isNull();
    }
}