package com.dva.bookstore.book_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.dva.bookstore.book_service.dto.CategoryRequest;
import com.dva.bookstore.book_service.dto.CategoryResponse;
import com.dva.bookstore.book_service.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);

}
