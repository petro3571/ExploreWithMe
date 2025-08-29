package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.dto.enums.State;
import ru.practicum.entity.Location;

import java.time.LocalDateTime;

@Data
public class NewEventRequest {
    private String annotation;

    private Long category;

    @NotBlank
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private boolean paid;

    private Integer participantLimit;

    private boolean requestModeration;

    private String title;

    private State state = State.PENDING;
}