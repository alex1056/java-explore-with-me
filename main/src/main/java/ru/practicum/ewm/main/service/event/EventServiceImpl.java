package ru.practicum.ewm.main.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.event.*;
import ru.practicum.ewm.main.exception.Conflict409Exception;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.helper.Helpers;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Location;
import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.model.event.EventState;
import ru.practicum.ewm.main.model.event.StateActionAdmin;
import ru.practicum.ewm.main.model.event.StateActionUser;
import ru.practicum.ewm.main.model.request.RequestCounter;
import ru.practicum.ewm.main.model.request.RequestStatus;
import ru.practicum.ewm.main.repository.EventRepository;
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
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final LocationService locationService;
    private final RequestSpecRepository repositorySpecRequest;
    private final CategoryService categoryService;
    private final StatService statService;

    @Transactional
    @Override
    public EventFullDto saveEvent(NewEventDto newEventDto, Long userId) {
        Location location = locationService.saveLocation(newEventDto.getLocation());
        Category category = categoryService.findCategoryById(newEventDto.getCategory());

        Event newEvent = new Event(
                null,
                newEventDto.getCategory(),
                category,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                newEventDto.getAnnotation(),
                newEventDto.getTitle(),
                userId, //initiator
                null,
                location.getId(), // locationId
                null,
                newEventDto.getPaid() != null && newEventDto.getPaid(),
                newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit(),
                newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration(),
                null,
                EventState.PENDING,
                null,
                null,
                null
        );

        Event eventSaved = repository.save(newEvent);
        eventSaved.setLocation(location);
        return EventMapper.toEventFullDto(eventSaved);
    }

    @Transactional
    @Override
    public EventFullDto getEventById(Long id) {
        Optional<Event> event = repository.findEventById(id);

        if (event.isPresent()) {
            Event ev = event.get();
            ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
            return EventMapper.toEventFullDto(ev);
        }
        return null;
    }

    @Transactional
    @Override
    public Event getEventOriginById(Long id) {
        Optional<Event> event = repository.findEventById(id);
        event.orElseThrow(() -> new NotFoundException("событие с id= " + id));
        Event ev = event.get();
        ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
        return ev;
    }

    @Transactional
    @Override
    public EventFullDto getPublishedEventById(Long id) {
        Optional<Event> event = repository.findEventByIdPublished(id);
        event.orElseThrow(() -> new NotFoundException("событие с id= " + id));
        Event ev = event.get();
        Long views = statService.getPublicEventViewsById(ev.getId());
        ev.setViews(views);
        ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
        return EventMapper.toEventFullDto(ev);
    }

    @Transactional
    @Override
    public EventFullDto getUserOneEvent(Long userId, Long eventId) {
        Optional<Event> event = repository.findUserEvent(userId, eventId);
        event.orElseThrow(() -> new NotFoundException("событие с id=" + eventId + " пользователя с id=" + userId));
        Event ev = event.get();
        ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
        return EventMapper.toEventFullDto(ev);
    }

    @Transactional
    @Override
    public Event getEvent(Long userId, Long eventId) {
        Optional<Event> event = repository.findUserEvent(userId, eventId);
        event.orElseThrow(() -> new NotFoundException("событие с id=" + eventId + " пользователя с id=" + userId));
        Event ev = event.get();
        ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
        return ev;
    }

    @Transactional
    @Override
    public List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(from, size), size);
        List<Event> events = repository.findUserEvents(userId, page).getContent();
        List<Event> eventsAdded = getEventsWithConfirmedReqCounter(events);
        return EventMapper.toEventFullDto(eventsAdded);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventToUpdate) {
        Event event = getEvent(userId, eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new Conflict409Exception("Изменение уже опубликованных событий запрещено");
        }

        if (eventToUpdate.getLocation() != null) {
            locationService.removeLocation(event.getLocationId());
            Location location = locationService.saveLocation(eventToUpdate.getLocation());
            event.setLocationId(location.getId());
        }

        if (eventToUpdate.getStateAction() == StateActionUser.SEND_TO_REVIEW) {
            event.setPublished(null);
            event.setState(EventState.PENDING);
        }
        if (eventToUpdate.getStateAction() == StateActionUser.CANCEL_REVIEW) {
            event.setPublished(null);
            event.setState(EventState.CANCELED);
        }

        Helpers.composeFieldsForUpdateEvent(eventToUpdate, event);
        Event eventSaved = repository.save(event);
        eventSaved.setConfirmedRequests(getConfirmedReqCounter(eventSaved.getId()));
        return EventMapper.toEventFullDto(eventSaved);
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest eventToUpdate) {
        Event event = getEventOriginById(eventId);
        if (event.getEventDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new Conflict409Exception("Изменение запрещено, т.к. событие состоится менее чем через 1 час");
        }

        if (eventToUpdate.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {

            if (event.getPublished() != null) {
                throw new Conflict409Exception("событие опубликовано ранее");
            }

            if (event.getState() == EventState.CANCELED) {
                throw new Conflict409Exception("событие отменено пользователем ранее");
            }

            event.setPublished(LocalDateTime.now());
            event.setState(EventState.PUBLISHED);
        }
        if (eventToUpdate.getStateAction() == StateActionAdmin.REJECT_EVENT) {
            if (event.getPublished() != null) {
                throw new Conflict409Exception("событие опубликовано ранее");
            }

            event.setState(EventState.CANCELED);
        }

        if (eventToUpdate.getLocation() != null) {
            locationService.removeLocation(event.getLocationId());
            Location location = locationService.saveLocation(eventToUpdate.getLocation());
            event.setLocationId(location.getId());
            event.setLocation(location);
        }

        Helpers.composeFieldsForUpdateEventAdmin(eventToUpdate, event);
        Event eventSaved = repository.save(event);
        eventSaved.setConfirmedRequests(getConfirmedReqCounter(eventSaved.getId()));
        Long views = statService.getPublicEventViewsById(eventSaved.getId());
        eventSaved.setViews(views);
        return EventMapper.toEventFullDto(eventSaved);
    }

    @Transactional
    @Override
    public List<EventShortDto> findEventsByParams(EventSearchParams searchParams) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(searchParams.getFrom(), searchParams.getSize()), searchParams.getSize());
        if (searchParams.getRangeStart() == null) {
            searchParams.setRangeStart(LocalDateTime.now());
        }
        List<Event> events;
        if (searchParams.getPaid() == null && searchParams.getText() == null) {
            events = repository.findEventsByParamsOnlyCategories(
                    searchParams.getCategories(),
                    searchParams.getRangeStart(),
                    searchParams.getRangeEnd(),
                    page
            ).getContent();
        } else {
            events = repository.findEventsByParams(
                            searchParams.getCategories(),
                            searchParams.getRangeStart(),
                            searchParams.getRangeEnd(),
                            searchParams.getPaid(),
                            searchParams.getText(),
                            searchParams.getOnlyAvailable(),
                            page
                    )
                    .getContent();
        }

        // если указан статус сортировки VIEWS -> сортируем согласно этому полю
        for (Event ev : events) {
            Long views = statService.getPublicEventViewsById(ev.getId());
            ev.setViews(views);
            ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
        }

        List<Event> eventsSorted = events;
        if (searchParams.getSort() == EventSortSearch.VIEWS) {
            eventsSorted = events.stream().sorted(Comparator.comparingLong(Event::getViews).reversed()).collect(Collectors.toList());
        }

        return EventMapper.toEventShortDto(eventsSorted);
    }

    @Transactional
    @Override
    public List<EventFullDto> findEventsByParamsAdmin(EventAdminSearchParams searchParams) {
        Pageable page = PageRequest.of(Helpers.getPageNumber(searchParams.getFrom(), searchParams.getSize()), searchParams.getSize());
        if (searchParams.getRangeStart() == null) {
            searchParams.setRangeStart(LocalDateTime.now());
        }

        List<Event> events = repository.findEventsByParamsAdmin(
                        searchParams.getUsers(),
                        searchParams.getStates(),
                        searchParams.getCategories(),
                        searchParams.getRangeStart(),
                        searchParams.getRangeEnd(),
                        page
                )
                .getContent();

        for (Event ev : events) {
            Long views = statService.getPublicEventViewsById(ev.getId());
            ev.setViews(views);
            ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
        }
        return EventMapper.toEventFullDto(events);
    }

    @Override
    public Long getConfirmedReqCounter(Long eventId) {
        RequestCounter rc = repositorySpecRequest.findRequestCountAllByStatus(eventId, RequestStatus.CONFIRMED);
        return rc.getCounter();
    }

    @Override
    public List<Event> getEventsWithConfirmedReqCounter(List<Event> events) {
        List<Event> eventsAdded = events.stream()
                .map(ev -> {
                    ev.setConfirmedRequests(getConfirmedReqCounter(ev.getId()));
                    return ev;
                })
                .collect(Collectors.toList());
        return eventsAdded;
    }
}
