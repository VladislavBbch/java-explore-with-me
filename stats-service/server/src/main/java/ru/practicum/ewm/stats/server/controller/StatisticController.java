package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.StatisticRequestDto;
import ru.practicum.ewm.stats.server.controller.dto.StatisticResponseDto;
import ru.practicum.ewm.stats.server.service.StatisticService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    public void createHit(@RequestBody @Valid StatisticRequestDto requestDto) {
        log.info("Начало обработки запроса на добавление записи в статистику");
        statisticService.createHit(requestDto);
        log.info("Окончание обработки запроса на добавление записи в статистику");
    }

    @GetMapping("/stats")
    public List<StatisticResponseDto> getStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(defaultValue = "") String[] uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Начало обработки запроса на получение статистики" + (unique ? " от уникальных пользователей" : "") +
                " с {} по {} для uri: " + (uris.length > 0 ? Arrays.toString(uris) : "all"), start, end);
        List<StatisticResponseDto> result = statisticService.getStatistics(
                start,
                end,
                List.of(uris),
                unique);
        log.info("Окончание обработки запроса на получение статистики");
        return result;
    }
}