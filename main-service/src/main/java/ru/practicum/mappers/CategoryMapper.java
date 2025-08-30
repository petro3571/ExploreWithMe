package ru.practicum.mappers;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;
import ru.practicum.entity.Category;

public class CategoryMapper {
    public static Category mapToCategory(CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        return category;
    }

    public static Category mapToCategoryFromNewRequest(NewCategoryRequest newCategoryRequest) {
        Category category = new Category();
        category.setName(newCategoryRequest.getName());
        return category;
    }

    public static CategoryDto mapToCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}