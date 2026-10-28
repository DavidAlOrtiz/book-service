package com.dva.bookstore.book_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dva.bookstore.book_service.dto.FavoriteResponse;
import com.dva.bookstore.book_service.exception.ResourceNotFoundException;
import com.dva.bookstore.book_service.mapper.FavoriteMapper;
import com.dva.bookstore.book_service.model.Book;
import com.dva.bookstore.book_service.model.Favorite;
import com.dva.bookstore.book_service.model.FavoriteId;
import com.dva.bookstore.book_service.model.User;
import com.dva.bookstore.book_service.repository.BookRepository;
import com.dva.bookstore.book_service.repository.FavoriteRepository;
import com.dva.bookstore.book_service.service.FavoriteService;
import com.dva.bookstore.book_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional
    public List<FavoriteResponse> findMine(String keycloakId) {
        User user = userService.getOrCreate(keycloakId);
        log.info("fovorites find {}", user.getId());
        return favoriteRepository.findByIdUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(favoriteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void add(String keycloakId, Long bookId) {
        User user = userService.getOrCreate(keycloakId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        FavoriteId id = new FavoriteId(user.getId(), book.getId());
        if (favoriteRepository.existsById(id)) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setId(id);
        favorite.setUser(user);
        favorite.setBook(book);
        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void remove(String keycloakId, Long bookId) {
        User user = userService.getOrCreate(keycloakId);
        FavoriteId id = new FavoriteId(user.getId(), bookId);
        if (!favoriteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Favorite", "bookId", bookId);
        }
        favoriteRepository.deleteById(id);
        log.info("favorites deleted {}", user.getId());
    }
}