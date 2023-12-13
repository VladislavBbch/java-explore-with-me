package ru.practicum.ewm.main.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.*;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ValidateException;
import ru.practicum.ewm.main.model.*;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final RequestRepository requestRepository;
    private final ViewsUtils viewsUtils;
    private final EntityManager entityManager;

    public EventResponseDto createEvent(Long userId, EventRequestDto eventDto) {
        checkEventDate(eventDto.getEventDate());
        Event event = eventMapper.toEvent(userId, eventDto);
        if (event.getIsPaid() == null) {
            event.setIsPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0); // 0 - w/o limit
        }
        if (event.getIsRequestModerationRequired() == null) {
            event.setIsRequestModerationRequired(true);
        }
        event.setState(EventState.PENDING);
        return eventMapper.toEventDto(
                eventRepository.save(event));
    }

    public List<EventShortResponseDto> getEvents(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequestByElement.of(from, size));
        List<Long> eventIds = events.stream().map(Event::getId).collect(toList());
        Map<Long, Integer> requestsCountByEventId = requestRepository.getConfirmedRequestsCountByEvents(eventIds);
        Map<Long, Integer> viewsByEventId = viewsUtils.getViewsByEvents(eventIds, events);
        return eventMapper.toEventShortDto(events, requestsCountByEventId, viewsByEventId);
    }

    public List<EventResponseDto> searchEvents(List<Long> userIds,
                                               List<EventState> states,
                                               List<Long> categoryIds,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        checkDateRange(rangeStart, rangeEnd);

        BooleanExpression query = QEvent.event.id.isNotNull();
        if (userIds.size() > 0) {
            query = query.and(QEvent.event.initiator.id.in(userIds));
        }
        if (states.size() > 0) {
            query = query.and(QEvent.event.state.in(states));
        }
        if (categoryIds.size() > 0) {
            query = query.and((QEvent.event.category.id.in(categoryIds)));
        }
        if (rangeStart != null) {
            query = query.and(QEvent.event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            query = query.and(QEvent.event.eventDate.before(rangeEnd));
        }
        List<Event> events = eventRepository.findAll(query, PageRequestByElement.of(from, size)).toList();
        List<Long> eventIds = events.stream().map(Event::getId).collect(toList());
        Map<Long, Integer> requestsCountByEventId = requestRepository.getConfirmedRequestsCountByEvents(eventIds);
        Map<Long, Integer> viewsByEventId = viewsUtils.getViewsByEvents(eventIds, events);
        return eventMapper.toEventDto(events, requestsCountByEventId, viewsByEventId);
    }

    public List<EventShortResponseDto> searchPublishedEvents(String text,
                                                             Boolean paid,
                                                             LocalDateTime rangeStart,
                                                             LocalDateTime rangeEnd,
                                                             List<Long> categoryIds,
                                                             Boolean onlyAvailable,
                                                             EventSortType sort,
                                                             Integer from,
                                                             Integer size) {
        checkDateRange(rangeStart, rangeEnd);

        BooleanExpression query = QEvent.event.state.eq(EventState.PUBLISHED);
        if (text != null) {
            query = query.and((QEvent.event.annotation.likeIgnoreCase('%' + text + '%'))
                    .or(QEvent.event.description.likeIgnoreCase('%' + text + '%')));
        }
        if (paid != null) {
            query = query.and(QEvent.event.isPaid.eq(paid));
        }
        if (rangeStart == null && rangeEnd == null) {
            query = query.and((QEvent.event.eventDate.after(LocalDateTime.now())));
        } else {
            if (rangeStart != null) {
                query = query.and(QEvent.event.eventDate.after(rangeStart));
            }
            if (rangeEnd != null) {
                query = query.and(QEvent.event.eventDate.before(rangeEnd));
            }
        }
        if (categoryIds.size() > 0) {
            query = query.and((QEvent.event.category.id.in(categoryIds)));
        }
        List<Event> events;
        if (onlyAvailable) {
            JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
            events = queryFactory.selectFrom(QEvent.event)
                    .leftJoin(QEventsRequests.eventsRequests)
                    .on(QEvent.event.id.eq(QEventsRequests.eventsRequests.eventId))
                    .where(QEvent.event.participantLimit.eq(0)
                            .or(QEvent.event.participantLimit
                                    .goe(QEventsRequests.eventsRequests.confirmedRequestCount.coalesce(0))))
                    .where(query)
                    .orderBy(QEvent.event.eventDate.asc())
                    .limit(size)
                    .offset(from)
                    .fetch();

        } else {
            events = eventRepository.findAll(query,
                    PageRequestByElement.of(from, size, new QSort(QEvent.event.eventDate.asc()))).toList();
        }
        List<Long> eventIds = events.stream().map(Event::getId).collect(toList());
        Map<Long, Integer> requestsCountByEventId = requestRepository.getConfirmedRequestsCountByEvents(eventIds);
        Map<Long, Integer> viewsByEventId = viewsUtils.getViewsByEvents(eventIds, events);
        return eventMapper.toEventShortDto(
                events,
                requestsCountByEventId,
                viewsByEventId,
                sort.equals(EventSortType.VIEWS));
    }

    public EventResponseDto getEventById(Long userId, Long id) {
        Event existingEvent = checkEvent(id);
        if (!Objects.equals(existingEvent.getInitiator().getId(), userId)) {
            throw new ConflictException("Вы не являетесь инициатором события с id: " + id);
        }
        Integer confirmedRequestsCount = requestRepository.getEventConfirmedRequestsCount(id);
        Integer views = viewsUtils.getEventViews(id, existingEvent.getPublishedOn());
        return eventMapper.toEventDto(existingEvent, confirmedRequestsCount, views);
    }

    public EventResponseDto getPublishedEventById(Long id) {
        Event event = checkEvent(id);
        Integer confirmedRequestsCount = requestRepository.getEventConfirmedRequestsCount(id);
        Integer views = viewsUtils.getEventViews(id, event.getPublishedOn());
        if (event.getState().equals(EventState.PUBLISHED)) {
            return eventMapper.toEventDto(event, confirmedRequestsCount, views);
        } else {
            throw new ObjectNotFoundException("Несуществующий id события: " + id);
        }
    }

    public EventResponseDto updateEvent(Long userId, Long id, EventUpdateUserRequestDto eventDto) {
        Event existingEvent = checkEvent(id);
        if (!Objects.equals(existingEvent.getInitiator().getId(), userId)) {
            throw new ConflictException("Вы не являетесь инициатором события с id: " + id);
        }
        EventState currentState = existingEvent.getState();
        if (currentState != EventState.PENDING && currentState != EventState.CANCELED) {
            throw new ConflictException("Событие находится в состоянии, недоступном для редактирования: " + currentState);
        }
        EventUpdateUserRequestDto.StateAction action = eventDto.getStateAction();
        if (action != null) {
            if (action.equals(EventUpdateUserRequestDto.StateAction.SEND_TO_REVIEW)
                    && currentState.equals(EventState.CANCELED)) {
                existingEvent.setState(EventState.PENDING);
            } else if (action.equals(EventUpdateUserRequestDto.StateAction.CANCEL_REVIEW)
                    && currentState.equals(EventState.PENDING)) {
                existingEvent.setState(EventState.CANCELED);
            }
        }
        updateEventData(existingEvent, eventDto);
        Event savedEvent = eventRepository.save(existingEvent);
        Integer confirmedRequestsCount = requestRepository.getEventConfirmedRequestsCount(id);
        Integer views = viewsUtils.getEventViews(id, savedEvent.getPublishedOn());
        return eventMapper.toEventDto(savedEvent, confirmedRequestsCount, views);
    }

    public EventResponseDto updateEventByAdmin(Long id, EventUpdateAdminRequestDto eventDto) {
        Event existingEvent = checkEvent(id);
        checkEventDateForAdmin(eventDto.getEventDate(), existingEvent.getEventDate());
        EventUpdateAdminRequestDto.StateAction action = eventDto.getStateAction();
        if (action != null) {
            if (action.equals(EventUpdateAdminRequestDto.StateAction.PUBLISH_EVENT)) {
                if (!existingEvent.getState().equals(EventState.PENDING)) {
                    throw new ConflictException(
                            "Событие, которое вы пытаетесь опубликовать, не находится в состоянии ожидания публикации");
                }
                existingEvent.setState(EventState.PUBLISHED);
                existingEvent.setPublishedOn(LocalDateTime.now());
            } else if (action.equals(EventUpdateAdminRequestDto.StateAction.REJECT_EVENT)) {
                if (!existingEvent.getState().equals(EventState.PENDING)) {
                    throw new ConflictException(
                            "Событие, которое вы пытаетесь отклонить, не находится в состоянии ожидания публикации");
                }
                existingEvent.setState(EventState.CANCELED);
            }
        }
        updateEventData(existingEvent, eventDto);
        Event savedEvent = eventRepository.save(existingEvent);
        Integer confirmedRequestsCount = requestRepository.getEventConfirmedRequestsCount(id);
        Integer views = viewsUtils.getEventViews(id, savedEvent.getPublishedOn());
        return eventMapper.toEventDto(savedEvent, confirmedRequestsCount, views);
    }

    private Event checkEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id события: " + id));
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateException("Планируемая дата события должна быть не ранее чем через 2 часа");
        }
    }

    private void checkEventDateForAdmin(LocalDateTime newDate, LocalDateTime currentDate) {
        if (newDate != null) {
            if (newDate.minusHours(1).isBefore(LocalDateTime.now())) {
                throw new ValidateException(
                        "Новая дата начала события менее чем через час");
            }
        } else {
            if (currentDate.minusHours(1).isBefore(LocalDateTime.now())) {
                throw new ValidateException(
                        "Текущая дата начала события менее чем через час");
            }
        }
    }

    private void checkDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidateException("Дата начала поиска позже даты окончания");
        }
    }

    private void updateEventData(Event existingEvent, EventRequestDto eventDto) {
        LocalDateTime eventDate = eventDto.getEventDate();
        if (eventDate != null) {
            checkEventDate(eventDate);
            existingEvent.setEventDate(eventDate);
        }
        String annotation = eventDto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            existingEvent.setAnnotation(annotation);
        }
        Long categoryId = eventDto.getCategoryId();
        if (categoryId != null) {
            existingEvent.setCategory(Category.builder().id(categoryId).build());
        }
        String description = eventDto.getDescription();
        if (description != null && !description.isBlank()) {
            existingEvent.setDescription(description);
        }
        if (eventDto.getLocation() != null) {
            Double latitude = eventDto.getLocation().getLat();
            if (latitude != null) {
                existingEvent.setLatitude(latitude);
            }
            Double longitude = eventDto.getLocation().getLon();
            if (longitude != null) {
                existingEvent.setLongitude(longitude);
            }
        }
        Boolean paid = eventDto.getIsPaid();
        if (paid != null) {
            existingEvent.setIsPaid(paid);
        }
        Integer participantLimit = eventDto.getParticipantLimit();
        if (participantLimit != null) {
            existingEvent.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = eventDto.getIsModerationRequested();
        if (requestModeration != null) {
            existingEvent.setIsRequestModerationRequired(requestModeration);
        }
        String title = eventDto.getTitle();
        if (title != null && !title.isBlank()) {
            existingEvent.setTitle(title);
        }
    }
}
