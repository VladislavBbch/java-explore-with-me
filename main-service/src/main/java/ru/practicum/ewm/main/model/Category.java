package ru.practicum.ewm.main.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "CATEGORIES")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
