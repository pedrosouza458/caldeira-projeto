package com.gc.projeto.modules.users.application.usecases;

import org.springframework.stereotype.Service;

import com.gc.projeto.modules.users.application.dtos.CreateUserInput;
import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.domain.UserRepository;
import com.gc.projeto.modules.users.domain.exceptions.EmailAlreadyExistsException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;

    @Transactional
    public User execute(CreateUserInput input) {
        if (userRepository.findByEmail(input.email()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }
        
        var user = User.builder()
                .id(input.id())
                .name(input.name())
                .username(input.username())
                .email(input.email())
                .profilePictureUrl(input.profilePictureUrl())
                .companyId(input.companyId())
                .workZoneId(input.workZoneId())
                .featuredProgramId(input.featuredProgramId())
                .build();

        var savedUser = userRepository.save(user);

        return savedUser;
    }
}
