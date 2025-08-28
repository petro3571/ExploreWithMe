package ru.practicum.dto.participationRequest;

import lombok.Data;
import ru.practicum.dto.enums.RequestStatus;
import ru.practicum.dto.enums.State;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}