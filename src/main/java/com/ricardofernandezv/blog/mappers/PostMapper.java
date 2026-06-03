package com.ricardofernandezv.blog.mappers;

import com.ricardofernandezv.blog.domain.CreatePostRequest;
import com.ricardofernandezv.blog.domain.UpdatePostRequest;
import com.ricardofernandezv.blog.domain.dtos.CreatePostRequestDto;
import com.ricardofernandezv.blog.domain.dtos.PostDto;
import com.ricardofernandezv.blog.domain.dtos.UpdatePostRequestDto;
import com.ricardofernandezv.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    @Mapping(target = "status", source = "status")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto dto);

    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto dto);

}
