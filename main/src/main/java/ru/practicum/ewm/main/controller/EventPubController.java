package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.exception.BadRequestException;
import ru.practicum.ewm.main.service.event.EventService;
import ru.practicum.ewm.main.service.location.LocationService;
import ru.practicum.ewm.main.service.stat.StatService;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class EventPubController {
    private final EventService eventService;
    private final StatService statService;
    private final LocationService locationService;

    @GetMapping
    public List<EventShortDto> getAllEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") String sort,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        EventSortSearch sortParam = EventSortSearch.valueOf(sort);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата конечная раньше начальной");
        }

        EventSearchParams searchParams = new EventSearchParams(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sortParam,
                from,
                size
        );
        statService.saveHit("/events");
        return eventService.findEventsByParams(searchParams);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId) {
        EventFullDto event = eventService.getPublishedEventById(eventId);
        statService.saveHit("/events/" + eventId);
        return event;
    }
}
