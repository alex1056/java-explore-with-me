package ru.practicum.ewm.main.service.locationAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.dto.locationAdmin.LocationAdminDto;
import ru.practicum.ewm.main.dto.locationAdmin.LocationAdminMapper;
import ru.practicum.ewm.main.dto.locationAdmin.NewLocationAdminDto;
import ru.practicum.ewm.main.dto.locationAdmin.UpdateLocationAdminRequest;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.helper.Helpers;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Location;
import ru.practicum.ewm.main.model.LocationAdmin;
import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.model.event.EventState;
import ru.practicum.ewm.main.model.event.StateActionAdmin;
import ru.practicum.ewm.main.model.event.StateActionUser;
import ru.practicum.ewm.main.model.request.RequestCounter;
import ru.practicum.ewm.main.model.request.RequestStatus;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.LocationAdminRepository;
import ru.practicum.ewm.main.repository.RequestSpecRepository;
import ru.practicum.ewm.main.service.category.CategoryService;
import ru.practicum.ewm.main.service.location.LocationService;
import ru.practicum.ewm.main.service.stat.StatService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationAdminServiceImpl implements LocationAdminService {
    private final LocationAdminRepository repository;
    private final LocationService locationService;
    private final EventRepository repositoryEvent;

    @Transactional
    @Override
    public LocationAdminDto saveLocationAdmin(NewLocationAdminDto locationAdminDto) {
        Location location = locationService.saveLocation(locationAdminDto.getLocation());
        LocationAdmin locationAdmin = new LocationAdmin(
                null,
                locationAdminDto.getName(),
                location.getId(),
                location
        );


        LocationAdmin locationAdminSaved = repository.save(locationAdmin);
        return LocationAdminMapper.toLocationAdminDto(locationAdminSaved);
    }

    @Transactional
    @Override
    public LocationAdminDto getLocationAdminById(Long id) {
        Optional<LocationAdmin> locationOpt = repository.findById(id);
        if (locationOpt.isPresent()) {
            LocationAdmin location = locationOpt.get();
            return LocationAdminMapper.toLocationAdminDto(location);
        }
        return null;
    }

    @Transactional
    @Override
    public LocationAdminDto updateLocationAdmin(Long locationId, UpdateLocationAdminRequest updateLocation) {
        LocationAdmin locationAdmin = getLocationAdminByIdPrivate(locationId);
        if (updateLocation.getLocation() != null) {
            locationService.removeLocation(locationAdmin.getLocationId());
            Location location = locationService.saveLocation(updateLocation.getLocation());
            locationAdmin.setLocationId(location.getId());
            locationAdmin.setLocation(location);
        }
        locationAdmin.setName(updateLocation.getName() != null ? updateLocation.getName() : locationAdmin.getName());
        LocationAdmin laSaved = repository.save(locationAdmin);
        return LocationAdminMapper.toLocationAdminDto(laSaved);
    }

    private LocationAdmin getLocationAdminByIdPrivate(Long id) {
        Optional<LocationAdmin> locationOpt = repository.findById(id);
        locationOpt.orElseThrow(() -> new NotFoundException("Локация с id=" + id));
        return locationOpt.get();
    }

    public List<LocationAdminDto> getLocationAdmin() {
        List<LocationAdmin> locations = repository.findAll();
        return LocationAdminMapper.toLocationAdminDto(locations);
    }

    public List<EventFullDto> getAllEventsInLocation(Long locationId, Double radius, Integer from, Integer size) {
        LocationAdmin la = getLocationAdminByIdPrivate(locationId);
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<Event> events = repositoryEvent.findEventsInLocation(la.getLocation().getLat(), la.getLocation().getLon(), radius, page).getContent();
        return EventMapper.toEventFullDto(events);
    }
}