package com.dva.bookstore.book_service.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dva.bookstore.book_service.dto.BookRequest;
import com.dva.bookstore.book_service.dto.BookResponse;
import com.dva.bookstore.book_service.dto.PageResponse;
import com.dva.bookstore.book_service.exception.ResourceNotFoundException;
import com.dva.bookstore.book_service.mapper.BookMapper;
import com.dva.bookstore.book_service.model.Author;
import com.dva.bookstore.book_service.model.Book;
import com.dva.bookstore.book_service.model.Category;
import com.dva.bookstore.book_service.repository.AuthorRepository;
import com.dva.bookstore.book_service.repository.BookRepository;
import com.dva.bookstore.book_service.repository.CategoryRepository;
import com.dva.bookstore.book_service.service.BookService;
import com.dva.bookstore.book_service.specification.BookSpecifications;
import com.dva.bookstore.book_service.validator.PdfValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookMapper bookMapper;
    private final PdfValidator pdfValidator;

    @Override
    public PageResponse<BookResponse> search(Integer categoryId, Integer authorId, String title, Pageable pageable) {
        List<Specification<Book>> specs = new ArrayList<>();
        if (categoryId != null) {
            specs.add(BookSpecifications.hasCategory(categoryId));
        }
        if (authorId != null) {
            specs.add(BookSpecifications.hasAuthor(authorId));
        }
        if (title != null && !title.isBlank()) {
            specs.add(BookSpecifications.titleContains(title.trim()));
        }
        return PageResponse.from(
                bookRepository.findAll(Specification.allOf(specs), pageable)
                        .map(bookMapper::toResponse));
    }

    @Override
    public BookResponse findById(Long id) {
        return bookMapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public BookResponse create(BookRequest request) {
        validatePdfIfPresent(request.pdfBase64());

        Book book = bookMapper.toEntity(request);
        book.setAuthor(getAuthor(request.authorId()));
        book.setCategory(getCategory(request.categoryId()));
        applyPdf(book, request.pdfBase64());

        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        validatePdfIfPresent(request.pdfBase64());

        Book book = getOrThrow(id);
        bookMapper.updateEntity(request, book);
        book.setAuthor(getAuthor(request.authorId()));
        book.setCategory(getCategory(request.categoryId()));
        applyPdf(book, request.pdfBase64());

        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bookRepository.delete(getOrThrow(id));
    }

    @Override
    public byte[] getPdf(Long id) {
        Book book = getOrThrow(id);
        if (!book.isHasPdf() || book.getPdfBase64() == null) {
            throw new ResourceNotFoundException("Book PDF", "bookId", id);
        }
        return Base64.getDecoder().decode(book.getPdfBase64());
    }

    private void applyPdf(Book book, String pdfBase64) {
        if (pdfBase64 == null) {
            return;
        }
        if (pdfBase64.isBlank()) {
            book.setPdfBase64(null);
            book.setHasPdf(false);
            return;
        }
        book.setPdfBase64(pdfBase64);
        book.setHasPdf(true);
    }

    private void validatePdfIfPresent(String pdfBase64) {
        if (pdfBase64 != null && !pdfBase64.isBlank()) {
            pdfValidator.validate(pdfBase64);
        }
    }

    private Book getOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
    }

    private Author getAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}