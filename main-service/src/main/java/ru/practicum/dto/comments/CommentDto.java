package ru.practicum.dto.comments;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    @Size(min = 1, max = 254, message = "Text must be between 1 and 254 characters")
    private String text;

    private String authorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}