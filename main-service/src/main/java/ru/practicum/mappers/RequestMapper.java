package ru.practicum.mappers;

import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.ParticipationRequest;
import ru.practicum.entity.User;

public class RequestMapper {
//    public static ParticipationRequest mapToUser(ParticipationRequestDto dto) {
//        ParticipationRequest request = new ParticipationRequest();
//        request.setId(dto.getId());
//        request.set(dto.getName());
//        urequestser.setEmail(dto.getEmail());
//        return user;
//    }

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