package com.dva.bookstore.book_service.service;

import org.springframework.data.domain.Pageable;

import com.dva.bookstore.book_service.dto.BookRequest;
import com.dva.bookstore.book_service.dto.BookResponse;
import com.dva.bookstore.book_service.dto.PageResponse;

public interface BookService {

    PageResponse<BookResponse> search(Integer categoryId, Integer authorId, String title, Pageable pageable);

    BookResponse findById(Long id);

    BookResponse create(BookRequest request);

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);

    byte[] getPdf(Long id);

}
