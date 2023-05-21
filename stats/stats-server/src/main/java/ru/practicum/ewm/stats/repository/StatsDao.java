package ru.practicum.ewm.stats.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.stats.exceptions.NotFoundException;
import ru.practicum.ewm.stats.model.Stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("StatsDB")
public class StatsDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StatsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Stat> create(Stat stat) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("STATS")
                .usingGeneratedKeyColumns("id");
        int statsId = simpleJdbcInsert.executeAndReturnKey(toMap(stat)).intValue();
        return findStatById(statsId);
    }

    public Optional<Stat> findStatById(int id) {
        String sql = "select * from STATS where id = ?";
        Optional<Stat> stat = jdbcTemplate.query(sql, (rs, rowNum) -> makeStat(rs), id).stream()
                .findAny();
        if (stat.isEmpty())
            throw new NotFoundException(String.format("Запись статистики с идентификатором %s не найдена", id));
        return stat;
    }

    public List<ViewStats> findStatByStartAndEndDateGibrid(LocalDateTime start, LocalDateTime end, Boolean unique, List<String> uris) {
        List<String> urisLocal = new ArrayList<>();
        String param = "";

        if (uris != null) {
            urisLocal = uris;
            StringBuilder sb = new StringBuilder();
            for (String el : urisLocal) {
                sb.append("uri=");
                sb.append("'");
                sb.append(el);
                sb.append("'");
                sb.append(" OR ");
            }
            sb.delete(sb.length() - 3, sb.length());
            param = "AND " + sb;
        }

        String countSqlPart = unique ? " count(distinct ip) " : " count(*) ";
        String urisSqlPart = urisLocal.size() > 0 ? param : "";
        String sql = "select app, uri," + countSqlPart + "as hits from stats " +
                "where "
                +
                "created >= ? AND created <= ? "
                +
                urisSqlPart
                +
                "group by app, uri "
                +
                "order by hits DESC ";
//        System.out.println(sql);
        if (urisLocal.size() > 0) {
            List<ViewStats> viewStats = jdbcTemplate.query(sql, (rs, rowNum) -> makeViewStats(rs), start, end);
            return viewStats;
        } else {
            List<ViewStats> viewStats = jdbcTemplate.query(sql, (rs, rowNum) -> makeViewStats(rs), start, end);
            return viewStats;
        }
    }

    private ViewStats makeViewStats(ResultSet rs) throws SQLException {
        return new ViewStats(
                rs.getString("app"),
                rs.getString("uri"),
                rs.getLong("hits")
        );
    }

    private Stat makeStat(ResultSet rs) throws SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new Stat(
                rs.getLong("id"),
                rs.getString("app"),
                rs.getString("uri"),
                rs.getString("ip"),
                LocalDateTime.parse(rs.getString("created"), formatter)
        );
    }

    private Map<String, Object> toMap(Stat stat) {
        Map<String, Object> values = new HashMap<>();
        values.put("app", stat.getApp());
        values.put("uri", stat.getUri());
        values.put("ip", stat.getIp());
        values.put("created", stat.getCreated());
        return values;
    }
}
