package com.dva.bookstore.book_service.dto;

import java.math.BigDecimal;

public record BookResponse(
        Integer id,
        String title,
        String description,
        BigDecimal price,
        String coverUrl,
        Integer publishedYear,
        Integer authorId,
        String authorName,
        Integer categoryId,
        String categoryName) {
}