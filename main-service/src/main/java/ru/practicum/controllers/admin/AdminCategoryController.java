package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;
import ru.practicum.services.category.CategoryService;

@RestController
@RequestMapping (path = "/admin/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryRequest request) {
        return service.addCategory(request);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") Long catId) {
        service.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@Valid @RequestBody NewCategoryRequest request,
                                      @PathVariable(name = "catId") Long catId) {
        return service.updateCategory(catId, request);
    }
}