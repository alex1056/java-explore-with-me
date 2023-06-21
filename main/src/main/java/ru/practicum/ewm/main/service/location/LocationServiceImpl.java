package ru.practicum.ewm.main.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.EventMapper;
import ru.practicum.ewm.main.dto.location.*;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.helper.Helpers;
import ru.practicum.ewm.main.model.Location;
//import ru.practicum.ewm.main.model.LocationAdmin;
import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.LocationRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;
    private final EventRepository repositoryEvent;


    @Transactional
    @Override
    public Location saveLocation(LocationDto locationDto) {
        Location location = new Location(
                null,
                locationDto.getLat(),
                locationDto.getLon(),
                null,
                null,
                null
        );
        return repository.save(location);
    }

    @Transactional
    @Override
    public LocationAdminDto saveLocationAdmin(NewLocationAdminDto locationAdminDto) {
        Location location = new Location(
                null,
                locationAdminDto.getLat(),
                locationAdminDto.getLon(),
                locationAdminDto.getName(),
                locationAdminDto.getDescription(),
                locationAdminDto.getRadius()
        );

        Location locationSaved = repository.save(location);
        return LocationMapper.toLocationAdminDto(locationSaved);
    }

    @Transactional
    @Override
    public void deleteLocationAdmin(Long locationId) {
        Location location = getLocationById(locationId);
        repository.delete(location);
    }

    @Transactional
    @Override
    public List<LocationAdminDto> getLocationsAdmin() {
        List<Location> locations = repository.findAdminLocationAll();
        return LocationMapper.toLocationAdminDto(locations);
    }

    public Location getLocationAdminByIdUser(Long id) {
        Optional<Location> location = repository.findAdminLocationById(id);
        location.orElseThrow(() -> new NotFoundException("Локация с id=" + id));
        return location.get();
    }

    @Transactional
    @Override
    public LocationAdminDto updateLocationAdmin(Long locationId, UpdateLocationAdminRequest updateLocation) {
        Location location = getLocationById(locationId);
        location.setLat(updateLocation.getLat() != null ? updateLocation.getLat() : location.getLat());
        location.setLon(updateLocation.getLon() != null ? updateLocation.getLon() : location.getLon());
        location.setName(updateLocation.getName() != null ? updateLocation.getName() : location.getName());
        location.setDescription(updateLocation.getDescription() != null ? updateLocation.getDescription() : location.getDescription());
        location.setRadius(updateLocation.getRadius() != null ? updateLocation.getRadius() : location.getRadius());
        Location locationSaved = repository.save(location);
        return LocationMapper.toLocationAdminDto(locationSaved);
    }

    @Transactional
    @Override
    public Location getLocationById(Long id) {
        Optional<Location> locationOpt = repository.findById(id);
        locationOpt.orElseThrow(() -> new NotFoundException("Локация с id=" + id));
        return locationOpt.get();
    }

    @Transactional
    @Override
    public LocationAdminDto getLocationAdminById(Long id) {
        return LocationMapper.toLocationAdminDto(getLocationById(id));
    }

    @Transactional
    @Override
    public List<EventFullDto> getAllEventsInLocationAdmin(Long locationId, Integer from, Integer size) {
        Location location = getLocationById(locationId);
        Double radius = location.getRadius();
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<Event> events = repositoryEvent.findEventsInLocationAdmin(
                location.getLat(), location.getLon(), radius, page
        ).getContent();
        return EventMapper.toEventFullDto(events);
    }

    @Transactional
    @Override
    /* событие должно быть опубликовано  */
    public List<EventFullDto> getAllEventsInLocationUser(Long locationId, Integer from, Integer size) {
        // метод поиска локации c name && radius != null
        Location location = getLocationAdminByIdUser(locationId);
        Double radius = location.getRadius();
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<Event> events = repositoryEvent.findEventsInLocationUser(
                location.getLat(), location.getLon(), radius, page
        ).getContent();
        return EventMapper.toEventFullDto(events);
    }


    @Transactional
    @Override
    public Location findLocationById(Long id) {
        Optional<Location> location = repository.findById(id);
        if (location.isPresent()) return location.get();
        return null;
    }

    @Transactional
    @Override
    public void removeLocation(Long id) {
        repository.deleteById(id);
    }
}
