package com.gc.projeto.modules.companies.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {
    private UUID id;
    private String name;
    private String logo;
    private boolean isResident;
    private Instant createdAt;
    private Instant updatedAt;
}
