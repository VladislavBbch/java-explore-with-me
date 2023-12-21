package ru.practicum.ewm.main.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private Double rating;
}
