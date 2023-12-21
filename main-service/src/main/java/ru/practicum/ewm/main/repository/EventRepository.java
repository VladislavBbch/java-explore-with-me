package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    default Event checkEvent(Long id) {
        return findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id события: " + id));
    }

    List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);
}
