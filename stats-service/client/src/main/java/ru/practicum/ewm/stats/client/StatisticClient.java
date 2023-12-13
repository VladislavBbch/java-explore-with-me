package ru.practicum.ewm.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.StatisticRequestDto;
import ru.practicum.ewm.stats.dto.StatisticResponseDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class StatisticClient extends BaseClient {
    private final String statisticServerUrl;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        log.info("Начало обработки запроса на добавление статистики: " + requestDto);
        URI url = UriComponentsBuilder.fromHttpUrl(statisticServerUrl + "/hit").build().toUri();
        ResponseEntity<Object> response = post(url, requestDto);
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Окончание обработки запроса на добавление статистики: " + requestDto);
        } else {
            log.error("Ошибка с кодом: {} обработки запроса на добавление статистики: {}. Сообщение: {}",
                    response.getStatusCode(), requestDto, response.getBody());
        }
        return response;
    }

    public ResponseEntity<List<StatisticResponseDto>> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Начало обработки запроса на получение статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        ResponseEntity<List<StatisticResponseDto>> response = getStatistics(
                UriComponentsBuilder.fromHttpUrl(statisticServerUrl + "/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build()
                        .encode()
                        .toUri());
        log.info("Окончание обработки запроса на получение статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return response;
    }
}