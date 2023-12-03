package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.StatisticRequestDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatisticClient extends BaseClient {
    private final String statisticServerUrl;

    @Autowired
    public StatisticClient(RestTemplateBuilder builder, @Value("${stats-server.url}") String statisticServerUrl) {
        super(
                builder
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
        this.statisticServerUrl = statisticServerUrl;
    }

    public ResponseEntity<Object> createHit(StatisticRequestDto requestDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(statisticServerUrl + "/hit").build().toUri();
        return post(url, requestDto);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return get(UriComponentsBuilder.fromHttpUrl(statisticServerUrl + "/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .build()
                .encode()
                .toUri());
    }
}