package com.dva.bookstore.book_service.service;

import java.util.List;

import com.dva.bookstore.book_service.dto.AuthorRequest;
import com.dva.bookstore.book_service.dto.AuthorResponse;

public interface AuthorService {

    List<AuthorResponse> findAll();

    AuthorResponse findById(Long id);

    AuthorResponse create(AuthorRequest request);

    AuthorResponse update(Long id, AuthorRequest request);

    void delete(Long id);
}
