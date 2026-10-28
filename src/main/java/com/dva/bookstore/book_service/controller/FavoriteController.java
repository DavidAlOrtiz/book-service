package com.dva.bookstore.book_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dva.bookstore.book_service.dto.FavoriteResponse;
import com.dva.bookstore.book_service.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public List<FavoriteResponse> findMine(@RequestHeader("X-User-Id") String userId) {
        return favoriteService.findMine(userId);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Void> add(@RequestHeader("X-User-Id") String userId,
            @PathVariable Long bookId) {
        favoriteService.add(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> remove(@RequestHeader("X-User-Id") String userId,
            @PathVariable Long bookId) {
        favoriteService.remove(userId, bookId);
        return ResponseEntity.noContent().build();
    }
}