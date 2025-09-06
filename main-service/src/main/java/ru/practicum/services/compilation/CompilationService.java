package ru.practicum.services.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto saveCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto request);

    List<CompilationDto> getCompilations(Boolean pinned,
                                         int from,
                                         int size);

    CompilationDto getCompilation(Long compId);
}