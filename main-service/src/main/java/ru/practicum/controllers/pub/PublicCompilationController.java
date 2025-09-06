package ru.practicum.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.services.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable(name = "compId") Long compId) {
        return service.getCompilation(compId);
    }
}
