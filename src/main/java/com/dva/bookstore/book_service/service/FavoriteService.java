package com.dva.bookstore.book_service.service;

import java.util.List;

import com.dva.bookstore.book_service.dto.FavoriteResponse;

public interface FavoriteService {

    List<FavoriteResponse> findMine(String keycloakId);

    void add(String keycloakId, Long bookId);

    void remove(String keycloakId, Long bookId);
}