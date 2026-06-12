package com.gc.projeto.modules.users.presentation.dtos;

import java.time.Instant;
import java.util.UUID;

import com.gc.projeto.modules.users.domain.User;

public record UserResponseDTO(
    UUID id,
    String name,
    String username,
    String email,
    String profilePictureUrl,
    UUID companyId,
    UUID workZoneId,
    UUID actualZoneId,
    UUID featuredProgramId,
    Instant createdAt,
    Instant updatedAt
) {
    public UserResponseDTO(User user) {
        this(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getProfilePictureUrl(),
            user.getCompanyId(),
            user.getWorkZoneId(),
            user.getActualZoneId(),
            user.getFeaturedProgramId(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}