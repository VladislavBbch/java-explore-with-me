package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.dto.EventRequestDto;
import ru.practicum.ewm.main.dto.EventResponseDto;
import ru.practicum.ewm.main.dto.EventShortResponseDto;
import ru.practicum.ewm.main.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public Event toEvent(Long userId, EventRequestDto requestDto) {
        return Event.builder()
                .annotation(requestDto.getAnnotation())
                .category(Category.builder().id(requestDto.getCategoryId()).build())
                .description(requestDto.getDescription())
                .eventDate(requestDto.getEventDate())
                .latitude(requestDto.getLocation().getLat())
                .longitude(requestDto.getLocation().getLon())
                .isPaid(requestDto.getIsPaid())
                .participantLimit(requestDto.getParticipantLimit())
                .isRequestModerationRequired(requestDto.getIsModerationRequested())
                .title(requestDto.getTitle())
                .initiator(User.builder().id(userId).build())
                .build();
    }

    public EventResponseDto toEventDto(Event event) {
        return toEventDto(event, null, null, null);
    }

    public EventResponseDto toEventDto(Event event, Integer confirmedRequestsCount, Integer views, Double rating) {
        return EventResponseDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(Location.builder()
                        .lat(event.getLatitude())
                        .lon(event.getLongitude()).build())
                .paid(event.getIsPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getIsRequestModerationRequired())
                .title(event.getTitle())
                .state(event.getState())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .confirmedRequests(confirmedRequestsCount)
                .views(views)
                .rating(rating)
                .build();
    }

    public List<EventResponseDto> toEventDto(List<Event> events,
                                             Map<Long, Integer> requestsCountByEventId,
                                             Map<Long, Integer> viewsByEventId,
                                             Map<Long, Double> ratingsByEventId) {
        return events.stream()
                .map(event -> toEventDto(
                        event,
                        requestsCountByEventId.getOrDefault(event.getId(), 0),
                        viewsByEventId.getOrDefault(event.getId(), 0),
                        ratingsByEventId.getOrDefault(event.getId(), 0.0)))
                .collect(Collectors.toList());
    }

    public EventShortResponseDto toEventShortDto(Event event, Integer confirmedRequestsCount, Integer views, Double rating) {
        return EventShortResponseDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .paid(event.getIsPaid())
                .title(event.getTitle())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequestsCount)
                .views(views)
                .rating(rating)
                .build();
    }

    public List<EventShortResponseDto> toEventShortDto(List<Event> events,
                                                       Map<Long, Integer> requestsCountByEventId,
                                                       Map<Long, Integer> viewsByEventId,
                                                       Map<Long, Double> ratingsByEventId) {
        return toEventShortDto(events, requestsCountByEventId, viewsByEventId, ratingsByEventId, false);
    }

    public List<EventShortResponseDto> toEventShortDto(List<Event> events,
                                                       Map<Long, Integer> requestsCountByEventId,
                                                       Map<Long, Integer> viewsByEventId,
                                                       Map<Long, Double> ratingsByEventId,
                                                       Boolean sortByViews) {
        List<EventShortResponseDto> result = events.stream()
                .map(event -> toEventShortDto(
                        event,
                        requestsCountByEventId.getOrDefault(event.getId(), 0),
                        viewsByEventId.getOrDefault(event.getId(), 0),
                        ratingsByEventId.getOrDefault(event.getId(), 0.0)))
                .collect(Collectors.toList());
        if (sortByViews) {
            result.sort((d1, d2) -> d2.getViews() - d1.getViews());
        }
        return result;
    }
}
