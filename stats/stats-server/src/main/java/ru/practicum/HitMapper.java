package ru.practicum;

public class HitMapper {
    public static Hit mapToHit(HitDto dto) {
        Hit hit = new Hit();
        hit.setId(dto.getId());
        hit.setIp(dto.getIp());
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setTimestamp(dto.getTimestamp());
        return hit;
    }

    public static HitDto mapToHitDto(Hit hit) {
        HitDto dto = new HitDto();
        dto.setId(hit.getId());
        dto.setIp(hit.getIp());
        dto.setApp(hit.getApp());
        dto.setUri(hit.getUri());
        dto.setTimestamp(hit.getTimestamp());
        return dto;
    }
}