package com.dva.bookstore.book_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dva.bookstore.book_service.model.User;
import com.dva.bookstore.book_service.repository.UserRepository;
import com.dva.bookstore.book_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User getOrCreate(String keycloakId) {
        log.info("Finding users");
        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setKeycloakId(keycloakId);
                    return userRepository.save(user);
                });
    }
}