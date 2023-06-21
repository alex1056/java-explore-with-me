package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.location.LocationAdminDto;
import ru.practicum.ewm.main.service.location.LocationService;
import ru.practicum.ewm.main.service.stat.StatService;

import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/locations")
@RequiredArgsConstructor
@Validated
public class LocationPubController {

    private final LocationService locationService;
    private final StatService statService;

    @GetMapping("/{locationId}/events")
    public List<EventFullDto> getAllPublishedEventsInLocation(
            @PathVariable Long locationId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        statService.saveHit("/events/locations/" + locationId);
        return locationService.getAllEventsInLocationUser(locationId, from, size);
    }

    @GetMapping
    public List<LocationAdminDto> getLocationAdmin() {
        statService.saveHit("/events/locations");
        return locationService.getLocationsAdmin();
    }
}
