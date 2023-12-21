package ru.practicum.ewm.main.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.dto.UserRequestDto;
import ru.practicum.ewm.main.dto.UserResponseDto;
import ru.practicum.ewm.main.dto.UserShortResponseDto;
import ru.practicum.ewm.main.model.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toUser(UserRequestDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public UserResponseDto toUserDto(User user, Double rating) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .rating(rating)
                .build();
    }

    public List<UserResponseDto> toUserDto(List<User> users, Map<Long, Double> ratingsByUserId) {
        return users.stream()
                .map(user -> toUserDto(
                        user,
                        ratingsByUserId.getOrDefault(user.getId(), 0.0)))
                .collect(Collectors.toList());
    }

    public UserShortResponseDto toUserShortDto(User user) {
        return UserShortResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
