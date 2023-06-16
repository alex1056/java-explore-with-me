package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.request.RequestDto;

import ru.practicum.ewm.main.service.request.RequestService;
import ru.practicum.ewm.main.service.stat.StatService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestService requestService;
    private final StatService statService;

    @GetMapping("/{userId}/requests")
    public List<RequestDto> findRequestsByUserId(@PathVariable Long userId) {
        statService.saveHit("/users/" + userId + "/requests");
        return requestService.findRequestByRequesterId(userId);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> saveRequest(@PathVariable Long userId,
                                              @RequestParam Long eventId
    ) {
        statService.saveHit("/users/" + userId + "/requests");
        RequestDto request = requestService.saveRequest(userId, eventId);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto updateRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId
    ) {
        statService.saveHit("/users/" + userId + "/requests" + requestId + "/cancel");
        return requestService.updateRequest(userId, requestId);
    }
}
