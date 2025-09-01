package ru.practicum.controllers.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.services.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminEventsController {
    private final EventService service;

    @GetMapping
    public List<EventFullDto> getEvents_2(@RequestParam(required = false) List<Long> users,
                                          @RequestParam(required = false) List<String> states,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        List<EventFullDto> result = service.getEvents_2(users, states, categories, rangeStart, rangeEnd, from, size, request);
        return result;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent_1(@PathVariable(name = "eventId") Long eventId,@Valid @RequestBody UpdateEventAdminRequest request) {
        return service.updateEvent_1(eventId, request);
    }
}