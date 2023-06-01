package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.util.List;

public interface StatsService {

    void createStat(EndpointHit endpointHit);

    List<ViewStats> getStats(ViewsStatsRequest viewsStatsRequest);
}
