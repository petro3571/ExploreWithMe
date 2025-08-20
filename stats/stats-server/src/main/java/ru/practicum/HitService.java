package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HitDto postHit(HitDto hitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}