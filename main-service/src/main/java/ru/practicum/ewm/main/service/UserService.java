package ru.practicum.ewm.main.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.UserDto;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.model.QUser;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        return userMapper.toUserDto(
                userRepository.save(
                        userMapper.toUser(userDto)));
    }

    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        BooleanExpression query = QUser.user.id.isNotNull();
        if (ids.size() > 0) {
            query = query.and(QUser.user.id.in(ids));
        }
        return userMapper.toUserDto(
                userRepository.findAll(query, PageRequestByElement.of(from, size)).toList());
    }

    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
        userRepository.deleteById(userId);
    }
}
