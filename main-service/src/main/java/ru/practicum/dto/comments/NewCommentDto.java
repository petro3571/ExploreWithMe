package ru.practicum.dto.comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentDto {
    @NotBlank
    @Size(min = 1, max = 254, message = "Text must be between 1 and 254 characters")
    private String text;
}