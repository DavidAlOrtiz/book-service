package com.dva.bookstore.book_service.service;

import java.util.List;

import com.dva.bookstore.book_service.dto.CategoryRequest;
import com.dva.bookstore.book_service.dto.CategoryResponse;

public interface CategoryService {

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);

}
