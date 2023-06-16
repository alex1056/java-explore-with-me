package ru.practicum.ewm.client.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewStats;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatsClient extends BaseClient {
    private static final String API_PREFIX = "";
    private String serverUrl;

    @Autowired
    public StatsClient(@Value("${stats.service.uri}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.serverUrl = serverUrl;
    }

    public void hit(String application, String uri, String ipAddress) {
        EndpointHit hit = new EndpointHit(
                application,
                uri,
                ipAddress,
                LocalDateTime.now()
        );
        post("/hit", hit);
    }

    public List<ViewStats> get(ViewsStatsRequest viewsStatsRequest) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("unique", "{unique}")
                .queryParamIfPresent("unique", Optional.ofNullable(viewsStatsRequest.getUnique()))
                .queryParamIfPresent("uris", Optional.ofNullable(viewsStatsRequest.getUris()))
                .encode()
                .toUriString();

        Map<String, Object> params = new HashMap<>();
        params.put("start", viewsStatsRequest.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("end", viewsStatsRequest.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("unique", viewsStatsRequest.getUnique());
        if (viewsStatsRequest.getUris() != null) {
            params.put("uris", viewsStatsRequest.getUris().toArray(new String[0]));
        } else {
            params.put("uris", null);
        }

        ResponseEntity response = get(urlTemplate, params);
        HttpStatus code = response.getStatusCode();
        if (code == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            List<ViewStats> pojos = mapper.convertValue(response.getBody(), new TypeReference<List<ViewStats>>() {
            });
            if (pojos == null)
                return new ArrayList<ViewStats>();
            return pojos;
        }
        return new ArrayList<ViewStats>();
    }
}
