package ru.practicum.ewm.stats.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.StatisticResponseDto;

import java.net.URI;
import java.util.List;

public class BaseClient {
    protected final RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    protected <T> ResponseEntity<Object> post(URI url, T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statisticServerResponse;
        try {
            statisticServerResponse = rest.exchange(url, HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statisticServerResponse);
    }

    protected ResponseEntity<List<StatisticResponseDto>> getStatistics(URI url) {
        HttpEntity<ResponseEntity<Object>> requestEntity = new HttpEntity<>(null, defaultHeaders());

        try {
            return rest.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
            });
        } catch (HttpStatusCodeException e) {
            throw new RuntimeException("Ошибка сервиса статистики. Код: " + e.getStatusCode() + " Сообщение: " + e.getResponseBodyAsString());
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
