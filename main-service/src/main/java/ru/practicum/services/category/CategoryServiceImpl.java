package ru.practicum.services.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.DuplicateCategoryException;
import ru.practicum.exceptions.NotFoundUserException;
import ru.practicum.mappers.CategoryMapper;
import ru.practicum.mappers.EventMapper;
import ru.practicum.repo.CategoryRepository;
import ru.practicum.repo.EventRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryRequest request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new DuplicateCategoryException("Нарушение целостности данных, категория с именем " + request.getName() + "существует");
        }

        Category category = categoryRepository.save(CategoryMapper.mapToCategoryFromNewRequest(request));
        return CategoryMapper.mapToCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            throw new NotFoundUserException("Category with id = " + catId + " was not found");
        } else {
            Optional<Event> findEvent = eventRepository.findByCategory_Id(catId);
            if (findEvent.isPresent()) {
                throw new ConflictException("Категория не должна быть связана ни c одним событием.");
            } else {
                categoryRepository.delete(category.get());
            }
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryRequest request) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            throw new NotFoundUserException("Category with id = " + catId + " was not found");
        } else {
            if (categoryRepository.findByName(request.getName()) != null) {
                throw new DuplicateCategoryException("Нарушение целостности данных, категория с именем " + request.getName() + "существует");
            } else {
                Category findCategory = category.get();
                findCategory.setName(request.getName());
                return CategoryMapper.mapToCategoryDto(categoryRepository.save(findCategory));
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));

        Page<Category> requestsPage = categoryRepository.findAll(pageable);

        if (!requestsPage.getContent().isEmpty()) {
            List<CategoryDto> content = requestsPage.getContent().stream()
                    .map(category -> CategoryMapper.mapToCategoryDto(category))
                    .sorted(Comparator.comparing(CategoryDto::getId))
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    content,
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        } else {

                return new PageImpl<>(
                        Collections.emptyList(),
                        requestsPage.getPageable(),
                        requestsPage.getTotalElements()
                );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(long catId) {
        Optional<Category> category = categoryRepository.findById(catId);
        if (category.isEmpty()) {
            throw new NotFoundUserException("Category with id = " + catId + " was not found");
        } else {
            return CategoryMapper.mapToCategoryDto(category.get());
        }
    }
}