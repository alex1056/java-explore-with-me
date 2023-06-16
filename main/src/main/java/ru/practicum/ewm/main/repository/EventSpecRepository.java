package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.model.event.EventCounter;

import java.util.List;

public interface EventSpecRepository extends CrudRepository<Event, Long> {

    @Query("select new ru.practicum.ewm.main.model.event.EventCounter(COUNT(*)) from Event ev where ev.id in :eventIds")
    EventCounter countEventsByIds(@Param("eventIds") List<Long> eventIds);
}
