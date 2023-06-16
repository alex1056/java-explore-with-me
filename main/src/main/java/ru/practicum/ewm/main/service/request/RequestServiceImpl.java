package ru.practicum.ewm.main.service.request;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.dto.request.RequestDto;
import ru.practicum.ewm.main.dto.request.RequestMapper;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.model.event.Event;
import ru.practicum.ewm.main.model.request.Request;
import ru.practicum.ewm.main.model.request.RequestCounter;
import ru.practicum.ewm.main.model.request.RequestIds;
import ru.practicum.ewm.main.model.request.RequestStatus;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.main.repository.RequestSpecRepository;

import ru.practicum.ewm.main.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final EventService eventService;
    private final RequestSpecRepository requestSpecRepository;

    @Transactional
    @Override
    public RequestDto saveRequest(Long userId, Long eventId) {
        Event event = eventService.getEventOriginById(eventId);
        if (event.getInitiatorId().equals(userId)) {
            throw new ConflictException("инициатор события не может отправлять запрос себе");
        }
        if (event.getPublished() == null) {
            throw new ConflictException("нельзя участвовать в неопубликованном событии ");
        }
        Optional<Request> requestOpt = repository.findRequest(userId, eventId);
        if (requestOpt.isPresent()) {
            throw new ConflictException("запрос уже имеется");
        }

        RequestCounter rc = requestSpecRepository.findRequestCountAllByStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && rc.getCounter() >= event.getParticipantLimit()) {
            throw new ConflictException("достигнуто ограничение мест");
        }

        Request request = new Request(
                null,
                LocalDateTime.now(),
                userId,
                eventId,
                (!event.getRequestModeration() || event.getParticipantLimit() == 0) ? RequestStatus.CONFIRMED : RequestStatus.PENDING
        );
        Request requestSaved = repository.save(request);
        return RequestMapper.toRequestDto(requestSaved);
    }

    @Transactional
    @Override
    public RequestDto updateRequest(Long userId, Long requestId) {
        Optional<Request> requestOptional = repository.findRequestByUserIdAndId(userId, requestId);
        requestOptional.orElseThrow(() -> new NotFoundException("запрос с id= " + requestId + " пользователя userId=" + userId));
        Request request = requestOptional.get();
        request.setStatus(RequestStatus.CANCELED);
        Request requestSaved = repository.save(request);
        return RequestMapper.toRequestDto(requestSaved);
    }

    @Transactional
    @Override
    public List<RequestDto> findRequestByRequesterId(Long requesterId) {
        List<Request> requestList = repository.findRequestByRequester(requesterId);
        return RequestMapper.toRequestDto(requestList);
    }

    @Transactional
    @Override
    public List<RequestDto> findRequestByEventId(Long eventId) {
        List<Request> requestList = repository.findRequestByEvent(eventId);
        return RequestMapper.toRequestDto(requestList);
    }

    @Transactional
    @Override
    public List<RequestDto> getUserEventRequests(Long userId, Long eventId) {
        eventService.getUserOneEvent(userId, eventId);
        List<RequestDto> requests = findRequestByEventId(eventId);
        return requests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateUserEventRequestsStatus(
            Long userId, Long eventId,
            EventRequestStatusUpdateRequest updateRequestStatus
    ) {
        List<Long> ids = updateRequestStatus.getRequestIds();
        List<Request> rConf = new ArrayList<>();
        List<Request> rRej = new ArrayList<>();

        System.out.println("updateRequestStatus=" + updateRequestStatus);
        System.out.println("ids=" + ids);
        RequestCounter rc2 = requestSpecRepository.findRequestCountAllByStatus(eventId, RequestStatus.CONFIRMED);
        long confRequestNumber = rc2.getCounter();
        /* проверяем какой статус в заявках надо проставить */
        if (updateRequestStatus.getStatus() == RequestStatus.REJECTED && ids.size() > 0 &&
                confRequestNumber == 0
        ) {
            repository.updateRequestStatusBulk(ids, RequestStatus.REJECTED);
            rConf = new ArrayList<>();
            rRej = repository.findRequestsByIds(ids);
            return RequestMapper.toEventRequestStatusUpdateResult(rConf, rRej);
        }

        /* 0. лимит заявок исчерпан */
        Event event = eventService.getEvent(userId, eventId);
        int participantLimit = event.getParticipantLimit();
        Boolean requestModeration = event.getRequestModeration();

        if (participantLimit != 0 && participantLimit == confRequestNumber) {
            throw new ConflictException("Лимит заявок исчерпан");
        }

        /* 1. проверяем все ли id имеются */
        RequestCounter rc = requestSpecRepository.findRequestNumberForUpdating(eventId, ids);
        if (ids.size() != rc.getCounter()) {
            throw new NotFoundException("какая-то заявка из списка ids=" + ids);
        }
        /* 2. проверяем все ли заявки со статусом PENDING */
        RequestCounter rc1 = requestSpecRepository.findRequestCountAllPending(RequestStatus.PENDING, ids);
        if (ids.size() != rc1.getCounter()) {
            throw new ConflictException("какая-то заявка имеет status отличный от PENDING");
        }

        /* 3. если отключена премодерация или без ограничений участников (это учтено при подаче заявок) */
        if (participantLimit == 0 && !requestModeration) {
            repository.updateRequestStatusBulk(ids, RequestStatus.CONFIRMED);
            rConf = repository.findRequestsByIds(ids);
            rRej = new ArrayList<>();
            return RequestMapper.toEventRequestStatusUpdateResult(rConf, rRej);
        }

        /* 4. модерация включена лимит отключен */
        if (participantLimit == 0) {
            repository.updateRequestStatusBulk(ids, RequestStatus.CONFIRMED);
            rConf = repository.findRequestsByIds(ids);
            rRej = new ArrayList<>();
            return RequestMapper.toEventRequestStatusUpdateResult(rConf, rRej);
        }

        /* 5. случай когда модерация влючена и имеется лимит */

        if (participantLimit == confRequestNumber) {
            List<RequestIds> requestIdsToReject = requestSpecRepository.findIdsRequestPending(eventId, RequestStatus.PENDING);
            List<Long> idsToReject = getIdsFromRequestIds(requestIdsToReject);
            // все остальные заявки отклоняем
            repository.updateRequestStatusBulk(idsToReject, RequestStatus.CANCELED);
            rConf = new ArrayList<>();
            rRej = repository.findRequestsByIds(idsToReject);
            return RequestMapper.toEventRequestStatusUpdateResult(rConf, rRej);
        }
        if (participantLimit >= (confRequestNumber + ids.size())) {
            repository.updateRequestStatusBulk(ids, RequestStatus.CONFIRMED);
            rConf = repository.findRequestsByIds(ids);
            rRej = new ArrayList<>();
            return RequestMapper.toEventRequestStatusUpdateResult(rConf, rRej);
        }
        List<Long> idsPart1 = new ArrayList<>();
        int x = (int) (participantLimit - confRequestNumber);
        int i;
        if (x > 0) {
            for (i = 0; i < ids.size(); i++) {
                if (i < x) {
                    idsPart1.add(ids.get(i));
                }
            }

            repository.updateRequestStatusBulk(idsPart1, RequestStatus.CONFIRMED);
            rConf = repository.findRequestsByIds(idsPart1);
            // все остальные заявки отклоняем
            List<RequestIds> requestIdsToReject = requestSpecRepository.findIdsRequestPending(eventId, RequestStatus.PENDING);
            List<Long> idsToReject = getIdsFromRequestIds(requestIdsToReject);
            repository.updateRequestStatusBulk(idsToReject, RequestStatus.CANCELED);
            rRej = repository.findRequestsByIds(idsToReject);
            return RequestMapper.toEventRequestStatusUpdateResult(rConf, rRej);
        }
        return null;
    }

    private List<Long> getIdsFromRequestIds(List<RequestIds> requestIds) {
        return requestIds.stream().map(RequestIds::getId).collect(Collectors.toList());
    }
}
