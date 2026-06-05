package com.gc.projeto.modules.users.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryPostgres implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entityToSave = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            jpaRepository.save(entity);
        });
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
       return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyId(companyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByActualZoneId(UUID actualZoneId) {
        return jpaRepository.findByActualZoneId(actualZoneId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
