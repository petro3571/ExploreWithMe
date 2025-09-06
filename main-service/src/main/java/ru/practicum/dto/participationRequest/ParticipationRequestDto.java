package ru.practicum.dto.participationRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.dto.enums.RequestStatus;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private RequestStatus status;
}