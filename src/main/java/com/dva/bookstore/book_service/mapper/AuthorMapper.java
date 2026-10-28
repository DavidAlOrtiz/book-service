package com.dva.bookstore.book_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.dva.bookstore.book_service.dto.AuthorRequest;
import com.dva.bookstore.book_service.dto.AuthorResponse;
import com.dva.bookstore.book_service.model.Author;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponse toResponse(Author author);

    @Mapping(target = "id", ignore = true)
    Author toEntity(AuthorRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(AuthorRequest request, @MappingTarget Author author);

}
