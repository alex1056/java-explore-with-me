package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TestController {

    private final StatsClient statsClient;

    @PostMapping("/testhit")
    public ResponseEntity<?> addStats(@Validated @RequestBody EndpointHit endpointHit) {

        statsClient.hit(endpointHit.getApp(), endpointHit.getUri(), endpointHit.getIp());

        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @GetMapping("/teststats")
    public ResponseEntity<Object> getTestStats(
            @RequestParam("start")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam("end")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false) Boolean unique
    ) {
        Boolean uniqueValue = unique == null ? false : unique;
        ViewsStatsRequest viewsStatsRequest = new ViewsStatsRequest(
                start,
                end,
                uniqueValue,
                uris
        );
        return statsClient.get(viewsStatsRequest);
    }
}
