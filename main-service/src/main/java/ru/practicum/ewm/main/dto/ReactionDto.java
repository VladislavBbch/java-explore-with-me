package ru.practicum.ewm.main.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ReactionDto {
    private Long userId;
    private Long eventId;
    private Boolean isPositive;
}
