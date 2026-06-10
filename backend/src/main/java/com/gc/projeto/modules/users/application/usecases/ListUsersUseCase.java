package com.gc.projeto.modules.users.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.gc.projeto.modules.users.application.dtos.UserFilterInput;
import com.gc.projeto.modules.users.domain.User;
import com.gc.projeto.modules.users.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {
    
    private final UserRepository userRepository;

    public Page<User> execute(UserFilterInput filter, Pageable pageable) {
        Sort sort = pageable.getSort();

        if (sort.isUnsorted() || sort.stream().anyMatch(o -> o.getProperty().contains("string"))) {
            sort = Sort.by("name");
        }

        return userRepository.findAll(filter, PageRequest.of(pageable.getPageNumber(), 20, sort));
    }
}
