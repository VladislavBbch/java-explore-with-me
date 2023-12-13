package ru.practicum.ewm.main.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "COMPILATIONS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "IS_PINNED")
    @ColumnDefault("false")
    private Boolean isPinned;
    private String title;
    @ManyToMany
    @JoinTable(
            name = "EVENTS_COMPILATIONS",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    private Set<Event> events;
}
