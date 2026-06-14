package com.gc.projeto.modules.users.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.gc.projeto.modules.users.application.dtos.UserFilterInput;
import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ListUsersUseCaseTest {

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private ListUsersUseCase listUsersUseCase;

        @Test
        @DisplayName("Should return a paginated list of users")
        void shouldReturnPaginatedListOfUsers() {
                UserFilterInput filters = new UserFilterInput(
                                "fulano.silva",
                                UUID.randomUUID(),
                                UUID.randomUUID());

                Pageable pageable = PageRequest.of(0, 20);

                User user = User.builder()
                                .id(UUID.randomUUID())
                                .name("Fulano da Silva")
                                .build();

                Page<User> mockPage = new PageImpl<>(List.of(user), pageable, 1);

                when(userRepository.findAll(any(UserFilterInput.class), any(Pageable.class))).thenReturn(mockPage);

                Page<User> result = listUsersUseCase.execute(filters, pageable);

                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                assertEquals("Fulano da Silva", result.getContent().get(0).getName());

                verify(userRepository).findAll(eq(filters), eq(pageable));
        }

        @Test
        @DisplayName("Should return an empty page when no users found")
        void shouldReturnEmptyPageWhenNoUsersFound() {
                UserFilterInput filters = new UserFilterInput("fulano.silva", UUID.randomUUID(), UUID.randomUUID());
                Pageable pageable = PageRequest.of(0, 20);

                Page<User> emptyPage = Page.empty(pageable);

                when(userRepository.findAll(filters, pageable)).thenReturn(emptyPage);

                Page<User> result = listUsersUseCase.execute(filters, pageable);

                assertTrue(result.isEmpty());
                assertEquals(0, result.getTotalElements());
        }

}
