package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.model.EventIdRequestCount;
import ru.practicum.ewm.main.model.Request;
import ru.practicum.ewm.main.model.RequestStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface RequestRepository extends JpaRepository<Request, Long> {
    default Integer getEventConfirmedRequestsCount(Long eventId) {
        if (eventId == null) {
            return 0;
        }
        return countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    default Map<Long, Integer> getConfirmedRequestsCountByEvents(List<Long> eventIds) {
        if (eventIds == null || !(eventIds.size() > 0)) {
            return Collections.emptyMap();
        }
        return countAllConfirmedByEventIdIn(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        EventIdRequestCount::getEventId,
                        EventIdRequestCount::getRequestCount
                ));
    }

    Request findByUserIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByUserId(Long userId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByStatus(RequestStatus status);

    Integer countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query(value = "select event_id as eventId, count(*) as requestCount " +
            "from requests " +
            "where event_id in (?1) " +
            "and status = 'CONFIRMED' " +
            "group by event_id ", nativeQuery = true)
    List<EventIdRequestCount> countAllConfirmedByEventIdIn(List<Long> eventIds);
}
