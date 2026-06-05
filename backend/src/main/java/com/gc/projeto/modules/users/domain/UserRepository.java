package com.gc.projeto.modules.users.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    void delete(UUID id);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    List<User> findByCompanyId(UUID companyId);
    List<User> findByActualZoneId(UUID actualZoneId);
    boolean existsById(UUID id);
    boolean existsByUsername(String username);
}
