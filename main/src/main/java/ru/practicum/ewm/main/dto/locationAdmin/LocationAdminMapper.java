package ru.practicum.ewm.main.dto.locationAdmin;

import ru.practicum.ewm.main.dto.location.LocationMapper;
import ru.practicum.ewm.main.model.LocationAdmin;

import java.util.ArrayList;
import java.util.List;


public class LocationAdminMapper {
    public static LocationAdminDto toLocationAdminDto(LocationAdmin la) {
        if (la == null) return null;
        return new LocationAdminDto(
                la.getId(),
                la.getName(),
                LocationMapper.toLocationDto(la.getLocation())
        );
    }

    public static List<LocationAdminDto> toLocationAdminDto(Iterable<LocationAdmin> locations) {
        List<LocationAdminDto> result = new ArrayList<>();
        for (LocationAdmin location : locations) {
            result.add(toLocationAdminDto(location));
        }
        return result;
    }
}
