package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.dto.EventRequestStatusUpdateResponse;
import ru.practicum.ewm.main.dto.RequestDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.exception.ValidateException;
import ru.practicum.ewm.main.model.*;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.checkUser(userId);
        Event event = eventRepository.checkEvent(eventId);
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new ConflictException("Нельзя создать запрос на участие, т.к. вы являетесь инициатором события с id: " + eventId);
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии с id: " + eventId);
        }
        Request existingRequest = requestRepository.findByUserIdAndEventId(userId, eventId);
        if (existingRequest != null) {
            throw new ConflictException("Нельзя создать повторный запрос, текущий с id: " + existingRequest.getId());
        }
        checkEventParticipantLimit(event);
        Request newRequest = Request.builder()
                .user(user)
                .event(event)
                .status(RequestStatus.PENDING)
                .build();
        if (!event.getIsRequestModerationRequired() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        }
        return requestMapper.toRequestDto(
                requestRepository.save(newRequest));
    }

    public List<RequestDto> getUserRequests(Long userId) {
        userRepository.checkUser(userId);
        return requestMapper.toRequestDto(
                requestRepository.findAllByUserId(userId));
    }

    public RequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.checkUser(userId);
        Request existingRequest = checkRequest(requestId);
        if (existingRequest.getStatus().equals(RequestStatus.PENDING)) {
            existingRequest.setStatus(RequestStatus.CANCELED);
            return requestMapper.toRequestDto(
                    requestRepository.save(existingRequest));
        }
        return requestMapper.toRequestDto(existingRequest);
    }

    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) {
        userRepository.checkUser(userId);
        eventRepository.checkEvent(eventId);
        return requestMapper.toRequestDto(
                requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    public EventRequestStatusUpdateResponse updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest requestDto) {
        userRepository.checkUser(userId);
        Event event = eventRepository.checkEvent(eventId);
        int availableParticipantCount = checkEventParticipantLimit(event);
        List<Request> requests = requestRepository.findAllById(requestDto.getRequestIds());
        if (!event.getIsRequestModerationRequired() || event.getParticipantLimit() == 0) {
            return EventRequestStatusUpdateResponse.builder()
                    .confirmedRequests(requestMapper.toRequestDto(requests))
                    .build();
        }
        RequestStatus status = requestDto.getStatus();
        if (status.equals(RequestStatus.PENDING)) {
            throw new ValidateException("Некорректное значение параметра status: " + RequestStatus.PENDING);
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        int i = 0;
        for (; i < requests.size() && i < availableParticipantCount; i++) {
            Request request = requests.get(i);
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Заявка c id: " + request.getId() +
                        ", которую вы пытаетесь изменить, не находится в состоянии ожидания подтверждения");
            }
            request.setStatus(status);
            if (status.equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(request);
            } else {
                rejectedRequests.add(request);
            }
        }
        if (i == availableParticipantCount) {
            requestRepository.saveAllAndFlush(confirmedRequests); //на случай если кроме тех которые пришли надо отклонить вообще все остальные
            rejectedRequests.addAll(requests.subList(i, requests.size()));
            List<Request> allPendingRequests = requestRepository.findAllByStatus(RequestStatus.PENDING);
            for (Request r : allPendingRequests) {
                r.setStatus(RequestStatus.REJECTED);
            }
        } //all requests lists in @Transactional method managed by hibernate & will be updated w/o saveAll invoke
        return EventRequestStatusUpdateResponse.builder()
                .confirmedRequests(requestMapper.toRequestDto(confirmedRequests))
                .rejectedRequests(requestMapper.toRequestDto(rejectedRequests))
                .build();
    }

    private Request checkRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id запроса на участие: " + id));
    }

    private int checkEventParticipantLimit(Event event) {
        int requestCount = requestRepository.findAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size();
        if (requestCount > 0 && requestCount == event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов на участие в событии: " + event.getId());
        }
        return event.getParticipantLimit() - requestCount;
    }
}
