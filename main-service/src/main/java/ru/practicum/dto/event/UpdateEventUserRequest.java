package ru.practicum.dto.event;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.entity.Location;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {
    private String annotation;

    private Long category;

    private String description;

    private LocalDateTime eventDate;

    private Location location;

    private boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private boolean requestModeration;

    private String title;

    private StateAction stateAction;
}