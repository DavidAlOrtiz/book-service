package com.dva.bookstore.book_service.dto;

import java.math.BigDecimal;

public record BookResponse(
                Long id,
                String title,
                String description,
                BigDecimal price,
                String coverUrl,
                Integer publishedYear,
                boolean hasPdf,
                Long authorId,
                String authorName,
                Long categoryId,
                String categoryName) {
}