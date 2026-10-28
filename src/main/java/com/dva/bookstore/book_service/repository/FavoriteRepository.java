package com.dva.bookstore.book_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dva.bookstore.book_service.model.Favorite;
import com.dva.bookstore.book_service.model.FavoriteId;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    @EntityGraph(attributePaths = { "book", "book.author", "book.category" })
    List<Favorite> findByIdUserIdOrderByCreatedAtDesc(Long userId);
}