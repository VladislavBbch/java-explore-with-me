package ru.practicum.ewm.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.client.StatisticClient;
import ru.practicum.ewm.stats.dto.StatisticRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Import(value = {StatisticClient.class})
public class Controller {
    private final StatisticClient client;

    @PostMapping("/hit")
    public ResponseEntity<Object> createHit(@RequestBody @Valid StatisticRequestDto requestDto) {
        log.info("Начало обработки запроса на добавление записи в статистику");
        ResponseEntity<Object> result = client.createHit(requestDto);
        log.info("Окончание обработки запроса на добавление записи в статистику");
        return result;
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistics(@RequestParam String start,
                                                @RequestParam String end,
                                                @RequestParam(defaultValue = "") String[] uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Начало обработки запроса на получение статистики" + (unique ? " от уникальных пользователей" : "") +
                " с {} по {} для uri: " + (uris.length > 0 ? Arrays.toString(uris) : "all"), start, end);
        ResponseEntity<Object> result = client.getStatistics(
                LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                List.of(uris),
                unique);
        log.info("Окончание обработки запроса на получение статистики");
        return result;
    }
}
