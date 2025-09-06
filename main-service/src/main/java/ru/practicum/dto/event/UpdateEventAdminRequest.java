package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.entity.Location;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "Annotation must be between 20 and 2000 characters")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Description must be between 20 and 7000 characters")
    private String description;

    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private boolean requestModeration;

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    private StateAction stateAction;
}