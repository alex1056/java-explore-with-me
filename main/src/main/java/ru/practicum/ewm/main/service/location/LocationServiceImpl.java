package ru.practicum.ewm.main.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.model.Location;
import ru.practicum.ewm.main.repository.LocationRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository repository;


    @Transactional
    @Override
    public Location saveLocation(LocationDto locationDto) {
        Location location = new Location(null, locationDto.getLat(), locationDto.getLon());
        return repository.save(location);
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
