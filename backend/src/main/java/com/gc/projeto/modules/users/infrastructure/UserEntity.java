package com.gc.projeto.modules.users.infrastructure;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {
    /**
     * ID managed externally by Supabase Auth (auth.users).
     * Must be assigned before persisting — no auto-generation.
     */
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "company_id")
    private UUID companyId;

    @Column(name = "last_urgent_need")
    private Instant lastUrgentNeed;

    @Column(name = "work_zone_id")
    private UUID workZoneId;

    @Column(name = "actual_zone_id")
    private UUID actualZoneId;

    @Column(name = "featured_program_id")
    private UUID featuredProgramId;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

