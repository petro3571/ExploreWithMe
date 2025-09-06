package ru.practicum.services.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryRequest request);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryRequest request);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
