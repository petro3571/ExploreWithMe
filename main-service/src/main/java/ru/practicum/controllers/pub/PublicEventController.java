package ru.practicum.controllers.pub;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.services.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@RequestParam(required = false) @Size(max = 999) String text,
                                           @RequestParam(required = false) List<Long> categories,
                                           @RequestParam(required = false) Boolean paid,
                                           @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                           @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                          @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size, HttpServletRequest request
                                          ) {
        return service.getEventsByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);

    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable(name = "eventId") Long eventId, HttpServletRequest request) {
        return service.getEventByUser(eventId, request);

    }
}