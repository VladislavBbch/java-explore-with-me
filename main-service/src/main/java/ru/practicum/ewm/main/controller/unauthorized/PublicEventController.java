package ru.practicum.ewm.main.controller.unauthorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.EventResponseDto;
import ru.practicum.ewm.main.dto.EventShortResponseDto;
import ru.practicum.ewm.main.model.EventSortType;
import ru.practicum.ewm.main.service.EventService;
import ru.practicum.ewm.stats.client.StatisticClient;
import ru.practicum.ewm.stats.dto.StatisticRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.main.Constant.PATTERN;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
@Import(value = {StatisticClient.class})
public class PublicEventController {
    private final EventService eventService;
    private final StatisticClient statisticClient;

    @Value("${application.name}")
    private String applicationName;

    @GetMapping
    public List<EventShortResponseDto> searchPublishedEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "") Long[] categories,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSortType sort,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(1000) Integer size,
            HttpServletRequest request) {
        log.info("Начало обработки запроса по поиску опубликованных событий");
        List<EventShortResponseDto> events = eventService.searchPublishedEvents(
                text,
                paid,
                rangeStart,
                rangeEnd,
                List.of(categories),
                onlyAvailable,
                sort,
                from,
                size);
        statisticClient.createHit(StatisticRequestDto.builder()
                .app(applicationName)
                .uri("/events")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        log.info("Окончание обработки запроса по поиску опубликованных событий");
        return events;
    }

    @GetMapping("/{id}")
    public EventResponseDto getPublishedEventById(@PathVariable Long id,
                                                  HttpServletRequest request) {
        log.info("Начало обработки запроса по получению опубликованного события: {}", id);
        EventResponseDto event = eventService.getPublishedEventById(id);
        statisticClient.createHit(StatisticRequestDto.builder()
                .app(applicationName)
                .uri("/events/" + id)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        log.info("Окончание обработки запроса по получению опубликованного события");
        return event;
    }
}