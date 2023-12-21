package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
    default Double getEventRating(Long eventId) {
        if (eventId == null) {
            return 0.0;
        }
        List<RatingInfo> result = getEventRating(List.of(eventId));
        return result.size() > 0 ? result.get(0).getRating() : 0.0;
    }

    default Map<Long, Double> getEventsRating(List<Long> eventIds) {
        if (eventIds == null || !(eventIds.size() > 0)) {
            return Collections.emptyMap();
        }
        return getEventRating(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        RatingInfo::getId,
                        RatingInfo::getRating
                ));
    }

    default Double getUserRating(Long userId) {
        if (userId == null) {
            return 0.0;
        }
        return getUserRating(List.of(userId)).get(0).getRating();
    }

    default Map<Long, Double> getUsersRating(List<Long> userIds) {
        return getUserRating(userIds)
                .stream()
                .collect(Collectors.toMap(
                        RatingInfo::getId,
                        RatingInfo::getRating
                ));
    }

    @Query(value = "select case when r1.event_id is null then r2.event_id else r1.event_id end as id, " +
            "wilson(coalesce(r1.positive, 0), coalesce(r2.negative, 0)) as rating " +
            "from (select event_id, count(*) as positive " +
            "      from reactions " +
            "      where is_positive = 'true' " +
            "      and ((?1) is null or event_id in (select id from events where id in (?1))) " +
            "      group by event_id) r1 " +
            "full join (select event_id, count(*) as negative " +
            "      from reactions " +
            "      where is_positive = 'false' " +
            "      and ((?1) is null or event_id in (select id from events where id in (?1))) " +
            "      group by event_id) r2 " +
            "     on r1.event_id = r2.event_id " +
            "order by rating desc;", nativeQuery = true)
    List<RatingInfo> getEventRating(List<Long> eventIds);

    @Query(value = "select e.initiator_id as id, avg(wilson(coalesce(r.positive, 0), coalesce(r.negative, 0))) as rating " +
            "from " +
            "(select case when r1.event_id is null then r2.event_id else r1.event_id end as event_id, r1.positive, r2.negative " +
            "from (select event_id, count(*) as positive " +
            "      from reactions " +
            "      where is_positive = 'true' " +
            "      group by event_id) r1 " +
            "full join (select event_id, count(*) as negative " +
            "      from reactions " +
            "      where is_positive = 'false' " +
            "      group by event_id) r2 " +
            "     on r1.event_id = r2.event_id) r " +
            "join events e on r.event_id = e.id " +
            "where ((?1) is null or e.initiator_id in (select id from users where id in (?1))) " +
            "group by e.initiator_id " +
            "order by rating desc;", nativeQuery = true)
    List<RatingInfo> getUserRating(List<Long> userIds);
}
