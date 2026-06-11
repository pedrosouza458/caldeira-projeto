package com.gc.projeto.modules.users.presentation;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gc.projeto.modules.users.application.usecases.CreateUserUseCase;
import com.gc.projeto.modules.users.application.usecases.GetUserByIdUseCase;
import com.gc.projeto.modules.users.application.usecases.ListUsersUseCase;
import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.presentation.dtos.UserFilterRequestDTO;
import com.gc.projeto.modules.users.presentation.dtos.UserRequestDTO;
import com.gc.projeto.modules.users.presentation.dtos.UserResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserAPI {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ListUsersUseCase listUsersUseCase;

    @Override
    @PostMapping()
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        User user = createUserUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        User user = getUserByIdUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponseDTO(user));
    }

    @Override
    @GetMapping()
    public ResponseEntity<Page<UserResponseDTO>> listUsers(
            @ParameterObject @ModelAttribute UserFilterRequestDTO filters,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
        ) {
        var usersPage = listUsersUseCase.execute(filters.toInput(), pageable);
        Page<UserResponseDTO> response = usersPage.map(UserResponseDTO::new);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}