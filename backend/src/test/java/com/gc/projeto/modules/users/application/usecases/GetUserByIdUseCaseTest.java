package com.gc.projeto.modules.users.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.domain.UserRepository;
import com.gc.projeto.modules.users.domain.exceptions.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GetUserByIdUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserByIdUseCase getUserByIdUseCase;

    @Test
    @DisplayName("Should return a user when id exists")
    void shouldReturnUserWhenIdExists() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
        .id(userId)
        .name("Fulano da Silva")
        .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = getUserByIdUseCase.execute(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Fulano da Silva", result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when id does not exist")
    void shouldThrowUserNotFoundExceptionWhenIdDoesNotExists() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            getUserByIdUseCase.execute(userId);
        });

        verify(userRepository, times(1)).findById(userId);
    }
}
