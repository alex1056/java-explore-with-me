package ru.practicum.ewm.main.service.location;


import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.location.LocationAdminDto;
import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.dto.location.NewLocationAdminDto;
import ru.practicum.ewm.main.dto.location.UpdateLocationAdminRequest;
import ru.practicum.ewm.main.model.Location;

import java.util.List;

public interface LocationService {

    Location saveLocation(LocationDto locationDto);

    LocationAdminDto saveLocationAdmin(NewLocationAdminDto locationAdminDto);

    LocationAdminDto updateLocationAdmin(Long locationId, UpdateLocationAdminRequest updateLocation);

    void deleteLocationAdmin(Long locationId);

    LocationAdminDto getLocationAdminById(Long id);

    List<EventFullDto> getAllEventsInLocationAdmin(Long locationId, Integer from, Integer size);

    List<EventFullDto> getAllEventsInLocationUser(Long locationId, Integer from, Integer size);

    List<LocationAdminDto> getLocationsAdmin();

    Location getLocationById(Long id);

    Location findLocationById(Long id);

    void removeLocation(Long id);
}
