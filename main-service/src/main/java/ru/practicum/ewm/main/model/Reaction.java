package ru.practicum.ewm.main.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "REACTIONS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Reaction {
    @EmbeddedId
    private ReactionId id;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "USER_ID")
    private User user;
    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "EVENT_ID")
    private Event event;
    @Column(name = "IS_POSITIVE")
    private Boolean isPositive;
}
