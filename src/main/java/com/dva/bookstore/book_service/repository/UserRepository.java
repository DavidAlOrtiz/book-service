package com.dva.bookstore.book_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dva.bookstore.book_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
}