package com.dva.bookstore.book_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "the name is required") @Size(max = 255, message = "max 255 charactees") String name) {
}