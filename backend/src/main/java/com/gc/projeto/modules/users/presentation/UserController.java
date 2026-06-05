package com.gc.projeto.modules.users.presentation;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gc.projeto.modules.users.application.usecases.CreateUserUseCase;
import com.gc.projeto.modules.users.presentation.dtos.UserRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final CreateUserUseCase createUserUseCase;

    @PostMapping()
    public ResponseEntity<Map<String, String>> createUser(@Valid @RequestBody UserRequestDTO request){
        createUserUseCase.execute(request.toInput());
        Map<String, String> response = Map.of("message", "User created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
