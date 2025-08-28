package ru.practicum.dto.event;

import lombok.Data;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.entity.Location;

import java.time.LocalDateTime;

@Data
public class NewEventRequest {
    private String annotation;

    private Long categoryId;

    private String description;

    private LocalDateTime eventDate;

    private LocationDto location;

    private boolean paid;

    private Integer participantLimit;

    private boolean requestModeration;

    private String title;

    private State state = State.PENDING;
}