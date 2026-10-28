package com.dva.bookstore.book_service.dto;

import java.time.LocalDateTime;

public record FavoriteResponse(BookResponse book, LocalDateTime addedAt) {
}