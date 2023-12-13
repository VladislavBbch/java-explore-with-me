package ru.practicum.ewm.stats.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class StatisticResponseDto {
    private String app;
    private String uri;
    private Integer hits;
}
