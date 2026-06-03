package com.ricardofernandezv.blog.mappers;

import com.ricardofernandezv.blog.domain.PostStatus;
import com.ricardofernandezv.blog.domain.dtos.CategoryDto;
import com.ricardofernandezv.blog.domain.dtos.CreateCategoryRequest;
import com.ricardofernandezv.blog.domain.entities.Category;
import com.ricardofernandezv.blog.domain.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    CategoryDto toDto(Category category);

    @Named("calculatePostCount")
    default long calculatePostCount(List<Post> posts) {
        if(posts == null) {
            return 0;
        }
        return posts.stream()
                .filter(post -> PostStatus.PUBLISHED.equals(post.getStatus()))
                .count();
    }

    Category toEntity(CreateCategoryRequest createCategoryRequest);

}
