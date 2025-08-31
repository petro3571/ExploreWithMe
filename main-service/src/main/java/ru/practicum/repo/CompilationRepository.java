package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Compilation;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Optional<Compilation> findById(Long compId);

    @Query(value = "SELECT c.* FROM compilations c " +
            "WHERE (c.pinned = :pinned) " +
            "AND c.id > :from " +
            "LIMIT :size",
            nativeQuery = true)
    List<Compilation> findByPinned(@Param("pinned") boolean pinned, @Param("from") int from,
                                   @Param("size") int size);

    @Query(value = "SELECT c.* FROM compilations c " +
            "WHERE c.id > :from " +
            "LIMIT :size",
            nativeQuery = true)
    List<Compilation> findByParam(@Param("from") int from,
                                   @Param("size") int size);

    boolean existsByTitle(String title);
}