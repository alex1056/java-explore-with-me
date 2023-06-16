package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.request.Request;
import ru.practicum.ewm.main.model.request.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findRequestByRequester(Long requester);

    List<Request> findRequestByEvent(Long event);

    @Query("select r from Request r where r.requester = :userId and r.event = :eventId")
    Optional<Request> findRequest(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("select r from Request r where r.requester = :userId and r.id = :requestId")
    Optional<Request> findRequestByUserIdAndId(@Param("userId") Long userId, @Param("requestId") Long requestId);

    @Query("UPDATE Request r SET r.status = :newStatus WHERE r.id in :ids")
    @Modifying
    Integer updateRequestStatusBulk(
            @Param("ids") List<Long> ids,
            @Param("newStatus") RequestStatus newStatus
    );

    @Query("select r from Request r where r.id in :ids")
    List<Request> findRequestsByIds(@Param("ids") List<Long> ids);
}
