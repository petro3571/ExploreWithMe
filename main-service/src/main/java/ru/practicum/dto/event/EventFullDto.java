package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.entity.Location;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private Integer participantLimit;

    private LocalDateTime /*String*/publishedOn;

    private boolean requestModeration;

    private State state;

    private String title;

    private Long views;
}
