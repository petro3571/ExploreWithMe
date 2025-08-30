package ru.practicum.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.compilation.CompilationService;

@RestController
@RequestMapping(path = "/compilations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService service;
    private final StatsClient statsClient;

    @GetMapping
    public Page<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") Long compId) {
        return service.getCompilation(compId);
    }
}
