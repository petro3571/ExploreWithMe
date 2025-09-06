package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    Optional<Category> findById(Long id);

    @Query(value = "SELECT c.* FROM categories c " +
            "WHERE c.id > :from " +
            "LIMIT :size",
            nativeQuery = true)
    List<Category> findByParam(@Param("from") int from,
                                @Param("size") int size);

    boolean existsByNameAndIdNot(String name, Long id);
}