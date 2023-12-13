package ru.practicum.ewm.main.controller.authorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.controller.Create;
import ru.practicum.ewm.main.controller.Update;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.service.EventService;
import ru.practicum.ewm.main.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.ewm.main.controller.Constant.AUTHORIZED_URL_PREFIX;

@RestController
@RequestMapping(path = AUTHORIZED_URL_PREFIX + "/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(@PathVariable Long userId,
                                        @RequestBody @Validated({Create.class}) EventRequestDto eventDto) {
        log.info("Начало обработки запроса на создание события: {}", eventDto);
        EventResponseDto newEvent = eventService.createEvent(userId, eventDto);
        log.info("Окончание обработки запроса на создание события");
        return newEvent;
    }

    @GetMapping
    public List<EventShortResponseDto> getEvents(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "10") @Min(1) @Max(1000) Integer size) {
        log.info("Начало обработки запроса по получению событий");
        List<EventShortResponseDto> events = eventService.getEvents(userId, from, size);
        log.info("Окончание обработки запроса по получению событий");
        return events;
    }

    @GetMapping("/{eventId}")
    public EventResponseDto getEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        log.info("Начало обработки запроса по получению события: {}", eventId);
        EventResponseDto event = eventService.getEventById(userId, eventId);
        log.info("Окончание обработки запроса по получению события");
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventResponseDto updateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody @Validated({Update.class}) EventUpdateUserRequestDto eventDto) {
        log.info("Начало обработки запроса на обновление события: {}", eventId);
        EventResponseDto event = eventService.updateEvent(userId, eventId, eventDto);
        log.info("Окончание обработки запроса на обновление события");
        return event;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        log.info("Начало обработки запроса по получению запросов на участие в событии: {}", eventId);
        List<RequestDto> requests = requestService.getRequestsByEventId(userId, eventId);
        log.info("Окончание обработки запроса по получению запросов на участие в событии: {}", eventId);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResponse updateRequest(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody @Valid EventRequestStatusUpdateRequest requestDto) {
        log.info("Начало обработки запроса на обновление запроса на участие в событии: {}", eventId);
        EventRequestStatusUpdateResponse request = requestService.updateRequest(userId, eventId, requestDto);
        log.info("Окончание обработки запроса на обновление запроса на участие в событии: {}", eventId);
        return request;
    }
}