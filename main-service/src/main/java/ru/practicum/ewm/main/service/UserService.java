package ru.practicum.ewm.main.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.UserRequestDto;
import ru.practicum.ewm.main.dto.UserResponseDto;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.model.QUser;
import ru.practicum.ewm.main.repository.ReactionRepository;
import ru.practicum.ewm.main.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ReactionRepository reactionRepository;

    public UserResponseDto createUser(UserRequestDto userDto) {
        return userMapper.toUserDto(
                userRepository.save(
                        userMapper.toUser(userDto)), null);
    }

    public List<UserResponseDto> getUsers(List<Long> ids, Integer from, Integer size) {
        BooleanExpression query = QUser.user.id.isNotNull();
        if (ids.size() > 0) {
            query = query.and(QUser.user.id.in(ids));
        }
        Map<Long, Double> ratingsByUserId = reactionRepository.getUsersRating(ids);
        return userMapper.toUserDto(
                userRepository.findAll(query, PageRequestByElement.of(from, size)).toList(),
                ratingsByUserId);
    }

    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id пользователя: " + userId));
        userRepository.deleteById(userId);
    }
}
