package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {
    private Long id;
    @Size(min = 2, max = 50, message = "Title must be between 2 and 50 characters")
    private String title;
    private boolean pinned;
    private List<EventShortDto> events;
}