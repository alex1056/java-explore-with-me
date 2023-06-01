package ru.practicum.ewm.main.service.event;


import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.model.event.Event;

import java.util.List;

public interface EventService {

    EventFullDto saveEvent(NewEventDto newEventDto, Long userId);

    EventFullDto getEventById(Long id);

    Event getEventOriginById(Long id);

    List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getUserOneEvent(Long userId, Long eventId);

    Event getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventToUpdate);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest eventToUpdate);

    List<EventShortDto> findEventsByParams(EventSearchParams searchParams);

    List<EventFullDto> findEventsByParamsAdmin(EventAdminSearchParams searchParams);

    Long getConfirmedReqCounter(Long eventId);

    List<Event> getEventsWithConfirmedReqCounter(List<Event> events);

    EventFullDto getPublishedEventById(Long id);
}
