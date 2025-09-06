package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public HitDto postHit(HitDto hitDto) {
        Hit hit = HitMapper.mapToHit(hitDto);
        hitRepository.save(hit);
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris != null) {
            if (unique) {
            return hitRepository.findUniqueStatsByUrisAndTimestampBetween(uris, start, end);

            } else {
            return hitRepository.findByUriInAndTimestampBetween(uris,start, end);
            }
        } else {
            if (unique) {
            return hitRepository.findUniqueStatsByTimestampBetween(start, end);

            } else {
            return hitRepository.findByTimestampBetween(start, end);
            }
        }
    }
}