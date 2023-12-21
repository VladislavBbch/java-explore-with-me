package ru.practicum.ewm.main.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "USERS")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Reaction> reactions;
}