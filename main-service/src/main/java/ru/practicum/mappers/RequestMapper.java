package ru.practicum.mappers;

import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.entity.ParticipationRequest;

public class RequestMapper {
    public static ParticipationRequestDto mapToRequestDtoFromRequest(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());
        return dto;
    }
}