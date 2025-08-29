package ru.practicum.services.compilation;

import org.springframework.data.domain.Page;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

public interface CompilationService {
    CompilationDto saveCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto request);

    Page<CompilationDto> getCompilations(boolean pinned,
                                         int from,
                                         int size);

    CompilationDto getCompilation(Long compId);
}