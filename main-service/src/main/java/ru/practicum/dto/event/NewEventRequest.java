package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;

@Data
public class NewEventRequest {
    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    private String annotation;

    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    @JsonProperty(defaultValue = "false")
    private boolean paid;

    @PositiveOrZero
    @JsonProperty(defaultValue = "0")
    private Integer participantLimit;

    @JsonProperty(defaultValue = "true")
    private boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    private State state = State.PENDING;
}