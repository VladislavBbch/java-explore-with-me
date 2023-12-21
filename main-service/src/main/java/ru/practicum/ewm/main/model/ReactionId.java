package ru.practicum.ewm.main.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ReactionId implements Serializable {
    private Long userId;
    private Long eventId;
}
