package ru.practicum.ewm.main.controller.authorized;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.dto.RequestDto;
import ru.practicum.ewm.main.service.RequestService;

import java.util.List;

import static ru.practicum.ewm.main.controller.Constant.AUTHORIZED_URL_PREFIX;

@RestController
@RequestMapping(path = AUTHORIZED_URL_PREFIX + "/requests")
@RequiredArgsConstructor
@Slf4j
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("Начало обработки запроса на создание запроса на участие пользователя: {} в событии: {}", userId, eventId);
        RequestDto newRequest = requestService.createRequest(userId, eventId);
        log.info("Окончание обработки запроса на создание запроса на участие пользователя: {} в событии: {}", userId, eventId);
        return newRequest;
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("Начало обработки запроса по получению запросов на участие пользователя: {}", userId);
        List<RequestDto> requests = requestService.getUserRequests(userId);
        log.info("Окончание обработки запроса по получению запросов на участие пользователя: {}", userId);
        return requests;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("Начало обработки запроса на отмену запроса на участие с id: {} пользователя: {}", requestId, userId);
        RequestDto cancelledRequest = requestService.cancelRequest(userId, requestId);
        log.info("Окончание обработки запроса на отмену запроса на участие с id: {} пользователя: {}", requestId, userId);
        return cancelledRequest;
    }
}
