package ru.practicum.ewm.main.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.dto.UserDto;
import ru.practicum.ewm.main.dto.UserShortDto;
import ru.practicum.ewm.main.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public List<UserDto> toUserDto(List<User> users) {
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
