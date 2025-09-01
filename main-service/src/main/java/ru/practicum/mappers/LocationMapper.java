package ru.practicum.mappers;

import ru.practicum.dto.location.LocationDto;
import ru.practicum.entity.Location;

public class LocationMapper {
    public static Location mapToLoc(LocationDto dto) {
        Location location = new Location();
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    public static LocationDto mapToLocaDto(Location location) {
        LocationDto dto = new LocationDto();
        dto.setLat(location.getLat());
        dto.setLon(location.getLon());
        return dto;
    }
}