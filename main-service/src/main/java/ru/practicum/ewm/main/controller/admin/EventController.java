package ru.practicum.ewm.main.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.controller.Update;
import ru.practicum.ewm.main.dto.EventResponseDto;
import ru.practicum.ewm.main.dto.EventUpdateAdminRequestDto;
import ru.practicum.ewm.main.model.EventState;
import ru.practicum.ewm.main.service.EventService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.main.Constant.PATTERN;
import static ru.practicum.ewm.main.controller.Constant.ADMIN_URL_PREFIX;

@RestController
@RequestMapping(path = ADMIN_URL_PREFIX + "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventResponseDto> searchEvents(
            @RequestParam(defaultValue = "") Long[] users,
            @RequestParam(defaultValue = "") EventState[] states,
            @RequestParam(defaultValue = "") Long[] categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(1000) Integer size) {
        log.info("Начало обработки запроса по поиску событий");
        List<EventResponseDto> events = eventService.searchEvents(
                List.of(users),
                List.of(states),
                List.of(categories),
                rangeStart,
                rangeEnd,
                from,
                size);
        log.info("Окончание обработки запроса по поиску событий");
        return events;
    }

    @PatchMapping("/{id}")
    public EventResponseDto updateEventByAdmin(@PathVariable Long id,
                                               @RequestBody @Validated({Update.class}) EventUpdateAdminRequestDto eventDto) {
        log.info("Начало обработки запроса на обновление события администратором: {}", id);
        EventResponseDto existingEvent = eventService.updateEventByAdmin(id, eventDto);
        log.info("Окончание обработки запроса на обновление события администратором");
        return existingEvent;
    }
}
