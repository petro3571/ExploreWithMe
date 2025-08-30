package ru.practicum.controllers.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.services.event.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PrivateCompilationController {
    private final EventService service;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable(name = "userId") Long userId) {
        return service.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable(name = "userId") Long userId,
                                                           @RequestParam(name = "eventId") Long eventId) {
        return service.addParticipationRequest(userId, eventId);
    }

    @DeleteMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(name = "userId") Long userId,
                                                           @PathVariable(name = "requestId") Long requestId) {
        return service.cancelRequest(userId, requestId);
    }
}