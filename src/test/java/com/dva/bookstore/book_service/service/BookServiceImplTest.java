package com.dva.bookstore.book_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.dva.bookstore.book_service.dto.BookRequest;
import com.dva.bookstore.book_service.dto.BookResponse;
import com.dva.bookstore.book_service.dto.PageResponse;
import com.dva.bookstore.book_service.exception.InvalidFileException;
import com.dva.bookstore.book_service.exception.ResourceNotFoundException;
import com.dva.bookstore.book_service.mapper.BookMapper;
import com.dva.bookstore.book_service.model.Author;
import com.dva.bookstore.book_service.model.Book;
import com.dva.bookstore.book_service.model.Category;
import com.dva.bookstore.book_service.repository.AuthorRepository;
import com.dva.bookstore.book_service.repository.BookRepository;
import com.dva.bookstore.book_service.repository.CategoryRepository;
import com.dva.bookstore.book_service.service.impl.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private com.dva.bookstore.book_service.validator.PdfValidator pdfValidator;

    @InjectMocks
    private BookServiceImpl service;

    private static BookRequest request(String pdfBase64) {
        return new BookRequest("Title", "Desc", new BigDecimal("99.90"), "http://cover",
                2000, 1L, 2L, pdfBase64);
    }

    private static BookResponse response() {
        return new BookResponse(10L, "Title", "Desc", new BigDecimal("99.90"), "http://cover",
                2000, false, 1L, "Author", 2L, "Category");
    }

    private void stubAuthorAndCategory(Author author, Category category) {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
    }

    @Test
    void search_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 6);
        Book book = new Book();
        BookResponse dto = response();

        when(bookRepository.findAll(ArgumentMatchers.<Specification<Book>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 1));
        when(bookMapper.toResponse(book)).thenReturn(dto);

        PageResponse<BookResponse> result = service.search(1, null, "  ", pageable);

        assertThat(result.content()).containsExactly(dto);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.page()).isZero();
        assertThat(result.last()).isTrue();
    }

    @Test
    void findById_found() {
        Book book = new Book();
        BookResponse dto = response();
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(dto);

        assertThat(service.findById(10L)).isEqualTo(dto);
    }

    @Test
    void findById_notFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withoutPdf() {
        Book entity = new Book();
        Author author = new Author();
        Category category = new Category();
        BookRequest req = request(null);
        BookResponse dto = response();

        when(bookMapper.toEntity(req)).thenReturn(entity);
        stubAuthorAndCategory(author, category);
        when(bookRepository.save(entity)).thenReturn(entity);
        when(bookMapper.toResponse(entity)).thenReturn(dto);

        BookResponse result = service.create(req);

        assertThat(result).isEqualTo(dto);
        assertThat(entity.getAuthor()).isSameAs(author);
        assertThat(entity.getCategory()).isSameAs(category);
        assertThat(entity.isHasPdf()).isFalse();
        verify(pdfValidator, never()).validate(any());
    }

    @Test
    void create_withPdf() {
        String pdf = Base64.getEncoder().encodeToString("%PDF-1.4".getBytes(StandardCharsets.US_ASCII));
        Book entity = new Book();
        BookRequest req = request(pdf);

        when(bookMapper.toEntity(req)).thenReturn(entity);
        stubAuthorAndCategory(new Author(), new Category());
        when(bookRepository.save(entity)).thenReturn(entity);
        when(bookMapper.toResponse(entity)).thenReturn(response());

        service.create(req);

        verify(pdfValidator).validate(pdf);
        assertThat(entity.getPdfBase64()).isEqualTo(pdf);
        assertThat(entity.isHasPdf()).isTrue();
    }

    @Test
    void create_invalidPdf_doesNotSave() {
        BookRequest req = request("basura");
        doThrow(new InvalidFileException("File is not a valid PDF")).when(pdfValidator).validate("basura");

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(InvalidFileException.class);

        verify(bookRepository, never()).save(any());
        verifyNoInteractions(bookMapper);
    }

    @Test
    void create_authorNotFound() {
        BookRequest req = request(null);
        when(bookMapper.toEntity(req)).thenReturn(new Book());
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void create_categoryNotFound() {
        BookRequest req = request(null);
        when(bookMapper.toEntity(req)).thenReturn(new Book());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(new Author()));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void update_nullPdf_keepsExisting() {
        Book book = new Book();
        book.setPdfBase64("old");
        book.setHasPdf(true);
        BookRequest req = request(null);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        stubAuthorAndCategory(new Author(), new Category());
        when(bookMapper.toResponse(book)).thenReturn(response());

        service.update(10L, req);

        verify(bookMapper).updateEntity(req, book);
        assertThat(book.getPdfBase64()).isEqualTo("old");
        assertThat(book.isHasPdf()).isTrue();
    }

    @Test
    void update_blankPdf_removesPdf() {
        Book book = new Book();
        book.setPdfBase64("old");
        book.setHasPdf(true);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        stubAuthorAndCategory(new Author(), new Category());
        when(bookMapper.toResponse(book)).thenReturn(response());

        service.update(10L, request(""));

        assertThat(book.getPdfBase64()).isNull();
        assertThat(book.isHasPdf()).isFalse();
        verify(pdfValidator, never()).validate(any());
    }

    @Test
    void update_newPdf_replacesExisting() {
        Book book = new Book();
        book.setPdfBase64("old");
        book.setHasPdf(true);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        stubAuthorAndCategory(new Author(), new Category());
        when(bookMapper.toResponse(book)).thenReturn(response());

        service.update(10L, request("NEWPDF"));

        verify(pdfValidator).validate("NEWPDF");
        assertThat(book.getPdfBase64()).isEqualTo("NEWPDF");
        assertThat(book.isHasPdf()).isTrue();
    }

    @Test
    void update_notFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request(null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_found() {
        Book book = new Book();
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        service.delete(10L);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_notFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPdf_returnsDecodedBytes() {
        byte[] original = "%PDF-1.4 hola".getBytes(StandardCharsets.US_ASCII);
        Book book = new Book();
        book.setHasPdf(true);
        book.setPdfBase64(Base64.getEncoder().encodeToString(original));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThat(service.getPdf(10L)).isEqualTo(original);
    }

    @Test
    void getPdf_noPdf_throws() {
        Book book = new Book();
        book.setHasPdf(false);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> service.getPdf(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
