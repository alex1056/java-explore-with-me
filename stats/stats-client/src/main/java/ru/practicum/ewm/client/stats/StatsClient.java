package ru.practicum.ewm.client.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.stats.EndpointHit;
import ru.practicum.ewm.dto.stats.ViewsStatsRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public ResponseEntity<Object> get(ViewsStatsRequest viewsStatsRequest) {
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", "{start}")
                .queryParam("end", "{end}")
                .queryParam("unique", "{unique}")
                .queryParamIfPresent("unique", Optional.ofNullable(viewsStatsRequest.getUnique()))
                .queryParamIfPresent("uris", Optional.ofNullable(viewsStatsRequest.getUris()))
                .encode()
                .toUriString();
//        System.out.println(urlTemplate);

        Map<String, Object> params = new HashMap<>();
//        params.put("start", viewsStatsRequest.getStart());
        params.put("start", viewsStatsRequest.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        params.put("end", viewsStatsRequest.getEnd());
        params.put("end", viewsStatsRequest.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("unique", viewsStatsRequest.getUnique());
        if (viewsStatsRequest.getUris() != null) {
            params.put("uris", viewsStatsRequest.getUris().toArray(new String[0]));
        } else {
            params.put("uris", null);
        }
//        System.out.println(viewsStatsRequest);
//        System.out.println(params);
        return get(urlTemplate, params);
    }
}
