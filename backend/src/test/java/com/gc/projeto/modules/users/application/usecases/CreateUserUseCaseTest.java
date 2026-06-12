package com.gc.projeto.modules.users.application.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gc.projeto.modules.users.application.dtos.CreateUserInput;
import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.domain.UserRepository;
import com.gc.projeto.modules.users.domain.exceptions.EmailAlreadyExistsException;
import com.gc.projeto.modules.users.domain.exceptions.UsernameAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
public class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private CreateUserInput input;

    @BeforeEach
    void setUp(){
        input = new CreateUserInput(
            UUID.randomUUID(),
            "Fulano da Silva",
            "fulano.silva",
            "fulanosilva@gmail.com",
            "fulanodasilva.png",
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
    }

    @Test
    @DisplayName("Should create a user with success")
    void shouldCreateUserWithSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        User userSaved = User.builder().id(UUID.randomUUID()).email(input.email()).build();
        when(userRepository.save(any(User.class))).thenReturn(userSaved);

        User result = createUserUseCase.execute(input);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw a exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        when(userRepository.existsByEmail(input.email())).thenReturn(true);
        
        assertThrows(EmailAlreadyExistsException.class, () -> {
            createUserUseCase.execute(input);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw a exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        when(userRepository.existsByUsername(input.username())).thenReturn(true);
        
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            createUserUseCase.execute(input);
        });

        verify(userRepository, never()).save(any());
    }
    
}
