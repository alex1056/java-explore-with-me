package ru.practicum.ewm.main.helper;

import ru.practicum.ewm.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.main.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.main.model.event.Event;

import java.util.List;
import java.util.stream.Collectors;

public class Helpers {
    public static Event composeFieldsForUpdateEvent(UpdateEventUserRequest eventReq, Event event) {
        event.setAnnotation(eventReq.getAnnotation() != null ? eventReq.getAnnotation() : event.getAnnotation());
        event.setCategoryId(eventReq.getCategory() != null ? eventReq.getCategory() : event.getCategoryId());
        event.setDescription(eventReq.getDescription() != null ? eventReq.getDescription() : event.getDescription());
        event.setTitle(eventReq.getTitle() != null ? eventReq.getTitle() : event.getTitle());
        event.setEventDate(eventReq.getEventDate() != null ? eventReq.getEventDate() : event.getEventDate());
        event.setPaid(eventReq.getPaid() != null ? eventReq.getPaid() : event.getPaid());
        event.setParticipantLimit(eventReq.getParticipantLimit() != null ? eventReq.getParticipantLimit() : event.getParticipantLimit());
        event.setRequestModeration(eventReq.getRequestModeration() != null ? eventReq.getRequestModeration() : event.getRequestModeration());
        return event;
    }

    public static Event composeFieldsForUpdateEventAdmin(UpdateEventAdminRequest eventReq, Event event) {
        event.setAnnotation(eventReq.getAnnotation() != null ? eventReq.getAnnotation() : event.getAnnotation());
        event.setCategoryId(eventReq.getCategory() != null ? eventReq.getCategory() : event.getCategoryId());
        event.setDescription(eventReq.getDescription() != null ? eventReq.getDescription() : event.getDescription());
        event.setTitle(eventReq.getTitle() != null ? eventReq.getTitle() : event.getTitle());
        event.setEventDate(eventReq.getEventDate() != null ? eventReq.getEventDate() : event.getEventDate());
        event.setPaid(eventReq.getPaid() != null ? eventReq.getPaid() : event.getPaid());
        event.setParticipantLimit(eventReq.getParticipantLimit() != null ? eventReq.getParticipantLimit() : event.getParticipantLimit());
        event.setRequestModeration(eventReq.getRequestModeration() != null ? eventReq.getRequestModeration() : event.getRequestModeration());
        return event;
    }


    public static Integer getPageNumber(Integer startIndex, Integer size) {
        Integer result = startIndex / size;
        return result;
    }

    public static Boolean isEventsEqual(List<Event> listSaved, List<Event> listUpdate) {
        if (listSaved == null || listUpdate == null) return true;
        List<Long> idsUpdate = listUpdate.stream()
                .distinct()
                .map(event -> event.getId())
                .collect(Collectors.toList());
        List<Long> idsSaved = listSaved.stream()
                .distinct()
                .map(event -> event.getId())
                .collect(Collectors.toList());

        if (idsUpdate.size() != idsSaved.size()) return false;

        List result = idsUpdate.stream()
                .filter(idsSaved::contains)
                .collect(Collectors.toList());
        if (result.size() == idsSaved.size()) return true;
        return false;
    }
}
