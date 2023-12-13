package ru.practicum.ewm.main.model;

import lombok.*;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Subselect("select event_id, count(*) as confirmed_request_count " +
        "from requests " +
        "where status = 'CONFIRMED' " +
        "group by event_id")
@Synchronize("requests")
@Getter
@Setter
public class EventsRequests { //confirmed requests count group by event id
    @Id
    private Long eventId;
    private Integer confirmedRequestCount;
}

/*necessary for get query with subselect join in QueryDSL:
    select *
    from events event0_
    left outer join
        (
            select event_id, count(*) as confirmed_request_count
            from requests
            where status = 'CONFIRMED'
            group by event_id
        ) eventsrequ1_
            on ( event0_.id=eventsrequ1_.event_id )
    where
        (
            event0_.participant_limit=?
            or event0_.participant_limit>=coalesce(eventsrequ1_.confirmed_request_count, ?)
        )
*/
