package ru.practicum.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.Compilation;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Optional<Compilation> findById(Long compId);

    Page<Compilation> findByPinned(boolean pinned, Pageable pageable);
}
