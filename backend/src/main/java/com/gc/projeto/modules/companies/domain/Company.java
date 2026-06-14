package com.gc.projeto.modules.companies.domain;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Company {

    @EqualsAndHashCode.Include
    private UUID id;

    private String name;
    private String logo;
    private Boolean isResident;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}
