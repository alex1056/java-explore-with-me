package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.event.EventFullDto;
import ru.practicum.ewm.main.dto.event.NewEventDto;
import ru.practicum.ewm.main.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.dto.request.RequestDto;
import ru.practicum.ewm.main.service.event.EventService;
import ru.practicum.ewm.main.service.request.RequestService;
import ru.practicum.ewm.main.service.stat.StatService;

import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;
    private final StatService statService;

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> saveNewEvent(@Validated @RequestBody NewEventDto newEventDto, @PathVariable Long userId) {
        statService.saveHit("/users/" + userId + "/events");
        EventFullDto savedEvent = eventService.saveEvent(newEventDto, userId);
        return new ResponseEntity<>(savedEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events")
    public List<EventFullDto> getEvent(@PathVariable Long userId,
                                       @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                       @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        statService.saveHit("/users/" + userId + "/events");
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserOneEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId
    ) {
        statService.saveHit("/users/" + userId + "/events/" + eventId);
        return eventService.getUserOneEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Validated @RequestBody UpdateEventUserRequest eventToUpdate
    ) {
        statService.saveHit("/users/" + userId + "/events/" + eventId);
        EventFullDto event = eventService.updateEvent(userId, eventId, eventToUpdate);
        return event;
    }


    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getUserEventRequests(@PathVariable Long userId,
                                                 @PathVariable Long eventId
    ) {
        statService.saveHit("/users/" + userId + "/events/" + eventId + "/requests");
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequestsStatus(
            @Validated @RequestBody EventRequestStatusUpdateRequest updateRequestStatus,
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        statService.saveHit("/users/" + userId + "/events/" + eventId + "/requests");
        return requestService.updateUserEventRequestsStatus(
                userId,
                eventId,
                updateRequestStatus
        );
    }
}
