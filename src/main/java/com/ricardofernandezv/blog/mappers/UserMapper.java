package com.ricardofernandezv.blog.mappers;

import com.ricardofernandezv.blog.domain.dtos.AuthorDto;
import com.ricardofernandezv.blog.domain.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    AuthorDto toAuthorDto(User user);
}
