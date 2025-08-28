package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class NewCompilationDto {
    @NotBlank
    private String title;

    private boolean pinned;

    private List<Long> events;
}