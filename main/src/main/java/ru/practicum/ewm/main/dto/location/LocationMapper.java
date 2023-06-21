package ru.practicum.ewm.main.dto.location;

import ru.practicum.ewm.main.model.Location;

import java.util.ArrayList;
import java.util.List;


public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        if (location == null) return null;
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }

    public static LocationAdminDto toLocationAdminDto(Location location) {
        if (location == null) return null;
        return new LocationAdminDto(
                location.getId(),
                location.getLat(),
                location.getLon(),
                location.getName(),
                location.getDescription(),
                location.getRadius()
        );
    }

    public static List<LocationAdminDto> toLocationAdminDto(Iterable<Location> locations) {
        List<LocationAdminDto> result = new ArrayList<>();
        for (Location location : locations) {
            result.add(toLocationAdminDto(location));
        }
        return result;
    }
}
