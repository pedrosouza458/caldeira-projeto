package com.gc.projeto.modules.users.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // Normalização inline do Sort para aceitar snake_case e limpar lixo do Swagger
        if (pageable.getSort().isSorted()) {
            var orders = pageable.getSort().stream()
                .filter(o -> !o.getProperty().contains("[") && !o.getProperty().equals("string"))
                .map(o -> {
                    String p = o.getProperty();
                    if (p.contains("_")) {
                        StringBuilder sb = new StringBuilder();
                        for (String s : p.split("_")) sb.append(sb.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1));
                        p = sb.toString();
                    }
                    return new org.springframework.data.domain.Sort.Order(o.getDirection(), p);
                }).toList();
            
            if (!orders.isEmpty()) {
                pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(), org.springframework.data.domain.Sort.by(orders));
            } else {
                pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(), pageable.getPageSize(), org.springframework.data.domain.Sort.by("name"));
            }
        }
        return userRepository.findAll(filter, pageable);
    }
}
