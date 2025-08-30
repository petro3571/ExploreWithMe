package ru.practicum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StatsController {
    private final HitServiceImpl hitService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/hit")
    public HitDto postHit(@Valid @RequestBody HitDto hitDto) {
        return hitService.postHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) throws Exception {

        try {
            String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8.toString());
            String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8.toString());

            LocalDateTime startDate = LocalDateTime.parse(decodedStart, FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(decodedEnd, FORMATTER);

            return hitService.getStats(startDate, endDate, uris, unique);
        } catch (DateTimeParseException e) {
            throw new Exception("Invalid date format. Use yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            throw new Exception("Invalid encoding");
        }
    }
}