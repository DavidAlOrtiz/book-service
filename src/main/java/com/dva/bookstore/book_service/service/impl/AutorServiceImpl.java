package com.dva.bookstore.book_service.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dva.bookstore.book_service.dto.AuthorRequest;
import com.dva.bookstore.book_service.dto.AuthorResponse;
import com.dva.bookstore.book_service.exception.ResourceNotFoundException;
import com.dva.bookstore.book_service.mapper.AuthorMapper;
import com.dva.bookstore.book_service.model.Author;
import com.dva.bookstore.book_service.repository.AuthorRepository;
import com.dva.bookstore.book_service.service.AuthorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        log.info("Finding all the authors");
        return this.authorRepository.findAll()
                .stream().map(author -> this.authorMapper.toResponse(author)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        log.info("Finding author -> {}", id);
        return this.authorMapper.toResponse(this.getOrThrow(id));
    }

    @Override
    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        log.info("save new autor");
        var autorSave = this.authorRepository.save(this.authorMapper.toEntity(request));
        log.info("new autor saved");
        return this.authorMapper.toResponse(autorSave);
    }

    @Override
    @Transactional
    public AuthorResponse update(Long id, AuthorRequest request) {
        log.info("updating autor   {}", id);
        Author author = getOrThrow(id);
        authorMapper.updateEntity(request, author);
        var autorUpdated = this.authorRepository.save(author);
        log.info("updated autor {}", author.getId());
        return authorMapper.toResponse(autorUpdated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("deleting autor {}", id);
        authorRepository.delete(getOrThrow(id));
        log.info("autor {} deleted ", id);
    }

    private Author getOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author", "id", id));
    }

}
