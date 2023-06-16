package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.request.Request;
import ru.practicum.ewm.main.model.request.RequestCounter;
import ru.practicum.ewm.main.model.request.RequestIds;
import ru.practicum.ewm.main.model.request.RequestStatus;

import java.util.List;

public interface RequestSpecRepository extends CrudRepository<Request, Long> {

    @Query("select new ru.practicum.ewm.main.model.request.RequestCounter(COUNT(*)) from Request r where r.event = :eventId")
    RequestCounter findRequestNumber(@Param("eventId") Long eventId);

    @Query("select new ru.practicum.ewm.main.model.request.RequestCounter(COUNT(*)) " +
            "from Request r where r.event = :eventId and r.id in :ids")
    RequestCounter findRequestNumberForUpdating(@Param("eventId") Long eventId, @Param("ids") List<Long> ids);

    @Query("select new ru.practicum.ewm.main.model.request.RequestCounter(COUNT(*)) " +
            "from Request r where r.status = :searchStatus and r.id in :ids")
    RequestCounter findRequestCountAllPending(@Param("searchStatus") RequestStatus searchStatus, @Param("ids") List<Long> ids);

    @Query("select new ru.practicum.ewm.main.model.request.RequestCounter(COUNT(*)) " +
            "from Request r where r.event = :eventId and r.status = :searchStatus")
    RequestCounter findRequestCountAllByStatus(@Param("eventId") Long eventId, @Param("searchStatus") RequestStatus searchStatus);

    @Query("select new ru.practicum.ewm.main.model.request.RequestIds(r.id) " +
            "from Request r where r.event = :eventId and r.status = :searchStatus")
    List<RequestIds> findIdsRequestPending(@Param("eventId") Long eventId, @Param("searchStatus") RequestStatus searchStatus);
}
