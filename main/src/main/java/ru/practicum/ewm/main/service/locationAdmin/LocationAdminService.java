package ru.practicum.ewm.main.service.locationAdmin;

import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.locationAdmin.LocationAdminDto;
import ru.practicum.ewm.main.dto.locationAdmin.NewLocationAdminDto;
import ru.practicum.ewm.main.dto.locationAdmin.UpdateLocationAdminRequest;


import java.util.List;

public interface LocationAdminService {

    LocationAdminDto saveLocationAdmin(NewLocationAdminDto locationAdminDto);

    LocationAdminDto getLocationAdminById(Long id);

    LocationAdminDto updateLocationAdmin(Long locationId, UpdateLocationAdminRequest updateLocation);

    List<LocationAdminDto> getLocationAdmin();

    List<EventFullDto> getAllEventsInLocation(Long locationId, Double radius, Integer from, Integer size);

    void deleteLocationAdmin(Long locationId);
}
