package com.dva.bookstore.book_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank(message = "Name is required") @Size(max = 255, message = "Maximum 255 characters") String name,

        @Size(max = 255, message = "Maximum 255 characters") String bio) {
}
