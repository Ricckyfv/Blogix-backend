//package com.ricardofernandezv.blog.mappers.impls;
//
//import com.ricardofernandezv.blog.domain.dtos.CategoryDto;
//import com.ricardofernandezv.blog.domain.dtos.CreateCategoryRequest;
//import com.ricardofernandezv.blog.domain.entities.Category;
//import com.ricardofernandezv.blog.mappers.CategoryMapper;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CategoryMapperImpl implements CategoryMapper {
//
//    @Override
//    public CategoryDto toDto(Category category) {
//        return CategoryDto.builder()
//                .name(category.getName())
//                // all values
//                .build();
//    }
//
//    @Override
//    public Category toEntity(CreateCategoryRequest createCategoryRequest) {
//        return null;
//    }
//}
