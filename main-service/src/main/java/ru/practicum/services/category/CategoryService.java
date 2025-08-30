package ru.practicum.services.category;

import org.springframework.data.domain.Page;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryRequest request);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryRequest request);

    Page<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
