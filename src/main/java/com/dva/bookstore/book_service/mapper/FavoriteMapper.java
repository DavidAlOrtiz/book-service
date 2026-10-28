package com.dva.bookstore.book_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.dva.bookstore.book_service.dto.FavoriteResponse;
import com.dva.bookstore.book_service.model.Favorite;

@Mapper(componentModel = "spring", uses = BookMapper.class)
public interface FavoriteMapper {

    @Mapping(target = "addedAt", source = "createdAt")
    FavoriteResponse toResponse(Favorite favorite);
}