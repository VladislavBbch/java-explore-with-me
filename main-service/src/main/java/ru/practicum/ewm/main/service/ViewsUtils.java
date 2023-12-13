package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.stats.client.StatisticClient;
import ru.practicum.ewm.stats.dto.StatisticResponseDto;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class ViewsUtils {
    private final StatisticClient statisticClient;

    private static final Integer GET_EVENT_BY_ID_URI_PARTS = 3;

    Integer getEventViews(Long eventId, LocalDateTime publishedOn) {
        if (eventId == null || publishedOn == null) {
            return 0;
        }
        return getViewsByEvents(List.of(eventId), publishedOn).getOrDefault(eventId, 0);
    }

    Map<Long, Integer> getViewsByEvents(List<Long> eventIds, Collection<Event> events) {
        LocalDateTime earliestPublishedOn = getEarliestPublishedOn(events);
        if (eventIds == null || !(eventIds.size() > 0) || earliestPublishedOn == null) {
            return Collections.emptyMap();
        }
        return getViewsByEvents(eventIds, earliestPublishedOn);
    }

    private LocalDateTime getEarliestPublishedOn(Collection<Event> events) {
        if (events == null || !(events.size() > 0)) {
            return null;
        }
        LocalDateTime earliestPublishedOn = null;
        for (Event event : events) {
            LocalDateTime eventPublishedOn = event.getPublishedOn();
            if (eventPublishedOn != null
                    && (earliestPublishedOn == null || eventPublishedOn.isBefore(earliestPublishedOn))) {
                earliestPublishedOn = eventPublishedOn;
            }
        }
        return earliestPublishedOn;
    }

    private Map<Long, Integer> getViewsByEvents(List<Long> eventIds, LocalDateTime earliestPublishedOn) {
        List<String> uris = eventIds.stream().map(id -> "/events/" + id).collect(toList());
        List<StatisticResponseDto> statistics = statisticClient.getStatistics(
                        earliestPublishedOn.minusMinutes(1),
                        LocalDateTime.now().plusMinutes(1),
                        uris,
                        true)
                .getBody();
        Map<Long, Integer> viewsCountByEventId = new HashMap<>();
        if (statistics != null && statistics.size() > 0) {
            for (StatisticResponseDto stat : statistics) {
                String uri = stat.getUri();
                if (uri.startsWith("/events/")) {
                    String[] uriParts = uri.split("/");
                    if (uriParts.length == GET_EVENT_BY_ID_URI_PARTS) {
                        viewsCountByEventId.put(Long.parseLong(uriParts[GET_EVENT_BY_ID_URI_PARTS - 1]), stat.getHits());
                    }
                }
            }
        }
        return viewsCountByEventId;
    }
}
