package ru.practicum.ewm.main.service.request;


import ru.practicum.ewm.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.dto.request.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto saveRequest(Long userId, Long eventId);

    RequestDto updateRequest(Long userId, Long requestId);

    List<RequestDto> findRequestByRequesterId(Long requesterId);

    List<RequestDto> findRequestByEventId(Long eventId);

    List<RequestDto> getUserEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateUserEventRequestsStatus(
            Long userId, Long eventId,
            EventRequestStatusUpdateRequest updateRequestStatus);
}
