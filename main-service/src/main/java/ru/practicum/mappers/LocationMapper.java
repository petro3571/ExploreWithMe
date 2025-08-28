package ru.practicum.mappers;

import ru.practicum.dto.location.LocationDto;
import ru.practicum.entity.Location;

public class LocationMapper {
    public static Location mapToUser(LocationDto dto) {
        Location location = new Location();
        location.setId(dto.getId());
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    public static LocationDto mapToUserDto(Location location) {
        LocationDto dto = new LocationDto();
        dto.setId(location.getId());
        dto.setLat(location.getLat());
        dto.setLon(location.getLon());
        return dto;
    }
}