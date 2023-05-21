package ru.practicum.ewm.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;
import ru.practicum.ewm.stats.model.Stat;
import ru.practicum.ewm.stats.repository.StatsDao;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsDao statsDao;

    @Autowired
    public StatsServiceImpl(StatsDao statsDao) {
        this.statsDao = statsDao;
    }

    @Override
    public void createStat(EndpointHit endpointHit) {
        Stat stat = new Stat(
                null,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
        Optional<Stat> stat1 = statsDao.create(stat);
    }

    @Override
    public List<ViewStats> getStats(ViewsStatsRequest viewsStatsRequest) {
        if (viewsStatsRequest.getUnique() == null && viewsStatsRequest.getUris() == null) {
            List<ViewStats> viewStats = statsDao.findStatByStartAndEndDateGibrid(viewsStatsRequest.getStart(), viewsStatsRequest.getEnd(), false, null);
            return viewStats;
        } else if (viewsStatsRequest.getUris() == null) {
            List<ViewStats> viewStats = statsDao.findStatByStartAndEndDateGibrid(viewsStatsRequest.getStart(), viewsStatsRequest.getEnd(), viewsStatsRequest.getUnique(), null);
            return viewStats;
        } else {
            List<ViewStats> viewStats = statsDao.findStatByStartAndEndDateGibrid(viewsStatsRequest.getStart(), viewsStatsRequest.getEnd(), viewsStatsRequest.getUnique(), viewsStatsRequest.getUris());
            return viewStats;
        }
    }
}
