package ru.practicum.dto.compilation;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationDto {
    private String title;
    private boolean pinned;
    private List<Long> events;
}