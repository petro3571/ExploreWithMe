package ru.practicum.services.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.*;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateResul;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventRequest request, Long userId);

    List<EventShortDto> getEvents(Long userId, int from, int size);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestStatusUpdateResul changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEvents_2(List<Long> userIds,
                                   List<String> states,
                                   List<Long> categoryIds,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   int from,
                                   int size, HttpServletRequest request);

    EventFullDto updateEvent_1(Long eventId, UpdateEventAdminRequest request);

    List<EventShortDto> getEvents_1(String text,
                                    List<Long> categoryIds,
                                    boolean paid,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    boolean onlyAvailable,
                                    String sort,
                                    int from,
                                    int size, HttpServletRequest request
    );

    EventFullDto getEvent_1(Long eventId);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}