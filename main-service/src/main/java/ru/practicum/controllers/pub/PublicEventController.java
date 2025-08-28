package ru.practicum.controllers.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.services.event.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublicEventController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventService service;
    private final StatsClient statsClient;

    @GetMapping
    public Page<EventShortDto> getEvents_1(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) List<Long> categoryIds,
                                           @RequestParam(required = false) boolean paid,
                                           @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(required = false) boolean onlyAvailable,
                                           @RequestParam(required = false) String sort,
                                          @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size, HttpServletRequest request
                                          ) {
        Page<EventShortDto> result = service.getEvents_1(text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);

        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER)));
        statsClient.postHit(hitDto);
        return result;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent_1(@PathVariable(name = "eventId") Long eventId, HttpServletRequest request) {
        EventFullDto result = service.getEvent_1(eventId);
        HitDto hitDto = new HitDto();
        hitDto.setApp("main-service");
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setUri(request.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.parse(LocalDateTime.now().format(FORMATTER)));
        statsClient.postHit(hitDto);
        return result;
    }
}