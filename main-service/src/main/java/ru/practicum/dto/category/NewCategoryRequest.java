package ru.practicum.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewCategoryRequest {
    @NotBlank
    private String name;
}
