package com.gc.projeto.modules.users.infrastructure;

import org.springframework.stereotype.Component;

import com.gc.projeto.modules.users.domain.User;

@Component
public class UserMapper {
    private UserMapper() {};

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .profilePictureUrl(domain.getProfilePictureUrl())
                .companyId(domain.getCompanyId())
                .lastUrgentNeed(domain.getLastUrgentNeed())
                .workZoneId(domain.getWorkZoneId())
                .actualZoneId(domain.getActualZoneId())
                .featuredProgramId(domain.getFeaturedProgramId())
                .deletedAt(domain.getDeletedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .profilePictureUrl(entity.getProfilePictureUrl())
                .companyId(entity.getCompanyId())
                .lastUrgentNeed(entity.getLastUrgentNeed())
                .workZoneId(entity.getWorkZoneId())
                .actualZoneId(entity.getActualZoneId())
                .featuredProgramId(entity.getFeaturedProgramId())
                .deletedAt(entity.getDeletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
