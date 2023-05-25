package ru.practicum.ewm.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;
import ru.practicum.ewm.stats.service.StatsService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<?> addStats(@Validated @RequestBody EndpointHit endpointHit, HttpServletRequest request) {
        endpointHit.setIp(request.getRemoteAddr());
        statsService.createStat(endpointHit);
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
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
        return statsService.getStats(viewsStatsRequest);
    }
}
