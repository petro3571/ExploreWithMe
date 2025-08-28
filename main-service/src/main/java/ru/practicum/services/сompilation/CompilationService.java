package ru.practicum.services.сompilation;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;

public interface CompilationService {
    CompilationDto saveCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationDto request);

    Page<CompilationDto> getCompilations(boolean pinned,
                                         int from,
                                         int size);

    CompilationDto getCompilation(Long compId);
}