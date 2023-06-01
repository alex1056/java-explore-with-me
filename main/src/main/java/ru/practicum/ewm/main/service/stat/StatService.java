package ru.practicum.ewm.main.service.stat;


import ru.practicum.ewm.main.model.event.Event;

import java.util.List;

public interface StatService {
    public void saveHit(String uri);

    public Long getViews(String uri);

    public Long getPublicEventViewsById(Long eventId);

    List<Event> getAddEventsWithViews(List<Event> events);
}
