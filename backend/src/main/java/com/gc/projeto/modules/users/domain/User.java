package com.gc.projeto.modules.users.domain;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String profilePictureUrl;
    private UUID companyId;
    private Instant lastUrgentNeed;
    private UUID workZoneId;
    private UUID actualZoneId;
    private UUID featuredProgramId;
    private Instant deletedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
