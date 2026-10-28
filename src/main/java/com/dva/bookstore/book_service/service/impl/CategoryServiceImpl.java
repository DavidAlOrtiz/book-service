package com.dva.bookstore.book_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dva.bookstore.book_service.dto.CategoryRequest;
import com.dva.bookstore.book_service.dto.CategoryResponse;
import com.dva.bookstore.book_service.exception.DuplicateResourceException;
import com.dva.bookstore.book_service.exception.ResourceNotFoundException;
import com.dva.bookstore.book_service.mapper.CategoryMapper;
import com.dva.bookstore.book_service.model.Category;
import com.dva.bookstore.book_service.repository.CategoryRepository;
import com.dva.bookstore.book_service.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        log.info("finding all categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        log.info("finding category {}", id);
        return categoryMapper.toResponse(getOrThrow(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Category '%s' already exists".formatted(request.name()));
        }
        Category saved = categoryRepository.save(categoryMapper.toEntity(request));
        log.info("category saved {}", saved.getClass());
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getOrThrow(id);
        categoryMapper.updateEntity(request, category);
        log.info("category updated {}", category.getId());
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        categoryRepository.delete(getOrThrow(id));
        log.info("category deleted {}", id);
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}
