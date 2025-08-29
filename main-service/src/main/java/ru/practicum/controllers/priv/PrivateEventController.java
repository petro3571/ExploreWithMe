package ru.practicum.controllers.priv;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateResul;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.services.event.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PrivateEventController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService service;

    private final StatsClient statsClient;

    private static final String APP_NAME = "ewm-main-service";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@Valid @RequestBody NewEventRequest request,
                                 @PathVariable(name = "userId") Long userId) {
        return service.addEvent(request, userId);
    }

    @GetMapping
    public Page<EventShortDto> getEvents(@PathVariable(name = "userId") Long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size,
                                         HttpServletRequest request) {
        Page<EventShortDto> result = service.getEvents(userId, from, size);
        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER));
        statsClient.postHit(hitDto);
        return result;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable(name = "userId") Long userId,
                                 @PathVariable(name = "eventId") Long eventId,
                                 HttpServletRequest request) {
        EventFullDto result = service.getEvent(userId, eventId);
        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER));
        statsClient.postHit(hitDto);
        return result;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "userId") Long userId,
                                    @PathVariable(name = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest request) {
        return service.updateEvent(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable(name = "userId") Long userId,
                                                              @PathVariable(name = "eventId") Long eventId) {
        return service.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResul changeRequestStatus(@PathVariable(name = "userId") Long userId,
                                                             @PathVariable(name = "eventId") Long eventId,
                                                             @RequestBody EventRequestStatusUpdateRequest request) {
        return service.changeRequestStatus(userId, eventId, request);
    }
}