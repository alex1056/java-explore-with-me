package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findEventById(Long id);

    @Query("select ev from Event ev where ev.id = :id and ev.published <> null")
    Optional<Event> findEventByIdPublished(@Param("id") Long id);

    @Query("select ev from Event ev where ev.categoryId = :categoryId")
    List<Event> findEventByCategoryId(@Param("categoryId") Long categoryId);


    @Query("select ev from Event ev where ev.id in :ids order by id asc")
    List<Event> findAllByIdsOrderByIdAsc(@Param("ids") List<Long> ids);

    @Query("select ev from Event ev where ev.initiatorId = :userId and ev.id = :eventId")
    Optional<Event> findUserEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("select ev from Event ev where ev.initiatorId = :userId")
    Page<Event> findUserEvents(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT e FROM Event e WHERE " +
            "e.published <> NULL AND " +
            "(e.categoryId IN (:categories) OR :categories = NULL) AND " +
            "(e.eventDate>=:rangeStart OR CAST(:rangeStart AS date) = NULL) AND (e.eventDate<=:rangeEnd OR CAST(:rangeEnd AS date) = NULL) AND " +
            "(e.paid = :paid OR :paid = NULL) AND " +
            "(upper(e.annotation) LIKE upper(concat('%', :text, '%')) " +
            "OR upper(e.description) LIKE upper(concat('%', :text, '%')) OR :text = NULL) AND " +
            "( :onlyAvailable = true AND e.participantLimit > ( " +
            "SELECT count(*) from Request r " +
            "where e.id = r.event AND r.status = 'CONFIRMED') OR :onlyAvailable = false )" +
            "ORDER BY e.eventDate ASC"
    )
    Page<Event> findEventsByParams(List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean paid, String text, Boolean onlyAvailable, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "e.published <> NULL AND " +
            "(e.categoryId IN (:categories) OR :categories = NULL) AND " +
            "(e.eventDate>=:rangeStart OR CAST(:rangeStart AS date) = NULL) AND (e.eventDate<=:rangeEnd OR CAST(:rangeEnd AS date) = NULL) " +
            "ORDER BY e.eventDate ASC"
    )
    Page<Event> findEventsByParamsOnlyCategories(List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "(e.initiatorId IN (:users) OR :users = NULL) AND " +
            "(e.state IN (:states) OR :states = NULL) AND " +
            "(e.categoryId IN (:categories) OR :categories = NULL) AND " +
            "(e.eventDate>=:rangeStart OR CAST(:rangeStart AS date) = NULL) AND (e.eventDate<=:rangeEnd OR CAST(:rangeEnd AS date) = NULL) " +
            "ORDER BY e.eventDate ASC"
    )
    Page<Event> findEventsByParamsAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select ev from Event ev " +
            "where ev.locationId IN (" +
            "select l.id from Location l " +
            "where distance(l.lat, l.lon, :locLat, :locLon) <= :radius) " +
            "order by ev.createdOn DESC ")
    Page<Event> findEventsInLocationAdmin(Double locLat,
                                          Double locLon,
                                          Double radius,
                                          Pageable page
    );

    @Query("select ev from Event ev " +
            "where ev.published != null AND ev.locationId IN (" +
            "select l.id from Location l " +
            "where distance(l.lat, l.lon, :locLat, :locLon) <= :radius) " +
            "order by ev.createdOn DESC ")
    Page<Event> findEventsInLocationUser(Double locLat,
                                         Double locLon,
                                         Double radius,
                                         Pageable page
    );


}
