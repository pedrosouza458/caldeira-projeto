package com.gc.projeto.modules.users.infrastructure;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u FROM UserEntity u WHERE " +
           "(:username IS NULL OR u.username LIKE CONCAT('%', :username, '%')) AND " +
           "(:companyId IS NULL OR u.companyId = :companyId) AND " +
           "(:workZoneId IS NULL OR u.workZoneId = :workZoneId)")
    Page<UserEntity> findAllByFilters(
            @Param("username") String username,
            @Param("companyId") UUID companyId,
            @Param("workZoneId") UUID workZoneId,
            Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
