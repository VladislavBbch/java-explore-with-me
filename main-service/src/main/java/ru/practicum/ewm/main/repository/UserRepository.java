package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.model.User;

public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {
    default User checkUser(Long id) {
        return findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + id));
    }
}
