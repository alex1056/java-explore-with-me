package ru.practicum.ewm.main.service.location;


import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.model.Location;

public interface LocationService {

    Location saveLocation(LocationDto locationDto);

    Location findLocationById(Long id);

    void removeLocation(Long id);
}
