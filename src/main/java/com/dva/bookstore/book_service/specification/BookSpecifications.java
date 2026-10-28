package com.dva.bookstore.book_service.specification;

import org.springframework.data.jpa.domain.Specification;

import com.dva.bookstore.book_service.model.Book;

public final class BookSpecifications {

    private BookSpecifications() {
    }

    public static Specification<Book> hasCategory(Integer categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Book> hasAuthor(Integer authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
}
