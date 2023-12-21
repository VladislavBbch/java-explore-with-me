package ru.practicum.ewm.main.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserShortResponseDto {
    private Long id;
    private String name;
}
