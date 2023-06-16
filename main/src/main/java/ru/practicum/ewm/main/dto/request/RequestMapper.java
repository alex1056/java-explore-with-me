package ru.practicum.ewm.main.dto.request;

import ru.practicum.ewm.main.model.request.Request;

import java.util.ArrayList;
import java.util.List;


public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getCreated(),
                request.getRequester(),
                request.getEvent(),
                request.getStatus()
        );
    }

    public static List<RequestDto> toRequestDto(Iterable<Request> requests) {
        List<RequestDto> result = new ArrayList<>();
        for (Request request : requests) {
            result.add(toRequestDto(request));
        }
        return result;
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(Iterable<Request> confirmedRequests, Iterable<Request> rejectedRequests) {
        return new EventRequestStatusUpdateResult(
                toRequestDto(confirmedRequests),
                toRequestDto(rejectedRequests)
        );
    }


}
