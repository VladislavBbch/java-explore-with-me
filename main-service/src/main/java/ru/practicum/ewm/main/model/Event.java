package ru.practicum.ewm.main.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "EVENTS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
    private String description;
    @Column(name = "EVENT_DATE")
    private LocalDateTime eventDate;
    private Double latitude;
    private Double longitude;
    @Column(name = "IS_PAID")
    private Boolean isPaid;
    @Column(name = "PARTICIPANT_LIMIT")
    private Integer participantLimit;
    @Column(name = "IS_REQUEST_MODERATION_REQUIRED")
    private Boolean isRequestModerationRequired;
    private String title;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INITIATOR_ID")
    private User initiator;
    @CreationTimestamp
    @Column(name = "CREATION_DATE")
    private LocalDateTime createdOn;
    @Column(name = "PUBLISH_DATE")
    private LocalDateTime publishedOn;
    @ManyToMany(mappedBy = "events", fetch = FetchType.LAZY)
    private Set<Compilation> compilations; //not used
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private Set<Reaction> reactions;
}
