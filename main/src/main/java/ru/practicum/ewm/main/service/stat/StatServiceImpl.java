package ru.practicum.ewm.main.service.stat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.stats.StatsClient;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;
import ru.practicum.ewm.main.model.event.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatsClient statsClient;

    @Override
    public void saveHit(String uri) {
        EndpointHit endpointHit = new EndpointHit(
                "ewb",
                uri,
                "127.0.0.1",
                LocalDateTime.now()
        );
        statsClient.hit(endpointHit.getApp(), endpointHit.getUri(), endpointHit.getIp());
    }

    public Long getViews(String uri) {
        List<String> uris = List.of(uri);
        ViewsStatsRequest viewsStatsRequest = new ViewsStatsRequest(
                LocalDateTime.now().minusDays(365),
                LocalDateTime.now().plusDays(1),
                true,
                uris
        );

        List<ViewStats> statsList = statsClient.get(viewsStatsRequest);

        if (statsList.size() > 0) {
            return statsList.get(0).getHits();
        }
        return 0L;
    }

    @Override
    public Long getPublicEventViewsById(Long eventId) {
        return getViews("/events/" + eventId);
    }

    @Override
    public List<Event> getAddEventsWithViews(List<Event> events) {
        return events.stream().map(ev -> {
            ev.setViews(getPublicEventViewsById(ev.getId()));
            return ev;
        }).collect(Collectors.toList());
    }
}
