package com.dva.bookstore.book_service.service;

import com.dva.bookstore.book_service.model.User;

public interface UserService {
    User getOrCreate(String keycloakId);
}