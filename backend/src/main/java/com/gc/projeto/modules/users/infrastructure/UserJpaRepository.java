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
           "(u.deletedAt IS NULL) AND " +
           "(:username IS NULL OR u.username LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:companyId IS NULL OR u.companyId = :companyId) AND " +
           "(:featuredProgramId IS NULL OR u.featuredProgramId = :featuredProgramId)")
    Page<UserEntity> findAllByFilters(
            @Param("username") String username,
            @Param("companyId") UUID companyId,
            @Param("featuredProgramId") UUID featuredProgramId,
            Pageable pageable);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
