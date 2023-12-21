package ru.practicum.ewm.main.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Subselect("select r1.event_id, wilson(coalesce(r1.positive, 0), coalesce(r2.negative, 0)) as rating " +
        "from (select event_id, count(*) as positive " +
        "      from reactions " +
        "      where is_positive = 'true' " +
        "      group by event_id) r1 " +
        "         full join (select event_id, count(*) as negative " +
        "               from reactions " +
        "               where is_positive = 'false' " +
        "               group by event_id) r2 " +
        "          on r1.event_id = r2.event_id") //left join events, coalesce(rating, 0)
@Synchronize("reactions")
@Getter
@Setter
public class EventsRatings {
    @Id
    private Long eventId;
    private Double rating;
}
