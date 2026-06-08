package com.gc.projeto.modules.users.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gc.projeto.modules.users.application.dtos.UserFilterInput;

public interface UserRepository {
    User save(User user);
    void delete(UUID id);

    Optional<User> findById(UUID id);

    Page<User> findAll(UserFilterInput filtrer, Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
