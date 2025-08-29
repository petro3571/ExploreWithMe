package ru.practicum.controllers.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.services.event.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminEventsController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService service;
    private final StatsClient statsClient;

    @GetMapping
    public Page<EventFullDto> getEvents_2(@RequestParam(required = false) List<Long> userIds,
                                          @RequestParam(required = false) List<String> states,
                                          @RequestParam(required = false) List<Long> categoryIds,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                          @RequestParam(required = false)
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
        Page<EventFullDto> result = service.getEvents_2(userIds, states, categoryIds, rangeStart, rangeEnd, from, size);
        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER));
        statsClient.postHit(hitDto);
        return result;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent_1(@PathVariable(name = "eventId") Long eventId,@RequestBody UpdateEventAdminRequest request) {
        return service.updateEvent_1(eventId, request);
    }
}