package ru.practicum.mappers;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;

public class CompilationMapper {

    public static Compilation mapToCompilationFromNewRequest(NewCompilationDto request) {
        Compilation compilation = new Compilation();
        compilation.setTitle(request.getTitle());
        compilation.setPinned(request.isPinned());
        return compilation;
    }

    public static CompilationDto mapToCompDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.isPinned());
        return dto;
    }

    public static Compilation mapToCompFroUpd(Compilation compilation, UpdateCompilationDto request) {
        if (!(request.getTitle() == null || request.getTitle().isBlank())) {
            compilation.setTitle(request.getTitle());
        }

        if (compilation.isPinned() == false && request.isPinned() == true) {
            compilation.setPinned(request.isPinned());
        }

        return compilation;
    }
}
