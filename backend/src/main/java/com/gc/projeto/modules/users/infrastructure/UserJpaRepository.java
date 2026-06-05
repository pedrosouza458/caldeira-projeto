package com.gc.projeto.modules.users.infrastructure;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID>{
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserEntity> findByCompanyId(UUID companyId);
    List<UserEntity> findByActualZoneId(UUID companyId);
}
