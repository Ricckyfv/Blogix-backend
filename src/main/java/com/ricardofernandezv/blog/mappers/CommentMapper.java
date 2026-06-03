package com.ricardofernandezv.blog.mappers;

import com.ricardofernandezv.blog.domain.dtos.CommentDto;
import com.ricardofernandezv.blog.domain.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "author", source = "author")
    CommentDto toDto(Comment comment);

}
