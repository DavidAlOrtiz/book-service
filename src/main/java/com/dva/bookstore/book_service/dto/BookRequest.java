package com.dva.bookstore.book_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record BookRequest(
        @NotBlank(message = "Title is required") @Size(max = 255, message = "Maximum 255 characters") String title,

        @Size(max = 2000, message = "Maximum 2000 characters") String description,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.0", message = "Price cannot be negative") @Digits(integer = 8, fraction = 2, message = "Invalid price format") BigDecimal price,

        @Size(max = 255, message = "Maximum 255 characters") String coverUrl,

        @Min(value = 0, message = "Invalid year") @Max(value = 2100, message = "Invalid year") Integer publishedYear,

        @NotNull(message = "Author is required") Long authorId,

        @NotNull(message = "Category is required") Long categoryId,

        @Size(max = 13_981_016, message = "PDF must not exceed 10 MB") String pdfBase64) {
}