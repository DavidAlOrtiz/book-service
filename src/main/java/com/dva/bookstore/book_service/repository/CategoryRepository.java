package com.dva.bookstore.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dva.bookstore.book_service.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
