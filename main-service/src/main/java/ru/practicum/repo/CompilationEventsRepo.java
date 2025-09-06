package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.CompilationEvents;

import java.util.List;

public interface CompilationEventsRepo extends JpaRepository<CompilationEvents, Long> {
    List<CompilationEvents> findByCompilation_Id(Long userId);

    void deleteByCompilation_Id(Long compilationId);
}