package ru.practicum.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.Category;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    Optional<Category> findById(Long id);

    Page<Category> findAll(Pageable pageable);
}