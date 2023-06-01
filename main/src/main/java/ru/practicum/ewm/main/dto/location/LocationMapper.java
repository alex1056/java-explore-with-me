package ru.practicum.ewm.main.dto.location;

import ru.practicum.ewm.main.model.Location;


public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        if (location == null) return null;
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }
}
