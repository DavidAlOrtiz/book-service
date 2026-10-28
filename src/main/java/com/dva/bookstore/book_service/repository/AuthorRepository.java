package com.dva.bookstore.book_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dva.bookstore.book_service.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
